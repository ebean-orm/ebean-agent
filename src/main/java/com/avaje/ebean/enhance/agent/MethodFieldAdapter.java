package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.AnnotationVisitor;
import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodAdapter;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Changes the method code from using PUTFIELD and GETFIELD to calling our
 * special field interception methods.
 * <p>
 * This should only be performed on non-transient methods. If a method has the
 * Transient annotation then it is not transformed in this way.
 * </p>
 */
public class MethodFieldAdapter extends MethodAdapter implements Opcodes {

	private final ClassMeta meta;

	private final String className;

	private final String methodDescription;

	private boolean transientAnnotation;

	public MethodFieldAdapter(MethodVisitor mv, ClassMeta meta, String methodDescription) {
		super(mv);
		this.meta = meta;
		this.className = meta.getClassName();
		this.methodDescription = methodDescription;
	}

	/**
	 * Checks for the javax/persistence/Transient annotation.
	 * <p>
	 * If this annotation is on the method then field interception is not
	 * applied (Aka the method is not transformed).
	 * </p>
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Ljavax/persistence/Transient;")) {
			transientAnnotation = true;
		}
		return super.visitAnnotation(desc, visible);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {

		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {

		super.visitMethodInsn(opcode, owner, name, desc);
	}

	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

		if (transientAnnotation) {
			// The whole method is left as is
			super.visitFieldInsn(opcode, owner, name, desc);
			return;
		}

		if (opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC){
			if (meta.isLog(3)) {
				meta.log(" ... info: skip static field "+owner+" "+name+" in "+methodDescription);
			}
			super.visitFieldInsn(opcode, owner, name, desc);
			return;
		}
		
		if (!meta.isFieldPersistent(name)) {
			if (meta.isLog(2)) {
				meta.log(" ... info: non-persistent field "+owner+" "+name+" in "+methodDescription);
			}
			super.visitFieldInsn(opcode, owner, name, desc);
			return;
		}

		// every basicField has a special set of field interception methods
		if (opcode == Opcodes.GETFIELD) {
			String methodName = "_ebean_get_" + name;
			String methodDesc = "()" + desc;
			if (meta.isLog(4)) {
				meta.log("GETFIELD method:" + methodDescription 
					+ " field:" + name + " > " + methodName + " "+ methodDesc);
			}
			super.visitMethodInsn(INVOKEVIRTUAL, className, methodName, methodDesc);

		} else if (opcode == Opcodes.PUTFIELD) {
			String methodName = "_ebean_set_" + name;
			String methodDesc = "(" + desc + ")V";
			if (meta.isLog(4)) {
				meta.log("PUTFIELD method:" + methodDescription 
					+ " field:" + name + " > " + methodName + " "+ methodDesc);
			}
			super.visitMethodInsn(INVOKEVIRTUAL, className, methodName, methodDesc);

		} else {
			meta.log("Warning adapting method:" + methodDescription 
				+ "; unexpected static access to a persistent field?? " + name
				+" opCode not GETFIELD or PUTFIELD??  opCode:"+opcode+"");
			
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}
}
