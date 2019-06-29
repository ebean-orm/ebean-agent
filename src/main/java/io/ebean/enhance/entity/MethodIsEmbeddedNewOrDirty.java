package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;

import java.util.List;

/**
 * Used to detect if a class has been enhanced.
 * <p>
 * Moved to use this over just relying on the existence of the EntityBean interface
 * to make the enhancement more robust.
 * </p>
 */
class MethodIsEmbeddedNewOrDirty implements Opcodes, EnhanceConstants {

  /**
   * Generate the _ebean_isEmbeddedNewOrDirty() method.
   *
   * <pre>
   * public boolean _ebean_isEmbeddedNewOrDirty() {
   *  // for each embedded bean field...
   * 	if (entityBeanIntercept.isEmbeddedNewOrDirty(embeddedBeanField)) return true;
   *  ...
   *  return false;
   * }
   * </pre>
   */
  static void addMethod(ClassVisitor cv, ClassMeta classMeta) {

    String className = classMeta.getClassName();

    MethodVisitor mv;

    mv = cv.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "_ebean_isEmbeddedNewOrDirty", "()Z", null, null);
    mv.visitCode();

    Label labelBegin = null;

    Label labelNext = null;

    List<FieldMeta> allFields = classMeta.getAllFields();
    for (int i = 0; i < allFields.size(); i++) {
      FieldMeta fieldMeta = allFields.get(i);
      if (fieldMeta.isEmbedded()) {

        Label l0 = labelNext;
        if (l0 == null) {
          l0 = new Label();
        }
        if (labelBegin == null) {
          labelBegin = l0;
        }

        mv.visitLabel(l0);
        mv.visitLineNumber(0, l0);
        if (i > 0) {
          mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
        mv.visitVarInsn(ALOAD, 0);
        fieldMeta.appendSwitchGet(mv, classMeta, false);
        mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "isEmbeddedNewOrDirty", "(Ljava/lang/Object;)Z", false);

        labelNext = new Label();
        mv.visitJumpInsn(IFEQ, labelNext);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);

      }
    }

    if (labelNext == null) {
      labelNext = new Label();
    }
    if (labelBegin == null) {
      labelBegin = labelNext;
    }
    mv.visitLabel(labelNext);
    mv.visitLineNumber(1, labelNext);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(IRETURN);

    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLocalVariable("this", "L" + className + ";", null, labelBegin, l3, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }
}
