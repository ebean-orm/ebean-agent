package com.avaje.ebean.enhance.agent;

import java.util.ArrayList;
import java.util.HashSet;

import com.avaje.ebean.enhance.asm.AnnotationVisitor;
import com.avaje.ebean.enhance.asm.ClassAdapter;
import com.avaje.ebean.enhance.asm.EmptyVisitor;
import com.avaje.ebean.enhance.asm.FieldVisitor;
import com.avaje.ebean.enhance.asm.MethodAdapter;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * ClassAdapter used to detect if this class needs enhancement for entity or
 * transactional support.
 */
public class ClassAdapterDetectEnhancement extends ClassAdapter {

	private final ClassLoader classLoader;
	
	private final EnhanceContext enhanceContext;

	private final HashSet<String> classAnnotation = new HashSet<String>();

	private final ArrayList<DetectMethod> methods = new ArrayList<DetectMethod>();

	private String className;

	private boolean entity;

	private boolean entityInterface;

	private boolean entityField;

	private boolean transactional;
	
	private boolean enhancedTransactional;

	public ClassAdapterDetectEnhancement(ClassLoader classLoader, EnhanceContext context) {
		super(new EmptyVisitor());
		this.classLoader = classLoader;
		this.enhanceContext = context;
	}

	public boolean isEntityOrTransactional() {
		return entity || isTransactional();
	}

	public String getStatus() {
		String s = "class: " + className;
		if (isEntity()) {
			s += " entity:true  enhanced:" + entityField;
			s = "*" + s;

		} else if (isTransactional()) {
			s += " transactional:true  enhanced:" + enhancedTransactional;
			s = "*" + s;

		} else {
			s = " " + s;
		}
		return s;
	}

	public boolean isLog(int level) {
		return enhanceContext.isLog(level);
	}

	public void log(String msg) {
		enhanceContext.log(className, msg);
	}

	public void log(int level, String msg) {
		if (isLog(level)){
			log(msg);
		}
	}

	public boolean isEnhancedEntity() {
		return entityField;
	}

	public boolean isEnhancedTransactional() {
		return enhancedTransactional;
	}

	/**
	 * Return true if this is an entity bean or embeddable bean.
	 */
	public boolean isEntity() {
		return entity;
	}

	/**
	 * Return true if ANY method has the transactional annotation.
	 */
	public boolean isTransactional() {
		if (transactional){
			// implements transactional interface or
			// transactional at class level
			return transactional;
		}
		
		// check each method...
		for (int i = 0; i < methods.size(); i++) {
			DetectMethod m = methods.get(i);
			if (m.isTransactional()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Visit the class with interfaces.
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

		if ((access & Opcodes.ACC_INTERFACE) != 0){
			throw new NoEnhancementRequiredException(name+" is an Interface");
		}
		
		className = name;

		for (int i = 0; i < interfaces.length; i++) {

			if (interfaces[i].equals(EnhanceConstants.C_ENTITYBEAN)) {
				entityInterface = true;
				entity = true;

			} else if (interfaces[i].equals(EnhanceConstants.C_ENHANCEDTRANSACTIONAL)) {
				enhancedTransactional = true;
			
			} else {
				ClassMeta intefaceMeta = enhanceContext.getInterfaceMeta(interfaces[i], classLoader);
				if (intefaceMeta != null && intefaceMeta.isTransactional()) {
					// implements transactional interface 
					transactional = true;
					if (isLog(9)) {
						log("detected implements tranactional interface " + intefaceMeta);
					}
				}
			}
		}

		if (isLog(2)){
			log("interfaces:  entityInterface["+entityInterface+"] transactional["+enhancedTransactional+"]");					
		}
		
		super.visit(version, access, name, signature, superName, interfaces);
	}

	/**
	 * Visit class level annotations.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (isLog(8)){
			log("visitAnnotation "+desc);					
		}
		classAnnotation.add(desc);
		if (isEntityAnnotation(desc)){
			// entity, embeddable or mappedSuperclass
			if (isLog(5)){
				log("found entity annotation "+desc);					
			}	
			entity = true;
			
		} else if (desc.equals(EnhanceConstants.AVAJE_TRANSACTIONAL_ANNOTATION)) {
			// class level Transactional annotation
			if (isLog(5)){
				log("found transactional annotation "+desc);					
			}	
			transactional = true;
		}

		return super.visitAnnotation(desc, visible);
	}

	/**
	 * Return true if the annotation is for an Entity, Embeddable or MappedSuperclass.
	 */
	private boolean isEntityAnnotation(String desc) {
		
		if (desc.equals(EnhanceConstants.ENTITY_ANNOTATION)) {
			return true;
			
		} else if (desc.equals(EnhanceConstants.EMBEDDABLE_ANNOTATION)) {
			return true;
	
		} else if (desc.equals(EnhanceConstants.MAPPEDSUPERCLASS_ANNOTATION)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Return true if this is the enhancement marker field.
	 * <p>
	 * The existence of this field is used to confirm that the class has been
	 * enhanced (rather than solely relying on the EntityBean interface). 
	 * <p>
	 */
	private boolean isEbeanFieldMarker(String name, String desc, String signature) {
		
		if (name.equals(MarkerField._EBEAN_MARKER)){
			if (!desc.equals("Ljava/lang/String;")){
				String m = "Error: _EBEAN_MARKER field of wrong type? "+desc;
				log(m);
			}
			return true;
		}
		return false;
	}

	
	public FieldVisitor visitField(int access, String name, String desc, String signature,
			Object value) {

		if (isLog(8)){
			log("visitField "+name+" "+value);					
		}	
		
		if ((access & Opcodes.ACC_STATIC) != 0) {
			// static field...
			if (isEbeanFieldMarker(name, desc, signature)){
				entityField = true;
				if (isLog(1)){
					log("Found ebean marker field "+name+" "+value);					
				}				
			}
		}

		return super.visitField(access, name, desc, signature, value);
	}
	
	/**
	 * Visit the methods specifically looking for method level transactional
	 * annotations.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		if (isLog(9)){
			log("visitMethod "+name+" "+desc);					
		}	
		
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		DetectMethod dmv = new DetectMethod(mv);

		methods.add(dmv);
		return dmv;
	}

	/**
	 * Check methods for Transactional annotation.
	 */
	private static class DetectMethod extends MethodAdapter {

		boolean transactional;

		public DetectMethod(final MethodVisitor mv) {
			super(mv);
		}

		/**
		 * Return true if this method has the transaction annotation supplied.
		 */
		public boolean isTransactional() {
			return transactional;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (desc.equals(EnhanceConstants.AVAJE_TRANSACTIONAL_ANNOTATION)) {
				transactional = true;
			}
			return super.visitAnnotation(desc, visible);
		}

	}
}
