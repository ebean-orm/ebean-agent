package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Adapts constructor method code with profile location for query beans and finders.
 */
class ConstructorMethodAdapter extends MethodVisitor implements EnhanceConstants, Opcodes {

  private final ProfileMethodInstruction profileMethod;

  ConstructorMethodAdapter(ClassAdapterTransactional classAdapter, final MethodVisitor mv) {
    super(Opcodes.ASM7, mv);
    this.profileMethod = new ProfileMethodInstruction(classAdapter, mv);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    profileMethod.visitMethodInsn(opcode, owner, name, desc, itf);
  }

}
