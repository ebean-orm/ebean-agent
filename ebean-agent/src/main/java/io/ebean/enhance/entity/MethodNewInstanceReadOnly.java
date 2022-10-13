package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;

import static io.ebean.enhance.asm.Opcodes.*;
import static io.ebean.enhance.common.EnhanceConstants.*;

/**
 * Add support for the InterceptReadOnly implementation of EntityBeanIntercept.
 */
final class MethodNewInstanceReadOnly {

  static void interceptAddReadOnly(ClassVisitor cv, ClassMeta meta) {
    if (!meta.interceptAddReadOnly()) {
      return;
    }
    if (meta.isSuperClassEntity()) {
      add_initSuper(cv, meta);
    } else {
      add_initLocal(cv, meta);
    }
    add_newInstanceReadOnly(cv, meta);
  }

  static void add_newInstanceReadOnly(ClassVisitor cv, ClassMeta meta) {
    MethodVisitor mv = cv.visitMethod(meta.accPublic(), "_ebean_newInstanceReadOnly", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    Label label0 = new Label();
    mv.visitLabel(label0);
    mv.visitLineNumber(4, label0);
    mv.visitTypeInsn(NEW, meta.className());
    mv.visitInsn(DUP);
    mv.visitInsn(ACONST_NULL);
    mv.visitMethodInsn(INVOKESPECIAL, meta.className(), INIT, "(L" + C_ENTITYBEAN + ";)V", false);
    mv.visitInsn(ARETURN);
    Label label1 = new Label();
    mv.visitLabel(label1);
    mv.visitLocalVariable("this", "L" + meta.className() + ";", null, label0, label1, 0);
    mv.visitMaxs(3, 1);
    mv.visitEnd();
  }

  static void add_initSuper(ClassVisitor cv, ClassMeta meta) {
    MethodVisitor mv = cv.visitMethod(meta.accProtected(), "<init>", "(L" + C_ENTITYBEAN + ";)V", null, null);
    mv.visitCode();
    Label label0 = new Label();
    mv.visitLabel(label0);
    mv.visitLineNumber(12, label0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESPECIAL, meta.superClassName(), "<init>", "(L" + C_ENTITYBEAN + ";)V", false);
    Label label1 = new Label();
    mv.visitLabel(label1);
    mv.visitLineNumber(13, label1);
    mv.visitInsn(RETURN);
    Label label2 = new Label();
    mv.visitLabel(label2);
    mv.visitLocalVariable("this", "L" + meta.className() + ";", null, label0, label2, 0);
    mv.visitLocalVariable("ignore", "L" + C_ENTITYBEAN + ";", null, label0, label2, 1);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  static void add_initLocal(ClassVisitor cv, ClassMeta meta) {
    MethodVisitor mv = cv.visitMethod(meta.accProtected(), "<init>", "(L" + C_ENTITYBEAN + ";)V", null, null);
    mv.visitCode();
    Label label0 = new Label();
    mv.visitLabel(label0);
    mv.visitLineNumber(2, label0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, meta.superClassName(), "<init>", "()V", false);
    Label label1 = new Label();
    mv.visitLabel(label1);
    mv.visitLineNumber(3, label1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitTypeInsn(NEW, C_INTERCEPT_RO);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, C_INTERCEPT_RO, "<init>", "(Ljava/lang/Object;)V", false);
    mv.visitFieldInsn(PUTFIELD, meta.className(), INTERCEPT_FIELD, EnhanceConstants.L_INTERCEPT);
    Label label2 = new Label();
    mv.visitLabel(label2);
    mv.visitLineNumber(25, label2);
    mv.visitInsn(RETURN);
    Label label3 = new Label();
    mv.visitLabel(label3);
    mv.visitLocalVariable("this", "L" + meta.className() + ";", null, label0, label3, 0);
    mv.visitLocalVariable("ignore", "L" + C_ENTITYBEAN + ";", null, label0, label3, 1);
    mv.visitMaxs(4, 2);
    mv.visitEnd();
  }
}
