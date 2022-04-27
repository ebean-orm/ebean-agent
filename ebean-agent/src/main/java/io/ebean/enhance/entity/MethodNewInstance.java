package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.common.ClassMeta;

import static io.ebean.enhance.asm.Opcodes.*;
import static io.ebean.enhance.common.EnhanceConstants.INIT;
import static io.ebean.enhance.common.EnhanceConstants.NOARG_VOID;

/**
 * Adds the _ebean_newInstance() method.
 */
class MethodNewInstance {

  /**
   * Add the _ebean_newInstance() method.
   */
  static void addMethod(ClassVisitor cv, ClassMeta classMeta) {
    MethodVisitor mv = cv.visitMethod(classMeta.accPublic(), "_ebean_newInstance", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(10, l0);
    mv.visitTypeInsn(NEW, classMeta.getClassName());
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, classMeta.getClassName(), INIT, NOARG_VOID, false);
    mv.visitInsn(ARETURN);

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + classMeta.getClassName() + ";", null, l0, l1, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }
}
