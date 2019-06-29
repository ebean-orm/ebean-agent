package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Attribute;
import io.ebean.enhance.asm.Handle;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.asm.TypePath;

/**
 * Changes the existing constructor removing all the existing code to be replaced via visitCode() implementation.
 */
abstract class BaseConstructorAdapter extends MethodVisitor implements Opcodes {

  /**
   * Construct for a query bean class given its associated entity bean domain class and a class visitor.
   */
  BaseConstructorAdapter() {
    super(Opcodes.ASM7, null);
  }

  @Override
  public abstract void visitCode();


  @Override
  public void visitParameter(String name, int access) {
    // do nothing / consume existing
  }

  @Override
  public AnnotationVisitor visitAnnotationDefault() {
    // do nothing / consume existing
    return null;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    // do nothing / consume existing
    return null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    // do nothing / consume existing
    return null;
  }

  @Override
  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
    // do nothing / consume existing
    return null;
  }

  @Override
  public void visitAttribute(Attribute attr) {
    // do nothing / consume existing
  }

  @Override
  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    // do nothing / consume existing
  }

  @Override
  public void visitInsn(int opcode) {
    // do nothing / consume existing
  }

  @Override
  public void visitIntInsn(int opcode, int operand) {
    // do nothing / consume existing
  }

  @Override
  public void visitVarInsn(int opcode, int var) {
    // do nothing / consume existing
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    // do nothing / consume existing
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    // do nothing / consume existing
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    // do nothing / consume existing
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    // do nothing / consume existing
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
    // do nothing / consume existing
  }

  @Override
  public void visitJumpInsn(int opcode, Label label) {
    // do nothing / consume existing
  }

  @Override
  public void visitLabel(Label label) {
    // do nothing / consume existing
  }

  @Override
  public void visitLdcInsn(Object cst) {
    // do nothing / consume existing
  }

  @Override
  public void visitIincInsn(int var, int increment) {
    // do nothing / consume existing
  }

  @Override
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    // do nothing / consume existing
  }

  @Override
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    // do nothing / consume existing
  }

  @Override
  public void visitMultiANewArrayInsn(String desc, int dims) {
    // do nothing / consume existing
  }

  @Override
  public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    // do nothing / consume existing
    return null;
  }

  @Override
  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    // do nothing / consume existing
  }

  @Override
  public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    // do nothing / consume existing
    return null;
  }

  @Override
  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    // do nothing / consume existing
  }

  @Override
  public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
    // do nothing / consume existing
    return null;
  }

  @Override
  public void visitLineNumber(int line, Label start) {
    // do nothing / consume existing
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    // do nothing / consume existing
  }

  @Override
  public void visitEnd() {
    // do nothing / consume existing
  }
}
