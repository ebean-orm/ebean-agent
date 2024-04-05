package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.Opcodes;

import java.util.List;

import static io.ebean.enhance.common.EnhanceConstants.INIT;

/**
 * Overrides the constructor to initialise all the fields (for use with 'Alias' select()/fetch() use).
 */
final class TypeQueryConstructorForAlias extends BaseConstructorAdapter implements Opcodes, Constants {

  private final ClassInfo classInfo;
  private final ClassVisitor cv;
  private final String superName;

  /**
   * Construct for a query bean class and a class visitor.
   */
  TypeQueryConstructorForAlias(ClassInfo classInfo, ClassVisitor cv, String superName) {
    super();
    this.cv = cv;
    this.classInfo = classInfo;
    this.superName = superName;
  }

  /**
   * Write the constructor initialising all the fields eagerly.
   */
  @Override
  public void visitCode() {
    mv = cv.visitMethod(ACC_PRIVATE, INIT, "(Z)V", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ILOAD, 1);
    mv.visitMethodInsn(INVOKESPECIAL, superName, INIT, "(Z)V", false);

    // init all the properties
    List<FieldInfo> fields = classInfo.getFields();
    if (fields != null) {
      for (FieldInfo field : fields) {
        field.writeFieldInit(mv);
      }
    }

    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(3, l2);
    mv.visitInsn(RETURN);
    Label l12 = new Label();
    mv.visitLabel(l12);
    mv.visitLocalVariable("this", "L" + classInfo.getClassName() + ";", null, l0, l12, 0);
    mv.visitLocalVariable("alias", "Z", null, l0, l12, 1);
    mv.visitMaxs(5, 2);
    mv.visitEnd();
  }

}
