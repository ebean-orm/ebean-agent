package io.ebean.enhance.common;

import io.ebean.enhance.asm.ClassReader;

/**
 * Reads class information as an alternative to using a ClassLoader.
 * <p>
 * Used because if annotation classes are not in the classpath they are silently
 * dropped from the class information. We are especially interested to know if
 * super classes are entities during enhancement.
 * </p>
 */
public class ClassMetaReader {

  private final ClassMetaCache metaCache;

  private final EnhanceContext enhanceContext;

  public ClassMetaReader(EnhanceContext enhanceContext, ClassMetaCache metaCache) {
    this.enhanceContext = enhanceContext;
    this.metaCache = metaCache;
  }

  public ClassMeta get(boolean readMethodAnnotations, String name, ClassLoader classLoader) throws ClassNotFoundException {
    return getWithCache(readMethodAnnotations, name, classLoader);
  }

  private ClassMeta getWithCache(boolean readMethodAnnotations, String name, ClassLoader classLoader) throws ClassNotFoundException {

    synchronized (metaCache) {
      ClassMeta meta = metaCache.get(name);
      if (meta == null) {
        meta = readFromResource(readMethodAnnotations, name, classLoader);
        if (meta != null) {
          if (meta.isCheckSuperClassForEntity()) {
            ClassMeta superMeta = getWithCache(readMethodAnnotations, meta.getSuperClassName(), classLoader);
            if (superMeta != null && superMeta.isEntity()) {
              meta.setSuperMeta(superMeta);
            }
          }
          metaCache.put(name, meta);
        }
      }
      return meta;
    }
  }

  private ClassMeta readFromResource(boolean readMethodAnnotations, String className, ClassLoader classLoader)
      throws ClassNotFoundException {

    byte[] classBytes = enhanceContext.getClassBytes(className, classLoader);
    if (classBytes == null){
      if (enhanceContext.isLog(2)) {
        enhanceContext.log(null, "Could not read meta data for class ["+className+"].");
      }
      return null;
    } else {
      if (enhanceContext.isLog(5)) {
        enhanceContext.log(className, "read ClassMeta");
      }
    }
    try {
      ClassReader cr = new ClassReader(classBytes);
      ClassMetaReaderVisitor ca = new ClassMetaReaderVisitor(readMethodAnnotations, enhanceContext);
      cr.accept(ca, ClassReader.SKIP_FRAMES + ClassReader.SKIP_DEBUG);
      return ca.getClassMeta();

    } catch (IllegalArgumentException e) {
      // try fallback for IDE partial compile
      ClassMeta classMeta = metaCache.getFallback(className);
      if (classMeta != null) {
        return classMeta;
      }
      throw new ClassNotFoundException("Error reading " + className + " bytes len:" + classBytes.length + " no fallback in " + metaCache.fallbackKeys());
    }
  }

}
