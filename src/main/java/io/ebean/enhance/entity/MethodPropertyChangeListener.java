package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Used to detect if a class has been enhanced.
 * <p>
 * Moved to use this over just relying on the existence of the EntityBean interface
 * to make the enhancement more robust.
 * </p>
 */
public class MethodPropertyChangeListener implements Opcodes, EnhanceConstants {

	/**
	 * Add the addPropertyChangeListener and removePropertyChangeListener methods.
	 */
	public static void addMethod(ClassVisitor cv, ClassMeta classMeta) {
		addAddListenerMethod(cv, classMeta);
		addAddPropertyListenerMethod(cv, classMeta);
		addRemoveListenerMethod(cv, classMeta);
		addRemovePropertyListenerMethod(cv, classMeta);
	}
	
	private static boolean alreadyExisting(ClassMeta classMeta, String method, String desc) {

		if (classMeta.isExistingMethod(method, desc)){
			if (classMeta.isLog(2)){
				classMeta.log("Existing method... "+method+desc+"  - not adding Ebean's implementation");
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Generate the addPropertyChangeListener method.
	 * 
	 * <pre>
	 *   public void addPropertyChangeListener(PropertyChangeListener listener) {
	 *     _ebean_intercept.addPropertyChangeListener(listener);
	 *   }
	 * </pre>
	 */
	private static void addAddListenerMethod(ClassVisitor cv, ClassMeta classMeta) {
	
		String desc = "(Ljava/beans/PropertyChangeListener;)V";
	
		if (alreadyExisting(classMeta, "addPropertyChangeListener", desc)){
			return;
		}
		
		String className = classMeta.getClassName();
		
		MethodVisitor mv;

		mv = cv.visitMethod(ACC_PUBLIC, "addPropertyChangeListener", desc, null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "addPropertyChangeListener", "(Ljava/beans/PropertyChangeListener;)V", false);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(2, l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L"+className+";", null, l0, l2, 0);
		mv.visitLocalVariable("listener", "Ljava/beans/PropertyChangeListener;", null, l0, l2, 1);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private static void addAddPropertyListenerMethod(ClassVisitor cv, ClassMeta classMeta) {
		
		String desc = "(Ljava/lang/String;Ljava/beans/PropertyChangeListener;)V";
		
		if (alreadyExisting(classMeta, "addPropertyChangeListener", desc)){
			return;
		}

		
		String className = classMeta.getClassName();
		
		MethodVisitor mv;
	
		mv = cv.visitMethod(ACC_PUBLIC, "addPropertyChangeListener", desc, null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "addPropertyChangeListener", "(Ljava/lang/String;Ljava/beans/PropertyChangeListener;)V", false);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(2, l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L"+className+";", null, l0, l2, 0);
		mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l2, 1);
		mv.visitLocalVariable("listener", "Ljava/beans/PropertyChangeListener;", null, l0, l2, 2);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
	}
	
	/**
	 * Add the removePropertyChangeListener method.
	 * 
	 * <pre>
	 * 	public void removePropertyChangeListener(PropertyChangeListener listener) {
	 *    _ebean_intercept.removePropertyChangeListener(listener);
	 *  }
	 * </pre>
	 */
	private static void addRemoveListenerMethod(ClassVisitor cv, ClassMeta classMeta) {
		
		String desc = "(Ljava/beans/PropertyChangeListener;)V";
		
		if (alreadyExisting(classMeta, "removePropertyChangeListener", desc)){
			return;
		}
		
		String className = classMeta.getClassName();
		
		MethodVisitor mv;
		
		mv = cv.visitMethod(ACC_PUBLIC, "removePropertyChangeListener", desc, null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "removePropertyChangeListener", "(Ljava/beans/PropertyChangeListener;)V", false);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(2, l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L"+className+";", null, l0, l2, 0);
		mv.visitLocalVariable("listener", "Ljava/beans/PropertyChangeListener;", null, l0, l2, 1);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private static void addRemovePropertyListenerMethod(ClassVisitor cv, ClassMeta classMeta) {
		
		String desc = "(Ljava/lang/String;Ljava/beans/PropertyChangeListener;)V";
		
		if (alreadyExisting(classMeta, "removePropertyChangeListener", desc)){
			return;
		}
		
		String className = classMeta.getClassName();
		
		MethodVisitor mv;

		mv = cv.visitMethod(ACC_PUBLIC, "removePropertyChangeListener", desc, null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "removePropertyChangeListener", "(Ljava/lang/String;Ljava/beans/PropertyChangeListener;)V", false);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(2, l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L"+className+";", null, l0, l2, 0);
		mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l2, 1);
		mv.visitLocalVariable("listener", "Ljava/beans/PropertyChangeListener;", null, l0, l2, 2);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
	}
}