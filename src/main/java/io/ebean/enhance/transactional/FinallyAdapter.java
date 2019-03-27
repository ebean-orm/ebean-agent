package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.commons.AdviceAdapter;

/**
 * FinallyAdapter adjusted to support both non-finally use (for ConstructorMethodAdapter)
 * and finally use (for MethodAdapter that also enhances transactional methods)
 */
public abstract class FinallyAdapter extends AdviceAdapter {

  protected Label startFinally = new Label();

  public FinallyAdapter(final int api, MethodVisitor mv, int acc, String name, String desc) {
    super(api, mv, acc, name, desc);
  }

  @Override
  public void visitCode() {
    super.visitCode();
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    super.visitMaxs(maxStack, maxLocals);
  }

  protected void finallyVisitCode() {
    super.visitCode();
    mv.visitLabel(startFinally);
  }

  protected void finallyVisitMaxs(int maxStack, int maxLocals) {

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

  protected void onFinally(int opcode) {
  }

}
