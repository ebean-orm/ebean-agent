package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.asm.Type;

import static io.ebean.enhance.common.EnhanceConstants.INIT;

/**
 * Changes the existing constructor to remove all the field initialisation as these are going to be
 * initialised lazily by calls to our generated methods.
 */
class TypeQueryConstructorAdapter extends BaseConstructorAdapter implements Opcodes, Constants {

  private final ClassInfo classInfo;

  private final String domainClass;

  private final ClassVisitor cv;

  private final String desc;

  private final String signature;

  /**
   * Construct for a query bean class given its associated entity bean domain class and a class visitor.
   */
  TypeQueryConstructorAdapter(ClassInfo classInfo, String domainClass, ClassVisitor cv, String desc, String signature) {
    super();
    this.cv = cv;
    this.classInfo = classInfo;
    this.domainClass = domainClass;
    this.desc = desc;
    this.signature = signature;
  }

  @Override
  public void visitCode() {

    boolean withDatabase = WITH_DATABASE_ARGUMENT.equals(desc);
    boolean withEbeanServer = !withDatabase && WITH_EBEANSERVER_ARGUMENT.equals(desc);

    mv = cv.visitMethod(ACC_PUBLIC, INIT, desc, signature, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn(Type.getType("L" + domainClass + ";"));
    if (withDatabase) {
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, TQ_ROOT_BEAN, INIT, "(Ljava/lang/Class;Lio/ebean/Database;)V", false);
    } else if (withEbeanServer) {
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, TQ_ROOT_BEAN, INIT, "(Ljava/lang/Class;Lio/ebean/EbeanServer;)V", false);
    } else {
      mv.visitMethodInsn(INVOKESPECIAL, TQ_ROOT_BEAN, INIT, "(Ljava/lang/Class;)V", false);
    }

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(2, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, classInfo.getClassName(), "setRoot", "(Ljava/lang/Object;)V", false);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(3, l2);
    mv.visitInsn(RETURN);
    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLocalVariable("this", "L" + classInfo.getClassName() + ";", null, l0, l3, 0);
    if (withDatabase) {
      mv.visitLocalVariable("server", "Lio/ebean/Database;", null, l0, l3, 1);
      mv.visitMaxs(3, 2);
    } else if (withEbeanServer) {
      mv.visitLocalVariable("server", "Lio/ebean/EbeanServer;", null, l0, l3, 1);
      mv.visitMaxs(3, 2);
    } else {
      mv.visitMaxs(2, 1);
    }
    mv.visitEnd();
  }

}
