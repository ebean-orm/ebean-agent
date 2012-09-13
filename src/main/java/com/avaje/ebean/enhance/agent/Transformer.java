package com.avaje.ebean.enhance.agent;

import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.ProtectionDomain;

import com.avaje.ebean.enhance.asm.ClassReader;
import com.avaje.ebean.enhance.asm.ClassWriter;

/**
 * A Class file Transformer that enhances entity beans.
 * <p>
 * This is used as both a javaagent or via an ANT task (or other off line
 * approach).
 * </p>
 */
public class Transformer implements ClassFileTransformer {

    public static void premain(String agentArgs, Instrumentation inst) {

        Transformer t = new Transformer("", agentArgs);
        inst.addTransformer(t);

        if (t.getLogLevel() > 0) {
            System.out.println("premain loading Transformer args:" + agentArgs);
        }
    }

    private static final int CLASS_WRITER_COMPUTEFLAGS = ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS;

    private final EnhanceContext enhanceContext;

    private boolean performDetect;
    private boolean transformTransactional;
    private boolean transformEntityBeans;

    public Transformer(String extraClassPath, String agentArgs) {
        this(parseClassPaths(extraClassPath), agentArgs);
    }

    public Transformer(URL[] extraClassPath, String agentArgs) {
        this(new ClassPathClassBytesReader(extraClassPath), agentArgs);
    }

    public Transformer(ClassBytesReader r, String agentArgs) {
        this.enhanceContext = new EnhanceContext(r, false, agentArgs);
        this.performDetect = enhanceContext.getPropertyBoolean("detect", true);
        this.transformTransactional = enhanceContext.getPropertyBoolean("transactional", true);
        this.transformEntityBeans = enhanceContext.getPropertyBoolean("entity", true);
    }

    /**
     * Override when you need transformation to occur with knowledge of other classes.
     * <p>
     * Note: Added to support Play framework.
     * </p>
     */
    protected ClassWriter createClassWriter() {
        return new ClassWriter(CLASS_WRITER_COMPUTEFLAGS);
    }

    /**
     * Change the logout to something other than system out.
     */
    public void setLogout(PrintStream logout) {
        this.enhanceContext.setLogout(logout);
    }

    public void log(int level, String msg) {
        enhanceContext.log(level, msg);
    }

    public int getLogLevel() {
        return enhanceContext.getLogLevel();
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        try {

            // ignore JDK and JDBC classes etc
            if (enhanceContext.isIgnoreClass(className)) {
                return null;
            }

            ClassAdapterDetectEnhancement detect = null;

            if (performDetect) {
                enhanceContext.log(5, "performing detection on " + className);
                detect = detect(loader, classfileBuffer);
            }

            if (detect == null) {
                // default only looks entity beans to enhance
                enhanceContext.log(1, "no detection so enhancing entity " + className);
                return entityEnhancement(loader, classfileBuffer);
            }

            if (transformEntityBeans && detect.isEntity()) {

                if (detect.isEnhancedEntity()) {
                    detect.log(1, "already enhanced entity");

                } else {
                    // 
                    detect.log(2, "performing entity transform");
                    return entityEnhancement(loader, classfileBuffer);
                }
            }

            if (transformTransactional && detect.isTransactional()) {

                if (detect.isEnhancedTransactional()) {
                    detect.log(1, "already enhanced transactional");

                } else {
                    detect.log(2, "performing transactional transform");
                    return transactionalEnhancement(loader, classfileBuffer);
                }
            }

            return null;

        } catch (NoEnhancementRequiredException e) {
            // the class is an interface
            log(8, "No Enhancement required " + e.getMessage());
            return null;

        } catch (Exception e) {
            // a safety net for unexpected errors
            // in the transformation
            enhanceContext.log(e);
            return null;
        }
    }
    
    /**
     * Perform entity bean enhancement.
     */
    private byte[] entityEnhancement(ClassLoader loader, byte[] classfileBuffer) {

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = createClassWriter();
        ClassAdpaterEntity ca = new ClassAdpaterEntity(cw, loader, enhanceContext);
        try {

            cr.accept(ca, 0);

            if (ca.isLog(1)) {
                ca.logEnhanced();
            }

            if (enhanceContext.isReadOnly()) {
                return null;

            } else {
                return cw.toByteArray();
            }

        } catch (AlreadyEnhancedException e) {
            if (ca.isLog(1)) {
                ca.log("already enhanced entity");
            }
            return null;

        } catch (NoEnhancementRequiredException e) {
            if (ca.isLog(2)) {
                ca.log("skipping... no enhancement required");
            }
            return null;
        }
    }

    /**
     * Perform transactional enhancement.
     */
    private byte[] transactionalEnhancement(ClassLoader loader, byte[] classfileBuffer) {

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = createClassWriter();
        ClassAdapterTransactional ca = new ClassAdapterTransactional(cw, loader, enhanceContext);

        try {

            cr.accept(ca, ClassReader.EXPAND_FRAMES);

            if (ca.isLog(1)) {
                ca.log("enhanced");
            }

            if (enhanceContext.isReadOnly()) {
                return null;

            } else {
                return cw.toByteArray();
            }

        } catch (AlreadyEnhancedException e) {
            if (ca.isLog(1)) {
                ca.log("already enhanced");
            }
            return null;

        } catch (NoEnhancementRequiredException e) {
            if (ca.isLog(0)) {
                ca.log("skipping... no enhancement required");
            }
            return null;
        }
    }

    /**
     * Helper method to split semi-colon separated class paths into a URL array.
     */
    public static URL[] parseClassPaths(String extraClassPath) {

        if (extraClassPath == null) {
            return new URL[0];
        }

        String[] stringPaths = extraClassPath.split(";");
        return UrlPathHelper.convertToUrl(stringPaths);
    }

    /**
     * Read the bytes quickly trying to detect if it needs entity or
     * transactional enhancement.
     */
    private ClassAdapterDetectEnhancement detect(ClassLoader classLoader, byte[] classfileBuffer) {

        ClassAdapterDetectEnhancement detect = new ClassAdapterDetectEnhancement(classLoader, enhanceContext);

        // skip what we can...
        ClassReader cr = new ClassReader(classfileBuffer);
        cr.accept(detect, ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);

        return detect;
    }
}
