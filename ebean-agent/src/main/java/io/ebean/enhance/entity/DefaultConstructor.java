package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.common.ClassMeta;

import static io.ebean.enhance.asm.Opcodes.*;
import static io.ebean.enhance.common.EnhanceConstants.INIT;
import static io.ebean.enhance.common.EnhanceConstants.NOARG_VOID;

/**
 * Adds a default constructor for the cases when there is not one already defined.
 */
final class DefaultConstructor {

  /**
   * Adds a default constructor.
   */
  public static void add(ClassVisitor cw, ClassMeta classMeta) {
    if (classMeta.isLog(4)) {
      classMeta.log("... adding default constructor, super class: " + classMeta.superClassName());
    }
    if (classMeta.hasTransientFieldErrors()) {
      if (classMeta.context().isTransientInitThrowError()) {
        throw new RuntimeException(classMeta.transientFieldErrorMessage());
      } else {
        // the default constructor being added will leave some transient fields uninitialised (null, 0, false etc)
        System.err.println(classMeta.transientFieldErrorMessage());
      }
    }

    MethodVisitor underlyingMV = cw.visitMethod(classMeta.accPublic(), INIT, NOARG_VOID, null, null);

    ConstructorAdapter mv = new ConstructorAdapter(underlyingMV, classMeta, NOARG_VOID);

    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, classMeta.superClassName(), INIT, NOARG_VOID, false);
    for (CapturedInitCode entry : classMeta.transientInit()) {
      if (classMeta.isLog(2)) {
        classMeta.log("... default constructor, init transient " + entry.name() + " type: " + entry.type());
      }
      entry.write(mv);
    }
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(2, l1);
    mv.visitInsn(RETURN);

    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", "L" + classMeta.className() + ";", null, l0, l2, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }
}
