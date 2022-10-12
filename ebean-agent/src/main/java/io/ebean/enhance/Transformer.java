package io.ebean.enhance;

import io.ebean.enhance.asm.ClassReader;
import io.ebean.enhance.asm.ClassWriter;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.AgentManifest;
import io.ebean.enhance.common.AlreadyEnhancedException;
import io.ebean.enhance.common.ClassBytesReader;
import io.ebean.enhance.common.ClassWriterWithoutClassLoading;
import io.ebean.enhance.common.CommonSuperUnresolved;
import io.ebean.enhance.common.DetectEnhancement;
import io.ebean.enhance.common.EnhanceContext;
import io.ebean.enhance.common.NoEnhancementRequiredException;
import io.ebean.enhance.common.TransformRequest;
import io.ebean.enhance.common.UrlPathHelper;
import io.ebean.enhance.entity.ClassAdapterEntity;
import io.ebean.enhance.entity.ClassPathClassBytesReader;
import io.ebean.enhance.entity.MessageOutput;
import io.ebean.enhance.querybean.TypeQueryClassAdapter;
import io.ebean.enhance.transactional.ClassAdapterTransactional;
import org.avaje.agentloader.AgentLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A Class file Transformer that performs Ebean enhancement of entity beans,
 * transactional methods and query bean enhancement.
 * <p>
 * This is used as both a java agent or via Maven and Gradle plugins etc.
 * </p>
 */
public class Transformer implements ClassFileTransformer {

  public static final int EBEAN_ASM_VERSION = Opcodes.ASM9;
  private static String version;

  /**
   * Return the version of the ebean-agent or "unknown" if the version can not be determined.
   */
  public static synchronized String getVersion() {
    if (version == null) {
      try (InputStream in = Transformer.class.getResourceAsStream("/META-INF/maven/io.ebean/ebean-agent/pom.properties")) {
        if (in != null) {
          Properties prop = new Properties();
          prop.load(in);
          version = prop.getProperty("version");
        }
      } catch (IOException e) {
        System.err.println("Could not determine ebean-agent version: " + e.getMessage());
      }
      if (version == null) {
        version = "unknown";
      }
    }
    return version;
  }

  public static void agentmain(String agentArgs, Instrumentation inst) {
    premain(agentArgs, inst);
  }

  public static void premain(String agentArgs, Instrumentation inst) {
    instrumentation = inst;
    transformer = new Transformer(null, agentArgs);
    inst.addTransformer(transformer);
  }

  private static Instrumentation instrumentation;
  private static Transformer transformer;

  private final EnhanceContext enhanceContext;
  private final List<CommonSuperUnresolved> unresolved = new ArrayList<>();
  private boolean keepUnresolved;

