package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
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
class MarkerField implements Opcodes, EnhanceConstants {

  /**
   * The name of the static field added. Its value is the class being enhanced.
   */
  static final String _EBEAN_MARKER = "_EBEAN_MARKER";

  /**
   * Add the _EBEAN_MARKER field.
   */
  static String addField(ClassVisitor cv, ClassMeta meta) {
    String cn = meta.getClassName().replace('/', '.');
    FieldVisitor fv = cv.visitField(meta.accPrivate() + ACC_STATIC, _EBEAN_MARKER, L_STRING, null, cn);
    fv.visitEnd();
    return cn;
  }

  /**
   * Generate the _ebean_getMarker() method.
   *
   * <pre>
   * public String _ebean_getMarker() {
   * 	return _EBEAN_MARKER;
   * }
   * </pre>
   */
  static void addGetMarker(ClassVisitor cv, ClassMeta meta) {

    MethodVisitor mv;

    mv = cv.visitMethod(meta.accPublic(), "_ebean_getMarker", "()Ljava/lang/String;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitFieldInsn(GETSTATIC, meta.getClassName(), "_EBEAN_MARKER", L_STRING);
    mv.visitInsn(ARETURN);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + meta.getClassName() + ";", null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }
}
