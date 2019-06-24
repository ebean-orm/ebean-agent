package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Used to detect if a class has been enhanced.
 * <p>
 * Moved to use this over just relying on the existence of the EntityBean interface
 * to make the enhancement more robust.
 * </p>
 */
public class MarkerField implements Opcodes, EnhanceConstants {

  /**
  * The name of the static field added. Its value is the class being enhanced.
  */
  public static final String _EBEAN_MARKER = "_EBEAN_MARKER";

  /**
  * Add the _EBEAN_MARKER field.
  */
  public static String addField(ClassVisitor cv, String className) {

    String cn = className.replace('/', '.');

    FieldVisitor fv = cv.visitField(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, _EBEAN_MARKER, L_STRING, null, cn);
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
  public static void addGetMarker(ClassVisitor cv, String className) {


    MethodVisitor mv;

    mv = cv.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "_ebean_getMarker", "()Ljava/lang/String;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitFieldInsn(GETSTATIC, className, "_EBEAN_MARKER", L_STRING);
    mv.visitInsn(ARETURN);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L"+className+";", null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }
}
