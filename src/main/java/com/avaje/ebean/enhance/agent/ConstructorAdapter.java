package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.MethodAdapter;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Modify the constructor to additionally initialise the entityBeanIntercept
 * field.
 * 
 * <pre class="code">
 * // added into constructor
 * _ebean_intercept = new EntityBeanIntercept(this);
 * </pre>
 */
public class ConstructorAdapter extends MethodAdapter implements EnhanceConstants, Opcodes {

	private final ClassMeta meta;

	private final String className;

	private final String constructorDesc;

	private boolean constructorInitializationDone;

	public ConstructorAdapter(MethodVisitor mv, ClassMeta meta, String constructorDesc) {
		super(mv);
		this.meta = meta;
		this.className = meta.getClassName();
		this.constructorDesc = constructorDesc;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {

		super.visitMethodInsn(opcode, owner, name, desc);
		addInitialisationIfRequired(opcode, owner, name, desc);
	}

	/**
	 * Add initialisation of EntityBeanIntercept to constructor.
	 * 
	 * <pre>
	 * _ebean_intercept = new EntityBeanIntercept(this);
	 * </pre>
	 */
	public void addInitialisationIfRequired(int opcode, String owner, String name, String desc) {

		if (opcode == INVOKESPECIAL && name.equals("<init>") && desc.equals("()V")) {
			if (meta.isSuperClassEntity()) {
				if (meta.isLog(3)) {
					meta.log("... skipping intercept <init> ... handled by super class... CONSTRUCTOR:"
							+ constructorDesc);
				}
			} else if (owner.equals(meta.getClassName())) {
				if (meta.isLog(3)) {
					meta.log("... skipping intercept <init> ... handled by other constructor... CONSTRUCTOR:"
							+ constructorDesc);
				}
			} else if (owner.equals(meta.getSuperClassName())){
				if (meta.isLog(2)) {
					meta.log("... adding intercept <init> in CONSTRUCTOR:" + constructorDesc + " OWNER/SUPER:" + owner);
				}

				if (constructorInitializationDone) {
					// hopefully this is never called but put it in here to be
					// on the safe side.
					String msg = "Error in Enhancement. Only expecting to add <init> of intercept object"
							+ " once but it is trying to add it twice for " + meta.getClassName() + " CONSTRUCTOR:"
							+ constructorDesc+ " OWNER:" + owner;
					System.err.println(msg);

				} else {
					// add the initialisation of the intercept object
					super.visitVarInsn(ALOAD, 0);
					super.visitTypeInsn(NEW, C_INTERCEPT);
					super.visitInsn(DUP);
					super.visitVarInsn(ALOAD, 0);

					super.visitMethodInsn(INVOKESPECIAL, C_INTERCEPT, "<init>", "(Ljava/lang/Object;)V");
					super.visitFieldInsn(PUTFIELD, className, INTERCEPT_FIELD, EnhanceConstants.L_INTERCEPT);

					constructorInitializationDone = true;
				}
			} else {
				if (meta.isLog(3)) {
					meta.log("... skipping intercept <init> ... incorrect type "+owner);
				}				
			}
		}
	}
}
