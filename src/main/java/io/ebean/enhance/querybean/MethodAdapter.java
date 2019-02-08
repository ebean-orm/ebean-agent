package io.ebean.enhance.querybean;

import io.ebean.enhance.common.EnhanceContext;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;

/**
 * Adapter that changes GETFIELD calls to type query beans to instead use the generated
 * 'property access' methods.
 */
public class MethodAdapter extends MethodVisitor implements Opcodes {

  private final EnhanceContext enhanceContext;

  private final ClassInfo classInfo;

  public MethodAdapter(MethodVisitor mv, EnhanceContext enhanceContext, ClassInfo classInfo) {
    super(ASM7, mv);
    this.enhanceContext = enhanceContext;
    this.classInfo = classInfo;
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {

    if (opcode == GETFIELD && enhanceContext.isQueryBean(owner)) {
      classInfo.addGetFieldIntercept(owner, name);
      mv.visitMethodInsn(INVOKEVIRTUAL, owner, "_" + name, "()" + desc, false);
    } else {
      super.visitFieldInsn(opcode, owner, name, desc);
    }
  }

}
