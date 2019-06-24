package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.common.VisitUtil;

/**
 * Generate the equals hashCode method using the identity.
 * <p>
 * This will add a _ebean_getIdentity() equals() and hashCode() methods based on
 * having a single ID property and no existing equals() or hashCode() methods.
 * </p>
 */
public class MethodEquals implements Opcodes, EnhanceConstants {

  private static final String _EBEAN_GET_IDENTITY = "_ebean_getIdentity";

  /**
  * Adds equals(), hashCode() and _ebean_getIdentity() methods.
  * <p>
  * If the class already has a equals() or hashCode() method defined then
  * these methods are not added (its a noop).
  * </p>
  *
  * @param idFieldIndex
  *            the index of the id field
  */
  public static void addMethods(ClassVisitor cv, ClassMeta meta, int idFieldIndex, FieldMeta idFieldMeta) {

    if (meta.hasEqualsOrHashCode()) {
      // already has a equals or hashcode method...
      // so we will not add our identity based one
      if (meta.isLog(2)) {
        meta.log("already has a equals() or hashCode() method. Not adding the identity based one.");
      }
    } else {
      if (meta.isLog(2)) {
        meta.log("adding equals() hashCode() and _ebean_getIdentity() with Id field "
          + idFieldMeta.getName()+ " index:" + idFieldIndex+" primitive:"+idFieldMeta.isPrimitiveType());
      }
      if (idFieldMeta.isPrimitiveType()){
        addGetIdentityPrimitive(cv, meta, idFieldMeta);
      } else {
        addGetIdentityObject(cv, meta, idFieldIndex);
      }
      addEquals(cv, meta);
      addHashCode(cv, meta);
    }

  }

  /**
  * The identity field used for implementing equals via the
  * _ebean_getIdentity() method.
  */
  public static void addIdentityField(ClassVisitor cv) {

      int access = ACC_PROTECTED + ACC_TRANSIENT + ACC_SYNTHETIC;
    FieldVisitor f0 = cv.visitField(access, IDENTITY_FIELD, L_OBJECT, null, null);
    f0.visitEnd();
  }

  /**
  * Generate the _ebean_getIdentity method for primitive id types.
  * <p>
  * For primitives we need to check for == 0 rather than null.
  * <p>
  * <p>
  * This is used for implementing equals().
  * <p>
  *
  * <pre>
  * private Object _ebean_getIdentity() {
  * 	synchronized (this) {
  * 		if (_ebean_identity != null) {
  * 			return _ebean_identity;
  * 		}
  *
  * 		if (0 != getId()) {
  * 			_ebean_identity = Integer.valueOf(getId());
  * 		} else {
  * 			_ebean_identity = new Object();
  * 		}
  *
  * 		return _ebean_identity;
  * 	}
  * }
  * </pre>
  */
  private static void addGetIdentityPrimitive(ClassVisitor cv, ClassMeta classMeta, FieldMeta idFieldMeta) {

    String className = classMeta.getClassName();

    MethodVisitor mv;

    mv = cv.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, _EBEAN_GET_IDENTITY, "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    Label l1 = new Label();
    Label l2 = new Label();
    mv.visitTryCatchBlock(l0, l1, l2, null);
    Label l3 = new Label();
    Label l4 = new Label();
    mv.visitTryCatchBlock(l3, l4, l2, null);
    Label l5 = new Label();
    mv.visitTryCatchBlock(l2, l5, l2, null);
    Label l6 = new Label();
    mv.visitLabel(l6);
    mv.visitLineNumber(1, l6);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, 1);
    mv.visitInsn(MONITORENTER);
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitJumpInsn(IFNULL, l3);
    Label l7 = new Label();
    mv.visitLabel(l7);
    mv.visitLineNumber(1, l7);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(l1);
    mv.visitInsn(ARETURN);
    mv.visitLabel(l3);
    mv.visitLineNumber(1, l3);
    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Object"}, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    idFieldMeta.appendGetPrimitiveIdValue(mv, classMeta);
    idFieldMeta.appendCompare(mv, classMeta);

