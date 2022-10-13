package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.common.ClassMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.ebean.enhance.asm.Opcodes.*;
import static io.ebean.enhance.common.EnhanceConstants.C_TOSTRINGBUILDER;

final class MethodToString {

  static void addMethod(ClassVisitor cv, ClassMeta classMeta) {
    if (!classMeta.context().isEnhancedToString()) {
      return;
    }
    if (!classMeta.hasToString()) {
      addToString(cv, classMeta);
    }
    addToStringExtra(cv, classMeta);
  }

  private static void addToString(ClassVisitor cv, ClassMeta meta) {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
    mv.visitCode();
    Label label0 = new Label();
    mv.visitLabel(label0);
    mv.visitLineNumber(2, label0);
    mv.visitTypeInsn(NEW, C_TOSTRINGBUILDER);
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, C_TOSTRINGBUILDER, "<init>", "()V", false);
    mv.visitVarInsn(ASTORE, 1);
    Label label1 = new Label();
    mv.visitLabel(label1);
    mv.visitLineNumber(3, label1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, meta.className(), "toString", "(L" + C_TOSTRINGBUILDER + ";)V", false);
    Label label2 = new Label();
    mv.visitLabel(label2);
    mv.visitLineNumber(4, label2);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TOSTRINGBUILDER, "toString", "()Ljava/lang/String;", false);
    mv.visitInsn(ARETURN);
    Label label3 = new Label();
    mv.visitLabel(label3);
    mv.visitLocalVariable("this", "L" + meta.className() + ";", null, label0, label3, 0);
    mv.visitLocalVariable("builder", "L" + C_TOSTRINGBUILDER + ";", null, label1, label3, 1);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  private static void addToStringExtra(ClassVisitor cv, ClassMeta meta) {
    MethodVisitor mv = cv.visitMethod(meta.accPublic(), "toString", "(Lio/ebean/bean/ToStringBuilder;)V", null, null);
    mv.visitCode();
    Label label0 = new Label();
    mv.visitLabel(label0);
    mv.visitLineNumber(2, label0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TOSTRINGBUILDER, "start", "(Ljava/lang/Object;)V", false);

    for (FieldMeta fieldMeta : copyAndSort(meta.allFields())) {
      Label label1 = new Label();
      mv.visitLabel(label1);
      mv.visitLineNumber(3, label1);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitLdcInsn(fieldMeta.name());
      mv.visitVarInsn(ALOAD, 0);
      fieldMeta.appendSwitchGet(mv, meta, false);
      mv.visitMethodInsn(INVOKEVIRTUAL, C_TOSTRINGBUILDER, "add", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
    }

    Label label7 = new Label();
    mv.visitLabel(label7);
    mv.visitLineNumber(4, label7);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TOSTRINGBUILDER, "end", "()V", false);
    Label label8 = new Label();
    mv.visitLabel(label8);
    mv.visitLineNumber(5, label8);
    mv.visitInsn(RETURN);
    Label label9 = new Label();
    mv.visitLabel(label9);
    mv.visitLocalVariable("this", "L" + meta.className() + ";", null, label0, label9, 0);
    mv.visitLocalVariable("sb", "L" + C_TOSTRINGBUILDER + ";", null, label0, label9, 1);
    mv.visitMaxs(4, 2);
    mv.visitEnd();
  }

  private static List<FieldMeta> copyAndSort(List<FieldMeta> allFields) {
    List<FieldMeta> copy = new ArrayList<>(allFields);
    for (int i = 0, size = copy.size(); i < size; i++) {
      copy.get(i).setSortOrder(i);
    }
    Collections.sort(copy);
    return copy;
  }
}
