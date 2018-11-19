package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Changes the existing constructor to remove all the field initialisation as these are going to be
 * initialised lazily by calls to our generated methods.
 */
public class TypeQueryAssocMainConstructor extends BaseConstructorAdapter implements Opcodes, Constants {

  private final ClassInfo classInfo;

  private final ClassVisitor cv;

  private final String desc;

  private final String signature;

  /**
  * Construct for a query bean class given its associated entity bean domain class and a class visitor.
  */
  public TypeQueryAssocMainConstructor(ClassInfo classInfo, ClassVisitor cv, String desc, String signature) {
    super();
    this.cv = cv;
    this.classInfo = classInfo;
    this.desc = desc;
    this.signature = signature;
  }

  @Override
  public void visitCode() {

    mv = cv.visitMethod(ACC_PUBLIC, "<init>", desc, signature, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitVarInsn(ALOAD, 3);
    mv.visitMethodInsn(INVOKESPECIAL, TQ_ASSOC_BEAN, "<init>", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)V", false);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(2, l1);
    mv.visitInsn(RETURN);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", "L"+classInfo.getClassName()+";", "L"+classInfo.getClassName()+"<TR;>;", l0, l2, 0);
    mv.visitLocalVariable("name", EnhanceConstants.STRING_CLASS, null, l0, l2, 1);
    mv.visitLocalVariable("root", EnhanceConstants.OBJECT_CLASS, "TR;", l0, l2, 2);
    mv.visitLocalVariable("prefix", EnhanceConstants.STRING_CLASS, null, l0, l2, 3);
    mv.visitLocalVariable("depth", "I", null, l0, l2, 4);
    mv.visitMaxs(4, 5);
    mv.visitEnd();
  }

}
