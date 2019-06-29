package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.asm.commons.AdviceAdapter;

/**
 * FinallyAdapter adjusted to support both non-finally use (for ConstructorMethodAdapter)
 * and finally use (for MethodAdapter that also enhances transactional methods)
 */
abstract class FinallyAdapter extends AdviceAdapter {

  private final Label startFinally = new Label();

  FinallyAdapter(MethodVisitor mv, int acc, String name, String desc) {
    super(Opcodes.ASM7, mv, acc, name, desc);
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    super.visitMaxs(maxStack, maxLocals);
  }

  void finallyVisitCode() {
    super.visitCode();
    mv.visitLabel(startFinally);
  }

  void finallyVisitMaxs(int maxStack, int maxLocals) {

    Label endFinally = new Label();
    mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
    mv.visitLabel(endFinally);
    onFinally(ATHROW);
    mv.visitInsn(ATHROW);
    mv.visitMaxs(maxStack, maxLocals);
  }

  @Override
  protected final void onMethodExit(int opcode) {
    if (opcode != ATHROW) {
      onFinally(opcode);
    }
  }

  void onFinally(int opcode) {
  }

}
