package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;

/**
 * For query beans with getter methods (Kotlin properties) replaces
 * the getter method with appropriate method call.
 */
class TypeQueryGetterAdapter extends BaseConstructorAdapter {

  private final ClassVisitor cv;
  private final ClassInfo classInfo;
  private final MethodDesc methodDesc;

  TypeQueryGetterAdapter(ClassVisitor cv, ClassInfo classInfo, MethodDesc methodDesc) {
    this.cv = cv;
    this.classInfo = classInfo;
    this.methodDesc = methodDesc;
  }

  @Override
  public void visitCode() {
    MethodVisitor mv = methodDesc.visitMethod(cv);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(3, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, classInfo.getClassName(), methodDesc.proxyMethodName(), methodDesc.getDesc(), false);
    mv.visitInsn(ARETURN);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + classInfo.getClassName() + ";", null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }
}
