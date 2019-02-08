package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.commons.AdviceAdapter;

/**
 * Enhance an existing static initialisation block with a call to our static
 * _$initProfileLocations() method.
 */
public class StaticInitAdapter extends AdviceAdapter {

  private final String className;

  StaticInitAdapter(MethodVisitor mv, int access, String name, String desc, String className) {
    super(ASM7, mv, access, name, desc);
    this.className = className;
  }

  /**
  * Adds the call to the static _$initProfileLocations() method.
  */
  @Override
  public void visitCode() {
    super.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitMethodInsn(INVOKESTATIC, className, "_$initProfileLocations", "()V", false);
  }

}
