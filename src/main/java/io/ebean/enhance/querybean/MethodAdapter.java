package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceContext;

import java.util.HashSet;
import java.util.Set;

import static io.ebean.enhance.querybean.Constants.SET_LABEL;

/**
 * Adapter that changes GETFIELD calls to type query beans to instead use the generated
 * 'property access' methods.
 */
class MethodAdapter extends MethodVisitor implements Opcodes {

  private static final Set<String> FINDER_METHODS = new HashSet<>();

  static {
    // exclude findEach, findEachWhile which take closures
    FINDER_METHODS.add("findList");
    FINDER_METHODS.add("findSet");
    FINDER_METHODS.add("findMap");
    FINDER_METHODS.add("findIds");
    FINDER_METHODS.add("findCount");
    FINDER_METHODS.add("findOne");
    FINDER_METHODS.add("findOneOrEmpty");
    FINDER_METHODS.add("findSingleAttribute");
    FINDER_METHODS.add("findSingleAttributeList");
    FINDER_METHODS.add("findIterate");
  }

  private final EnhanceContext enhanceContext;

  private final ClassInfo classInfo;

  private final String methodName;

  private boolean labelSet;

  MethodAdapter(MethodVisitor mv, EnhanceContext enhanceContext, ClassInfo classInfo, String methodName) {
    super(ASM7, mv);
    this.enhanceContext = enhanceContext;
    this.classInfo = classInfo;
    this.methodName = methodName;
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

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

    if (!isInterface && enhanceContext.isEnableQueryAutoLabel()) {
      if (SET_LABEL.equals(name) && enhanceContext.isQueryBean(owner)) {
        // label set explicitly in code so don't auto set it
        labelSet = true;
      }
      if (!labelSet && isFinderMethod(name) && enhanceContext.isQueryBean(owner)) {
        // set a label on the query
        classInfo.markTypeQueryEnhanced();
        mv.visitLdcInsn(classInfo.getShortName() + "." + methodName);
        mv.visitMethodInsn(INVOKEVIRTUAL, owner, SET_LABEL, "(Ljava/lang/String;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, owner);
      }
    }

    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
  }

  private boolean isFinderMethod(String name) {
    return FINDER_METHODS.contains(name);
  }
}
