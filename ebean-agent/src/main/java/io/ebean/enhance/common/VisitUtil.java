package io.ebean.enhance.common;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;

public class VisitUtil implements Opcodes {

  /**
  * Helper method for visiting an int value.
  * <p>
  * This can use special constant values for int values from 0 to 5.
  * </p>
  */
  public static void visitIntInsn(MethodVisitor mv, int value) {
    switch (value) {
      case 0:
        mv.visitInsn(ICONST_0);
        break;
      case 1:
        mv.visitInsn(ICONST_1);
        break;
      case 2:
        mv.visitInsn(ICONST_2);
        break;
      case 3:
        mv.visitInsn(ICONST_3);
        break;
      case 4:
        mv.visitInsn(ICONST_4);
        break;
      case 5:
        mv.visitInsn(ICONST_5);
        break;
      default:
        if (value <= Byte.MAX_VALUE){
          mv.visitIntInsn(BIPUSH, value);
        } else {
          mv.visitIntInsn(SIPUSH, value);
        }
    }
  }
}
