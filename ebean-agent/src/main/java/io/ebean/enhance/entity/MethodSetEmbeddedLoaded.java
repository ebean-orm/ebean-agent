package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;

import java.util.List;

/**
 * Generate the _ebean_setEmbeddedLoaded() method.
 */
final class MethodSetEmbeddedLoaded implements Opcodes, EnhanceConstants {

  /**
   * Generate the _ebean_setEmbeddedLoaded() method.
   *
   * <pre>
   * public void _ebean_setEmbeddedLoaded() {
   *  // for each embedded bean field...
   * 	entityBeanIntercept.setEmbeddedLoaded(embeddedBeanField);
   * }
   * </pre>
   */
  static void addMethod(ClassVisitor cv, ClassMeta classMeta) {
    String className = classMeta.className();

    MethodVisitor mv;
    mv = cv.visitMethod(classMeta.accPublic(), "_ebean_setEmbeddedLoaded", NOARG_VOID, null, null);
    mv.visitCode();

    Label labelBegin = null;
    List<FieldMeta> allFields = classMeta.allFields();
    for (FieldMeta fieldMeta : allFields) {
      if (fieldMeta.isEmbedded()) {

        Label l0 = new Label();
        if (labelBegin == null) {
          labelBegin = l0;
        }

        mv.visitLabel(l0);
        mv.visitLineNumber(0, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
        mv.visitVarInsn(ALOAD, 0);
        fieldMeta.appendSwitchGet(mv, classMeta, false);
        classMeta.visitMethodInsnIntercept(mv, "setEmbeddedLoaded", "(Ljava/lang/Object;)V");
      }
    }

    Label l2 = new Label();
    if (labelBegin == null) {
      labelBegin = l2;
    }
    mv.visitLabel(l2);
    mv.visitLineNumber(1, l2);
    mv.visitInsn(RETURN);
    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLocalVariable("this", "L" + className + ";", null, labelBegin, l3, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }
}
