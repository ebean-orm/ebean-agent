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

  private boolean isEntityBean(String owner) {
    return enhanceContext.isEntityBean(owner);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    if (opcode == GETSTATIC || opcode == PUTSTATIC) {
      super.visitFieldInsn(opcode, owner, name, desc);
      return;
    }
    if (opcode == GETFIELD && isQueryBean(owner)) {
      classInfo.addGetFieldIntercept(owner, name);
      mv.visitMethodInsn(INVOKEVIRTUAL, owner, "_" + name, "()" + desc, false);
    } else {
      if (opcode == GETFIELD && fieldAccessReplacement(owner)) {
        classInfo.markEntityFieldAccess("get", owner, name);
        mv.visitMethodInsn(INVOKEVIRTUAL, owner, "_ebean_get_" + name, "()" + desc, false);
        return;
      } else if (opcode == PUTFIELD && fieldAccessReplacement(owner)) {
        classInfo.markEntityFieldAccess("set", owner, name);
        mv.visitMethodInsn(INVOKEVIRTUAL, owner, "_ebean_set_" + name, "(" + desc + ")V", false);
        return;
      }
      super.visitFieldInsn(opcode, owner, name, desc);
    }
  }

  private boolean fieldAccessReplacement(String owner) {
    return !classInfo.isEntityBean()
      && enhanceContext.isEnableEntityFieldAccess()
      && isOtherEntityClass(owner);
  }

  private boolean isOtherEntityClass(String owner) {
    return !classInfo.getClassName().equals(owner) && isEntityBean(owner);
  }

}
