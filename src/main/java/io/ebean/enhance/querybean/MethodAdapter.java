package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceContext;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

/**
 * Adapter that changes GETFIELD calls to type query beans to instead use the generated
 * 'property access' methods.
 */
class MethodAdapter extends MethodVisitor implements Opcodes {

  private final EnhanceContext enhanceContext;
  private final ClassInfo classInfo;
  private final ClassLoader loader;

  MethodAdapter(MethodVisitor mv, EnhanceContext enhanceContext, ClassInfo classInfo, ClassLoader loader) {
    super(EBEAN_ASM_VERSION, mv);
    this.enhanceContext = enhanceContext;
    this.classInfo = classInfo;
    this.loader = loader;
  }

  private boolean isQueryBean(String owner) {
    return enhanceContext.isQueryBean(owner, loader);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    if (opcode == GETFIELD && isQueryBean(owner)) {
      classInfo.addGetFieldIntercept(owner, name);
      mv.visitMethodInsn(INVOKEVIRTUAL, owner, "_" + name, "()" + desc, false);
    } else {
      super.visitFieldInsn(opcode, owner, name, desc);
    }
  }

}
