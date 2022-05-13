package io.ebean.enhance.common;

import io.ebean.enhance.Transformer;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.entity.MessageOutput;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.ebean.enhance.asm.Opcodes.INVOKEINTERFACE;
import static io.ebean.enhance.asm.Opcodes.INVOKEVIRTUAL;
import static io.ebean.enhance.common.EnhanceConstants.C_INTERCEPT_I;
import static io.ebean.enhance.common.EnhanceConstants.C_INTERCEPT_RW;

/**
 * Used to hold meta data, arguments and log levels for the enhancement.
 */
public class EnhanceContext {

  private static final Logger logger = Logger.getLogger(EnhanceContext.class.getName());

  private final AgentManifest manifest;
  private final IgnoreClassHelper ignoreClassHelper;
  private final Map<String, String> agentArgsMap;
  private final ClassMetaReader reader;
  private final ClassBytesReader classBytesReader;
  private MessageOutput logout;
  private int logLevel;
  private final HashMap<String, ClassMeta> map = new HashMap<>();
  private final FilterEntityTransactional filterEntityTransactional;
  private final FilterQueryBean filterQueryBean;
  private boolean throwOnError;
  private final boolean enableProfileLocation;
  private final int accPublic;
  private final int accProtected;
  private final int accPrivate;
  private final int ebeanInternalVersion;
  private SummaryInfo summaryInfo;