    Label l8 = new Label();
    mv.visitJumpInsn(IFEQ, l8);
    Label l9 = new Label();
    mv.visitLabel(l9);
    mv.visitLineNumber(1, l9);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 0);
    idFieldMeta.appendGetPrimitiveIdValue(mv, classMeta);
    idFieldMeta.appendValueOf(mv);
    mv.visitFieldInsn(PUTFIELD, className, IDENTITY_FIELD, L_OBJECT);
    Label l10 = new Label();
    mv.visitJumpInsn(GOTO, l10);
    mv.visitLabel(l8);
    mv.visitLineNumber(1, l8);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitTypeInsn(NEW, "java/lang/Object");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", INIT, NOARG_VOID, false);
    mv.visitFieldInsn(PUTFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitLabel(l10);
    mv.visitLineNumber(1, l10);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(l4);
    mv.visitInsn(ARETURN);
    mv.visitLabel(l2);
    mv.visitLineNumber(1, l2);
    mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
    mv.visitVarInsn(ASTORE, 2);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(l5);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitInsn(ATHROW);
    Label l11 = new Label();
    mv.visitLabel(l11);
    mv.visitLocalVariable("this", "L"+className+";", null, l6, l11, 0);
    mv.visitMaxs(4, 3);
    mv.visitEnd();
  }

  /**
  * Generate the _ebean_getIdentity method for used with equals().
  *
  * <pre>
  * private Object _ebean_getIdentity() {
  * 	synchronized (this) {
  * 		if (_ebean_identity != null) {
  * 			return _ebean_identity;
  * 		}
  *
  * 		Object id = getId();
  * 		if (id != null) {
  * 			_ebean_identity = id;
  * 		} else {
  * 			_ebean_identity = new Object();
  * 		}
  *
  * 		return _ebean_identity;
  * 	}
  * }
  * </pre>
  */
  private static void addGetIdentityObject(ClassVisitor cv, ClassMeta classMeta, int idFieldIndex) {

    String className = classMeta.getClassName();

    MethodVisitor mv;

    mv = cv.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, _EBEAN_GET_IDENTITY, "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    Label l1 = new Label();
    Label l2 = new Label();
    mv.visitTryCatchBlock(l0, l1, l2, null);
    Label l3 = new Label();
    Label l4 = new Label();
    mv.visitTryCatchBlock(l3, l4, l2, null);
    Label l5 = new Label();
    mv.visitTryCatchBlock(l2, l5, l2, null);
    Label l6 = new Label();
    mv.visitLabel(l6);
    mv.visitLineNumber(1, l6);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, 1);
    mv.visitInsn(MONITORENTER);
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitJumpInsn(IFNULL, l3);
    Label l7 = new Label();
    mv.visitLabel(l7);
    mv.visitLineNumber(1, l7);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(l1);
    mv.visitInsn(ARETURN);
    mv.visitLabel(l3);
    mv.visitLineNumber(1, l3);
    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Object"}, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    VisitUtil.visitIntInsn(mv, idFieldIndex);
    mv.visitMethodInsn(INVOKESPECIAL, className, "_ebean_getField", "(I)Ljava/lang/Object;", false);
    mv.visitVarInsn(ASTORE, 2);
    Label l8 = new Label();
    mv.visitLabel(l8);
    mv.visitLineNumber(1, l8);
    mv.visitVarInsn(ALOAD, 2);
    Label l9 = new Label();
    mv.visitJumpInsn(IFNULL, l9);
    Label l10 = new Label();
    mv.visitLabel(l10);
    mv.visitLineNumber(1, l10);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitFieldInsn(PUTFIELD, className, IDENTITY_FIELD, L_OBJECT);
    Label l11 = new Label();
    mv.visitJumpInsn(GOTO, l11);
    mv.visitLabel(l9);
    mv.visitLineNumber(1, l9);
    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Object"}, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitTypeInsn(NEW, "java/lang/Object");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", INIT, NOARG_VOID, false);
    mv.visitFieldInsn(PUTFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitLabel(l11);
    mv.visitLineNumber(1, l11);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, IDENTITY_FIELD, L_OBJECT);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(l4);
    mv.visitInsn(ARETURN);
    mv.visitLabel(l2);
    mv.visitLineNumber(1, l2);
    mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{className, "java/lang/Object"}, 1, new Object[]{"java/lang/Throwable"});
    mv.visitVarInsn(ASTORE, 3);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(l5);
    mv.visitVarInsn(ALOAD, 3);
    mv.visitInsn(ATHROW);
    Label l12 = new Label();
    mv.visitLabel(l12);
    mv.visitLocalVariable("this", "L"+className+";", null, l6, l12, 0);
    mv.visitLocalVariable("tmpId", L_OBJECT, null, l8, l2, 2);
    mv.visitMaxs(3, 4);
    mv.visitEnd();
  }

    /**
    * Generate the equals method.
    *
    * <pre>
    * public boolean equals(Object o) {
    *     if (o == null) {
    *         return false;
    *     }
    *     if (!this.getClass().equals(o.getClass())) {
    *         return false;
    *     }
    *     if (o == this) {
    *         return true;
    *     }
    *     return _ebean_getIdentity().equals(((FooEntity)o)._ebean_getIdentity());
    * }
    * </pre>
    */
  private static void addEquals(ClassVisitor cv, ClassMeta classMeta) {

    MethodVisitor mv;

    mv = cv.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "equals", "(Ljava/lang/Object;)Z", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 1);
    Label l1 = new Label();
    mv.visitJumpInsn(IFNONNULL, l1);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(2, l2);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(IRETURN);
    mv.visitLabel(l1);
    mv.visitLineNumber(3, l1);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
    Label l3 = new Label();
    mv.visitJumpInsn(IFNE, l3);
    Label l4 = new Label();
    mv.visitLabel(l4);
    mv.visitLineNumber(4, l4);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(IRETURN);
    mv.visitLabel(l3);
    mv.visitLineNumber(5, l3);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 0);
    Label l5 = new Label();
    mv.visitJumpInsn(IF_ACMPNE, l5);
    Label l6 = new Label();
    mv.visitLabel(l6);
    mv.visitLineNumber(6, l6);
    mv.visitInsn(ICONST_1);
    mv.visitInsn(IRETURN);
    mv.visitLabel(l5);
    mv.visitLineNumber(7, l5);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), _EBEAN_GET_IDENTITY, "()Ljava/lang/Object;", false);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitTypeInsn(CHECKCAST, classMeta.getClassName());
    mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), _EBEAN_GET_IDENTITY, "()Ljava/lang/Object;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
    mv.visitInsn(IRETURN);
    Label l7 = new Label();
    mv.visitLabel(l7);
    mv.visitLocalVariable("this", "L"+classMeta.getClassName()+";", null, l0, l7, 0);
    mv.visitLocalVariable("obj", L_OBJECT, null, l0, l7, 1);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  /**
  * Generate a hashCode method used to go with MethodEquals.
  *
  * <pre><code>
  * public int hashCode() {
  * 	return ebeanGetIdentity().hashCode();
  * }
  * </code></pre>
  */
  private static void addHashCode(ClassVisitor cv, ClassMeta meta) {

    MethodVisitor mv;

    mv = cv.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "hashCode", "()I", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, meta.getClassName(), _EBEAN_GET_IDENTITY, "()Ljava/lang/Object;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
    mv.visitInsn(IRETURN);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + meta.getClassName() + ";", null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

  }

}