  public Transformer(ClassLoader classLoader, String agentArgs) {
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
    }
    ClassBytesReader reader = new ClassPathClassBytesReader(null);
    AgentManifest manifest = new AgentManifest(classLoader);
    this.enhanceContext = new EnhanceContext(reader, agentArgs, manifest);
  }

  /**
   * Create with an EnhancementContext (for IDE Plugins mainly)
   */
  public Transformer(EnhanceContext enhanceContext) {
    this.enhanceContext = enhanceContext;
  }

  /**
   * Create a transformer for entity bean enhancement and transactional method enhancement.
   *
   * @param bytesReader reads resources from class path for related inheritance and interfaces
   * @param agentArgs   command line arguments for debug level etc
   */
  public Transformer(ClassBytesReader bytesReader, String agentArgs, AgentManifest manifest) {
    this.enhanceContext = new EnhanceContext(bytesReader, agentArgs, manifest);
  }

  /**
   * Return the Instrumentation instance.
   */
  public static Instrumentation instrumentation() {
    verifyInitialization();
    return instrumentation;
  }

  /**
   * Return the Transformer instance.
   */
  public static Transformer get() {
    verifyInitialization();
    return transformer;
  }

  /**
   * Use agent loader if necessary to initialise the transformer.
   */
  public static void verifyInitialization() {
    if (instrumentation == null) {
      if (!AgentLoader.loadAgentFromClasspath("ebean-agent", "debug=0")) {
        throw new IllegalStateException("ebean-agent not found in classpath - not dynamically loaded");
      }
    }
  }

  /**
   * Set this to keep and report unresolved explicitly.
   */
  public void setKeepUnresolved() {
    this.keepUnresolved = true;
  }

  /**
   * Change the logout to something other than system out.
   */
  public void setLogout(MessageOutput logout) {
    this.enhanceContext.setLogout(logout);
  }

  public void log(int level, String msg) {
    log(level, null, msg);
  }

  private void log(int level, String className, String msg) {
    enhanceContext.log(level, className, msg);
  }

  public int getLogLevel() {
    return enhanceContext.logLevel();
  }

  public EnhanceContext getEnhanceContext() {
    return enhanceContext;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    try {
      enhanceContext.withClassLoader(loader);
      // ignore JDK and JDBC classes etc
      if (enhanceContext.isIgnoreClass(className) || isQueryBeanCompanion(className, loader)) {
        log(9, className, "ignore class");
        return null;
      }
      TransformRequest request = new TransformRequest(className, classfileBuffer);
      if (enhanceContext.detectEntityTransactionalEnhancement(className)) {
        enhanceEntityAndTransactional(loader, request);
      }
      if (enhanceContext.detectQueryBeanEnhancement(className)) {
        enhanceQueryBean(loader, request);
      }
      if (request.isEnhanced()) {
        return request.getBytes();
      }

      log(9, className, "no enhancement on class");
      return null;

    } catch (NoEnhancementRequiredException e) {
      // the class is an interface
      log(8, className, "No Enhancement required " + e.getMessage());
      return null;
    } catch (IllegalArgumentException | IllegalStateException e) {
      log(2, className, "No enhancement on class due to " + e);
      return null;
    } catch (Exception e) {
      if (enhanceContext.isThrowOnError()) {
        throw new IllegalStateException(e);
      }
      enhanceContext.log(className, "Error during transform " + e);
      enhanceContext.log(e);
      return null;
    } finally {
      logUnresolvedCommonSuper(className);
    }
  }

  private boolean isQueryBeanCompanion(String className, ClassLoader classLoader) {
    return className.endsWith("$Companion") && enhanceContext.isQueryBean(className, classLoader);
  }

  /**
   * Perform entity and transactional enhancement.
   */
  private void enhanceEntityAndTransactional(ClassLoader loader, TransformRequest request) {
    try {
      DetectEnhancement detect = detect(loader, request.getBytes());
      if (detect.isEntity()) {
        if (detect.isEnhancedEntity()) {
          detect.log(3, "already enhanced entity");
        } else {
          entityEnhancement(loader, request);
        }
      }
      if (enhanceContext.isEnableProfileLocation() || detect.isTransactional()) {
        if (detect.isEnhancedTransactional()) {
          detect.log(3, "already enhanced transactional");
        } else {
          transactionalEnhancement(loader, request);
        }
      }
    } catch (NoEnhancementRequiredException e) {
      log(8, request.getClassName(), "No entity or transactional enhancement required " + e.getMessage());
    }
  }

  /**
   * Log and common superclass classpath issues that defaulted to Object.
   */
  private void logUnresolvedCommonSuper(String className) {
    if (!keepUnresolved && !unresolved.isEmpty()) {
      for (CommonSuperUnresolved commonUnresolved : unresolved) {
        log(0, className, commonUnresolved.getMessage());
      }
      unresolved.clear();
    }
  }

  /**
   * Return the list of unresolved common superclass issues. This should be cleared
   * after each use and can only be used with {@link #setKeepUnresolved()}.
   */
  public List<CommonSuperUnresolved> getUnresolved() {
    return unresolved;
  }

  /**
   * Perform entity bean enhancement.
   */
  private void entityEnhancement(ClassLoader loader, TransformRequest request) {
    ClassReader cr = new ClassReader(request.getBytes());
    ClassWriterWithoutClassLoading cw = new ClassWriterWithoutClassLoading(ClassWriter.COMPUTE_FRAMES, loader);
    ClassAdapterEntity ca = new ClassAdapterEntity(cw, loader, enhanceContext);
    try {
      cr.accept(ca, ClassReader.EXPAND_FRAMES);
      if (ca.isLog(2)) {
        ca.logEnhanced();
        unresolved.addAll(cw.getUnresolved());
      }

      request.enhancedEntity(cw.toByteArray());

    } catch (AlreadyEnhancedException e) {
      if (ca.isLog(3)) {
        ca.log("already enhanced entity");
      }
      request.enhancedEntity(null);
    } catch (NoEnhancementRequiredException e) {
      if (ca.isLog(4)) {
        ca.log("skipped entity enhancement");
      }
    }
  }

  /**
   * Perform transactional enhancement and Finder profileLocation enhancement.
   */
  private void transactionalEnhancement(ClassLoader loader, TransformRequest request) {
    ClassReader cr = new ClassReader(request.getBytes());
    ClassWriterWithoutClassLoading cw = new ClassWriterWithoutClassLoading(ClassWriter.COMPUTE_FRAMES, loader);
    ClassAdapterTransactional ca = new ClassAdapterTransactional(cw, loader, enhanceContext);
    try {
      cr.accept(ca, ClassReader.EXPAND_FRAMES);
      if (ca.isLog(2)) {
        ca.logEnhanced();
      }

      request.enhancedTransactional(cw.toByteArray());

    } catch (AlreadyEnhancedException e) {
      if (ca.isLog(3)) {
        ca.log("already transactional enhanced");
      }
    } catch (NoEnhancementRequiredException e) {
      if (ca.isLog(4)) {
        ca.log("skipped transactional enhancement");
      }
    } finally {
      unresolved.addAll(cw.getUnresolved());
    }
  }

  /**
   * Perform enhancement.
   */
  private void enhanceQueryBean(ClassLoader loader, TransformRequest request) {
    ClassReader cr = new ClassReader(request.getBytes());
    ClassWriterWithoutClassLoading cw = new ClassWriterWithoutClassLoading(ClassWriter.COMPUTE_FRAMES, loader);
    TypeQueryClassAdapter ca = new TypeQueryClassAdapter(cw, enhanceContext, loader);
    try {
      cr.accept(ca, ClassReader.EXPAND_FRAMES);
      request.enhancedQueryBean(cw.toByteArray());
    } catch (AlreadyEnhancedException e) {
      if (ca.isLog(3)) {
        ca.log("already query bean enhanced");
      }
    } catch (NoEnhancementRequiredException e) {
      if (ca.isLog(4)) {
        ca.log("skipped query bean enhancement");
      }
    } finally {
      unresolved.addAll(cw.getUnresolved());
    }
  }

  /**
   * Helper method to split semi-colon separated class paths into a URL array.
   */
  public static URL[] parseClassPaths(String extraClassPath) {
    if (extraClassPath == null) {
      return new URL[0];
    }
    return UrlPathHelper.convertToUrl(extraClassPath.split(";"));
  }

  /**
   * Read the bytes quickly trying to detect if it needs entity or transactional
   * enhancement.
   */
  private DetectEnhancement detect(ClassLoader classLoader, byte[] classfileBuffer) {
    DetectEnhancement detect = new DetectEnhancement(classLoader, enhanceContext);
    ClassReader cr = new ClassReader(classfileBuffer);
    cr.accept(detect, ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
    return detect;
  }
}
