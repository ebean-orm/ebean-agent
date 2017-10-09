package io.ebean.enhance.transactional;

import io.ebean.enhance.common.AnnotationInfo;
import io.ebean.enhance.common.AnnotationInfoVisitor;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.AlreadyEnhancedException;
import io.ebean.enhance.common.EnhanceContext;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClassAdapter used to add transactional support.
 */
public class ClassAdapterTransactional extends ClassVisitor {

	private static final Logger logger = Logger.getLogger(ClassAdapterTransactional.class.getName());

	private final ArrayList<String> transactionalMethods = new ArrayList<>();

	private final EnhanceContext enhanceContext;

	private final ClassLoader classLoader;

	private ArrayList<ClassMeta> transactionalInterfaces = new ArrayList<>();

	/**
	 * Class level annotation information.
	 */
	private AnnotationInfo classAnnotationInfo;

	private String className;

	public ClassAdapterTransactional(ClassVisitor cv, ClassLoader classLoader, EnhanceContext context) {
		super(Opcodes.ASM5, cv);
		this.classLoader = classLoader;
		this.enhanceContext = context;
	}

	public String className() {
		return className;
	}

	public boolean isLog(int level) {
		return enhanceContext.isLog(level);
	}

	public void log(String msg) {
		enhanceContext.log(className, msg);
	}

  public AnnotationInfo getClassAnnotationInfo() {
    return classAnnotationInfo;
  }
  
	/**
	 * Returns Transactional information from a matching interface method.
	 * <p>
	 * Returns null if no matching (transactional) interface method was found.
	 * </p>
	 * 
	 * @param methodName
	 *            The method name
	 * @param methodDesc
	 *            The method description
	 */
	public AnnotationInfo getInterfaceTransactionalInfo(String methodName, String methodDesc) {

		AnnotationInfo interfaceAnnotationInfo = null;

		for (int i = 0; i < transactionalInterfaces.size(); i++) {
			ClassMeta interfaceMeta = transactionalInterfaces.get(i);
			AnnotationInfo ai = interfaceMeta.getInterfaceTransactionalInfo(methodName, methodDesc);
			if (ai != null) {
				if (interfaceAnnotationInfo != null) {
					String msg = "Error in [" + className + "] searching the transactional interfaces ["
							+ transactionalInterfaces + "] found more than one match for the transactional method:"
							+ methodName + " " + methodDesc;

					logger.log(Level.SEVERE, msg);

				} else {
					interfaceAnnotationInfo = ai;
					if (isLog(2)) {
						log("inherit transactional from interface [" + interfaceMeta + "] method[" + methodName + " "
								+ methodDesc + "]");
					}
				}
			}
		}

		return interfaceAnnotationInfo;
	}

	/**
	 * Visit the class with interfaces.
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

		className = name;

		// Note: interfaces can be an empty array but not null
		int n = 1 + interfaces.length;
		String[] newInterfaces = new String[n];
		for (int i = 0; i < interfaces.length; i++) {
			newInterfaces[i] = interfaces[i];
			if (newInterfaces[i].equals(EnhanceConstants.C_ENHANCEDTRANSACTIONAL)) {
				throw new AlreadyEnhancedException(name);
			}
			ClassMeta interfaceMeta = enhanceContext.getInterfaceMeta(newInterfaces[i], classLoader);
			if (interfaceMeta != null && interfaceMeta.isTransactional()) {
				// the interface was transactional. We gather its information
				// because our methods inherit that transactional configuration
				transactionalInterfaces.add(interfaceMeta);
				
				if (isLog(6)) {
					log(" implements tranactional interface " + interfaceMeta.getDescription());
				}
			}
		}

		// Add the EnhancedTransactional interface
		newInterfaces[newInterfaces.length - 1] = EnhanceConstants.C_ENHANCEDTRANSACTIONAL;

		super.visit(version, access, name, signature, superName, newInterfaces);
	}

	/**
	 * Visit class level annotations.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

		AnnotationVisitor av = super.visitAnnotation(desc, visible);

		if (desc.equals(EnhanceConstants.AVAJE_TRANSACTIONAL_ANNOTATION)) {
			// we have class level Transactional annotation
			// which will act as default for all methods in this class
			classAnnotationInfo = new AnnotationInfo(null);
			return new AnnotationInfoVisitor(null, classAnnotationInfo, av);

		} else {
			return av;
		}
	}

	/**
	 * Visit the methods specifically looking for method level transactional
	 * annotations.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("<init>") || name.equals("<clinit>")) {
			// not enhancing constructors at the moment
			return mv;
		}
		return new ScopeTransAdapter(this, mv, access, name, desc);
	}

	@Override
	public void visitEnd() {
		if (isLog(2)) {
			log("methods:" + transactionalMethods);
		}
		super.visitEnd();
	}

	void transactionalMethod(String methodName, String methodDesc, AnnotationInfo annoInfo) {

		transactionalMethods.add(methodName);

		if (isLog(4)) {
			log("method:" + methodName + " " + methodDesc + " transactional " + annoInfo);
		} else if (isLog(3)) {
			log("method:" + methodName);
		}
	}
}
