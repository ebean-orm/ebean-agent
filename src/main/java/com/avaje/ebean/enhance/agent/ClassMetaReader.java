package com.avaje.ebean.enhance.agent;

import java.util.HashMap;
import java.util.Map;

import com.avaje.ebean.enhance.asm.ClassReader;

/**
 * Reads class information as an alternative to using a ClassLoader.
 * <p>
 * Used because if annotation classes are not in the classpath they are silently
 * dropped from the class information. We are especially interested to know if
 * super classes are entities during enhancement.
 * </p>
 */
public class ClassMetaReader {

	private Map<String, ClassMeta> cache = new HashMap<String, ClassMeta>();

	private final EnhanceContext enhanceContext;

	public ClassMetaReader(EnhanceContext enhanceContext) {
		this.enhanceContext = enhanceContext;
	}

	public ClassMeta get(boolean readMethodAnnotations, String name, ClassLoader classLoader) throws ClassNotFoundException {
		return getWithCache(readMethodAnnotations, name, classLoader);
	}

	private ClassMeta getWithCache(boolean readMethodAnnotations, String name, ClassLoader classLoader)
			throws ClassNotFoundException {
		
		synchronized (cache) {
			ClassMeta meta = cache.get(name);
			if (meta == null) {
				meta = readFromResource(readMethodAnnotations, name, classLoader);
				if (meta != null) {
					if (meta.isCheckSuperClassForEntity()) {
						ClassMeta superMeta = getWithCache(readMethodAnnotations, meta.getSuperClassName(), classLoader);
						if (superMeta != null && superMeta.isEntity()) {
							meta.setSuperMeta(superMeta);
						}
					}
					cache.put(name, meta);
				}
			}
			return meta;
		}
	}

	private ClassMeta readFromResource(boolean readMethodAnnotations, String className, ClassLoader classLoader)
			throws ClassNotFoundException {

			
		byte[] classBytes = enhanceContext.getClassBytes(className, classLoader);
		if (classBytes == null){
		  if (enhanceContext.isLog(1)) {
	      enhanceContext.log(null, "Could not read meta data for class ["+className+"].");		    
		  }
			return null;
		} else {
			if (enhanceContext.isLog(3)) {
				enhanceContext.log(className, "read ClassMeta");
			}
		}
		ClassReader cr = new ClassReader(classBytes);
		ClassMetaReaderVisitor ca = new ClassMetaReaderVisitor(readMethodAnnotations, enhanceContext);

		cr.accept(ca, 0);

		return ca.getClassMeta();
	}

}