  public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs, AgentManifest manifest) {
    this(classBytesReader, agentArgs, manifest, new ClassMetaCache());
  }

  /**
   * Construct a context for enhancement.
   */
  public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs, AgentManifest manifest, ClassMetaCache metaCache) {
    this.manifest = manifest;
    this.enableProfileLocation = manifest.isEnableProfileLocation();
    this.accPublic = manifest.accPublic();
    this.accProtected = manifest.accProtected();
    this.accPrivate = manifest.accPrivate();
    this.agentArgsMap = ArgParser.parse(agentArgs);
    this.ebeanInternalVersion = versionOf(manifest);
    this.filterEntityTransactional = new FilterEntityTransactional(manifest);
    this.filterQueryBean = new FilterQueryBean(manifest);
    this.ignoreClassHelper = new IgnoreClassHelper();
    this.logout = new SysoutMessageOutput(System.out);
    this.classBytesReader = classBytesReader;
    this.reader = new ClassMetaReader(this, metaCache);

    if (manifest.getDebugLevel() > -1) {
      logLevel = manifest.getDebugLevel();
    }
    String debugValue = agentArgsMap.get("debug");
    if (debugValue != null) {
      try {
        logLevel = Integer.parseInt(debugValue);
      } catch (NumberFormatException e) {
        logger.log(Level.WARNING, "Agent debug argument [" + debugValue + "] is not an int?");
      }
    }
    if (logLevel > 0 || getPropertyBoolean("printversion", false)) {
      System.out.println("ebean-agent version:" + Transformer.getVersion() + " enhancement:" + ebeanInternalVersion + " resources:" + manifest.getLoadedResources());
    }
  }

  private int versionOf(AgentManifest manifest) {
    String ver = agentArgsMap.get("version");
    if (ver != null) {
      return Integer.parseInt(ver);
    }
    return manifest.getEnhancementVersion();
  }

  public void withClassLoader(ClassLoader loader) {
    if (manifest.readManifest(loader)) {
      if (logLevel > 1) {
        log(null, "loaded entity packages: " + manifest.getEntityPackages());
      }
    }
  }

  public void setLogLevel(int logLevel) {
    this.logLevel = logLevel;
  }

  /**
   * Return the summary of the packages controlling enhancement.
   */
  public String getPackagesSummary() {
    return "packages entity:" + getEntityPackages()
      + "  transactional:" + getTransactionalPackages()
      + "  querybean:" + getQuerybeanPackages()
      + "  profileLocation:" + enableProfileLocation
      + "  version:" + ebeanInternalVersion;
  }

  public Set<String> getEntityPackages() {
    return manifest.getEntityPackages();
  }

  public Set<String> getTransactionalPackages() {
    return manifest.getTransactionalPackages();
  }

  public Set<String> getQuerybeanPackages() {
    return manifest.getQuerybeanPackages();
  }

  public byte[] getClassBytes(String className, ClassLoader classLoader) {
    return classBytesReader.getClassBytes(className, classLoader);
  }

  /**
   * Return true if the owner class is a type query bean.
   * <p>
   * If true typically means the caller needs to change GETFIELD calls to instead invoke the generated
   * 'property access' methods.
   * </p>
   */
  public boolean isQueryBean(String owner, ClassLoader classLoader) {
    if (manifest.isDetectQueryBean(owner)) {
      try {
        final ClassMeta classMeta = reader.get(true, owner, classLoader);
        if (classMeta == null) {
          // For Gradle Kotlin KAPT the generate query bean bytecode
          // isn't available to the classLoader. Just returning true.
          return true;
        }
        return classMeta.isQueryBean();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }

  /**
   * Return a value from the entity arguments using its key.
   */
  private String getProperty(String key) {
    return agentArgsMap.get(key.toLowerCase());
  }

  private boolean getPropertyBoolean(String key, boolean defaultValue) {
    String s = getProperty(key);
    if (s == null) {
      return defaultValue;
    } else {
      return s.trim().equalsIgnoreCase("true");
    }
  }

  /**
   * Return true if profile location enhancement is on.
   */
  public boolean isEnableProfileLocation() {
    return enableProfileLocation;
  }

  /**
   * Return true if this class should be scanned for transactional enhancement.
   */
  public boolean detectEntityTransactionalEnhancement(String className) {
    return filterEntityTransactional.detectEnhancement(className);
  }

  /**
   * Return true if this class should be scanned for query bean enhancement.
   */
  public boolean detectQueryBeanEnhancement(String className) {
    return filterQueryBean.detectEnhancement(className);
  }

  /**
   * Return true if this class should be ignored. That is JDK classes and
   * known libraries JDBC drivers etc can be skipped.
   */
  public boolean isIgnoreClass(String className) {
    return ignoreClassHelper.isIgnoreClass(className);
  }

  /**
   * Change the logout to something other than system out.
   */
  public void setLogout(MessageOutput logout) {
    this.logout = logout;
  }

  /**
   * Create a new meta object for enhancing a class.
   */
  public ClassMeta createClassMeta() {
    return new ClassMeta(this, logLevel, logout);
  }

  /**
   * Read the class meta data for a super class.
   * <p>
   * Typically used to read meta data for inheritance hierarchy.
   * </p>
   */
  public ClassMeta getSuperMeta(String superClassName, ClassLoader classLoader) {
    try {
      if (isIgnoreClass(superClassName)) {
        return null;
      }
      return reader.get(false, superClassName, classLoader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Read the class meta data for an interface.
   * <p>
   * Typically used to check the interface to see if it is transactional.
   * </p>
   */
  public ClassMeta getInterfaceMeta(String interfaceClassName, ClassLoader classLoader) {
    try {
      if (isIgnoreClass(interfaceClassName)) {
        return null;
      }
      return reader.get(true, interfaceClassName, classLoader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void addClassMeta(ClassMeta meta) {
    map.put(meta.getClassName(), meta);
  }

  public ClassMeta get(String className) {
    return map.get(className);
  }

  /**
   * Log some debug output.
   */
  public void log(int level, String className, String msg) {
    if (logLevel >= level) {
      log(className, msg);
    }
  }

  public void log(String className, String msg) {
    if (className != null) {
      msg = "cls: " + className + "  msg: " + msg;
    }
    logout.println("ebean-enhance> " + msg);
  }

  public boolean isLog(int level) {
    return logLevel >= level;
  }

  /**
   * Log an error.
   */
  public void log(Throwable e) {
    e.printStackTrace(
      new PrintStream(new ByteArrayOutputStream()) {
        @Override
        public void print(String message) {
          logout.println(message);
        }

        @Override
        public void println(String message) {
          logout.println(message);
        }
      });
  }


  /**
   * Return the log level.
   */
  public int getLogLevel() {
    return logLevel;
  }

  /**
   * Return true if internal ebean fields in entity classes should be transient.
   */
  public boolean isTransientInternalFields() {
    return manifest.isTransientInternalFields();
  }

  /**
   * Return true if we should add null checking on *ToMany fields.
   * <p>
   * On getting a many that is null Ebean will create an empty List, Set or Map. If it is a
   * ManyToMany it will turn on Modify listening.
   * </p>
   */
  public boolean isCheckNullManyFields() {
    return manifest.isCheckNullManyFields();
  }

  /**
   * Return true if transform should throw exception rather than log and return null.
   */
  public boolean isThrowOnError() {
    return throwOnError;
  }

  /**
   * Set to true if you want transform to throw exceptions rather than return null.
   */
  public void setThrowOnError(boolean throwOnError) {
    this.throwOnError = throwOnError;
  }

  /**
   * Turn on the summary collection of the enhancement.
   */
  public void collectSummary() {
    this.summaryInfo = new SummaryInfo(manifest.getLoadedResources());
  }

  /**
   * Add the transactional enhanced class to summary information.
   */
  public void summaryTransactional(String className) {
    if (summaryInfo != null) {
      summaryInfo.addTransactional(className);
    }
  }

  /**
   * Add the entity enhanced class to summary information.
   */
  public void summaryEntity(String className) {
    if (summaryInfo != null) {
      summaryInfo.addEntity(className);
    }
  }

  /**
   * Add the query bean enhanced class to summary information.
   */
  public void summaryQueryBean(String className) {
    if (summaryInfo != null) {
      summaryInfo.addQueryBean(className);
    }
  }

  /**
   * Add the query bean caller enhanced class to summary information.
   */
  public void summaryQueryBeanCaller(String className) {
    if (summaryInfo != null) {
      summaryInfo.addQueryBeanCaller(className);
    }
  }

  /**
   * Return the summary of the enhancement.
   * <p>
   * Note that <code>collectSummary()</code> must be called in order for summary
   * information to be collected and returned here.
   */
  public SummaryInfo getSummaryInfo() {
    return summaryInfo.prepare();
  }

  public int accPublic() {
    return accPublic;
  }

  public int accProtected() {
    return accProtected;
  }

  public int accPrivate() {
    return accPrivate;
  }

  public boolean isToManyGetField() {
    return ebeanInternalVersion > 128;
  }

  public boolean isEnhancedToString() {
    return ebeanInternalVersion > 132;
  }

  public String interceptNew() {
    return ebeanInternalVersion >= 140 ? C_INTERCEPT_RW : C_INTERCEPT_I;
  }

  public void visitMethodInsnIntercept(MethodVisitor mv, String name, String desc) {
    mv.visitMethodInsn(interceptInvoke(), C_INTERCEPT_I, name, desc, interceptIface());
  }

  private int interceptInvoke() {
    return ebeanInternalVersion >= 140 ? INVOKEINTERFACE : INVOKEVIRTUAL;
  }

  private boolean interceptIface() {
    return ebeanInternalVersion >= 140;
  }

  public boolean interceptAddReadOnly() {
    return ebeanInternalVersion >= 141;
  }
}
