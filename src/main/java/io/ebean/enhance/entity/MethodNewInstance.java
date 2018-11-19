package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;

/**
 * Adds the _ebean_newInstance() method.
 */
public class MethodNewInstance {

  /**
  * Add the _ebean_newInstance() method.
  */
  public static void addMethod(ClassVisitor cv, ClassMeta classMeta) {

    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "_ebean_newInstance", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(10, l0);
    mv.visitTypeInsn(Opcodes.NEW, classMeta.getClassName());
    mv.visitInsn(Opcodes.DUP);
    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classMeta.getClassName(), "<init>", "()V", false);
    mv.visitInsn(Opcodes.ARETURN);

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + classMeta.getClassName() + ";", null, l0, l1, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }
}
