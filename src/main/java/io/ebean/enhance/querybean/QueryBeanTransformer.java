package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassReader;
import io.ebean.enhance.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Set;

/**
 * A Class file Transformer that enhances entity beans.
 * <p>
 * This is used as both a javaagent or via an ANT task (or other off line approach).
 * </p>
 */
public class QueryBeanTransformer implements ClassFileTransformer {

  private static final int FLAGS = ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS;

  public static void premain(String agentArgs, Instrumentation inst) {

    QueryBeanTransformer t = new QueryBeanTransformer(agentArgs, null, null);
    inst.addTransformer(t);
    if (t.getLogLevel() > 0) {
      System.out.println("premain loading Transformer with args:" + agentArgs);
    }
  }

  public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {

    QueryBeanTransformer t = new QueryBeanTransformer(agentArgs, null, null);
    inst.addTransformer(t);
    if (t.getLogLevel() > 0) {
      System.out.println("agentmain loading Transformer with args:" + agentArgs);
    }
  }

  private final QBEnhanceContext enhanceContext;

  /**
   * Construct using the default classBytesReader implementation.
   */
  public QueryBeanTransformer(String agentArgs, ClassLoader classLoader, Set<String> initialPackages) {
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
    }
    this.enhanceContext = new QBEnhanceContext(agentArgs, classLoader, initialPackages);
  }

  /**
   * Change the logout to something other than system out.
   */
  public void setMessageListener(MessageListener messageListener) {
    this.enhanceContext.setMessageListener(messageListener);
  }

  public void log(int level, String msg, String extra) {
    enhanceContext.log(level, msg, extra);
  }

  public int getLogLevel() {
    return enhanceContext.getLogLevel();
  }

  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

    try {
      // ignore JDK and JDBC classes etc
      if (enhanceContext.isIgnoreClass(className)) {
        enhanceContext.log(8, "ignore class ", className);
        return null;
      }

      enhanceContext.log(8, "look at ", className);
      return enhancement(loader, classfileBuffer);
     
    } catch (NoEnhancementRequiredException e) {
      // the class is an interface
      log(8, "No Enhancement required ",  e.getMessage());
      return null;
    }
  }

  /**
   * Perform enhancement.
   */
  private byte[] enhancement(ClassLoader classLoader, byte[] classfileBuffer) {

    ClassReader cr = new ClassReader(classfileBuffer);
    ClassWriter cw = new ClassWriter(FLAGS);//, classLoader);
    TypeQueryClassAdapter ca = null;//new TypeQueryClassAdapter(cw, enhanceContext);

    try {

      cr.accept(ca, ClassReader.EXPAND_FRAMES);
      if (ca.isLog(9)) {
        ca.log("... completed");
      }
      return cw.toByteArray();

    } catch (AlreadyEnhancedException e) {
      if (ca.isLog(1)) {
        ca.log("already enhanced");
      }
      return null;

    } catch (NoEnhancementRequiredException e) {
      if (ca.isLog(9)) {
        ca.log("... skipping, no enhancement required");
      }
      return null;
    }
  }

}
