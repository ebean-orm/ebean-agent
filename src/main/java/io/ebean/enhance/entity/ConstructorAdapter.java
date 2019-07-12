package io.ebean.enhance.entity;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Attribute;
import io.ebean.enhance.asm.Handle;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.asm.TypePath;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Modify the constructor to additionally initialise the entityBeanIntercept
 * field.
 *
 * <pre class="code">
 * // added into constructor
 * _ebean_intercept = new EntityBeanIntercept(this);
 * </pre>
 */
class ConstructorAdapter extends MethodVisitor implements EnhanceConstants, Opcodes {

  private final ClassMeta meta;

  private final String className;

  private final String constructorDesc;

  private boolean constructorInitializationDone;

  /**
   * Holds an init instructions to see if it is an init of a OneToMany or ManyToMany.
   */
  private final ConstructorDeferredCode deferredCode;

  ConstructorAdapter(MethodVisitor mv, ClassMeta meta, String constructorDesc) {
    super(Opcodes.ASM7, mv);
    this.meta = meta;
    this.className = meta.getClassName();
    this.constructorDesc = constructorDesc;
    this.deferredCode = new ConstructorDeferredCode(meta, mv);
  }

  @Override
  public void visitVarInsn(int opcode, int var) {
    if (!deferredCode.deferVisitVarInsn(opcode, var)) {
      super.visitVarInsn(opcode, var);
    }
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    if (!deferredCode.deferVisitTypeInsn(opcode, type)) {
      super.visitTypeInsn(opcode, type);
    }
  }

  @Override
  public void visitInsn(int opcode) {
    if (!deferredCode.deferVisitInsn(opcode)) {
      super.visitInsn(opcode);
    }
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    if (!deferredCode.deferVisitMethodInsn(opcode, owner, name, desc, itf)) {
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      addInitialisationIfRequired(opcode, owner, name, desc);
    }
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {

    if (deferredCode.consumeVisitFieldInsn(opcode, name)) {
      // we have removed all the instructions initialising the many property
      return;
    }
    if (!className.equals(owner)) {
      super.visitFieldInsn(opcode, owner, name, desc);
      return;
    }
    FieldMeta fieldMeta = meta.getFieldPersistent(name);
    if (fieldMeta == null || !fieldMeta.isPersistent()) {
      // leave transient fields in constructor alone
      if (meta.isLog(4)) {
        meta.log("... visitFieldInsn (in constructor but non-persistent)- " + opcode + " owner:" + owner + ":" + name + ":" + desc);
      }
      super.visitFieldInsn(opcode, owner, name, desc);

    } else {
      if (opcode == PUTFIELD) {
        // intercept persistent PUTFIELD in the constructor
        String methodName = "_ebean_set_" + name;
        String methodDesc = "(" + desc + ")V";
        if (meta.isLog(3)) {
          meta.log("... Constructor PUTFIELD replaced with:" + methodName + methodDesc);
        }
        super.visitMethodInsn(INVOKEVIRTUAL, className, methodName, methodDesc, false);

      } else if (opcode == GETFIELD && fieldMeta.isMany()) {
        // intercept persistent many GETFIELD in the constructor to initialise the collection
        String methodName = "_ebean_get_" + name;
        String methodDesc = "()" + desc;
        if (meta.isLog(3)) {
          meta.log("... Constructor GETFIELD:" + name + " replaced with: " + methodName + methodDesc);
        }
        super.visitMethodInsn(INVOKEVIRTUAL, className, methodName, methodDesc, false);

      } else {
        if (meta.isLog(3)) {
          meta.log("... visitFieldInsn (unaltered in constructor)- " + opcode + " owner:" + owner + ":" + name + ":" + desc);
        }
        super.visitFieldInsn(opcode, owner, name, desc);
      }
    }
  }

  /**
   * Add initialisation of EntityBeanIntercept to constructor.
   *
   * <pre>
   * _ebean_intercept = new EntityBeanIntercept(this);
   * </pre>
   */
  private void addInitialisationIfRequired(int opcode, String owner, String name, String desc) {

    if (C_MODEL.equals(owner) && INIT.equals(name)) {
      addConstructorInit(owner);
      return;
    }

    if (opcode == INVOKESPECIAL && name.equals(INIT) && desc.equals(NOARG_VOID)) {
      if (meta.isSuperClassEntity()) {
        if (meta.isLog(3)) {
          meta.log("... skipping intercept <init> ... handled by super class... CONSTRUCTOR: owner:" + owner + " " + constructorDesc);
        }
      } else if (owner.equals(meta.getClassName())) {
        if (meta.isLog(3)) {
          meta.log("... skipping intercept <init> ... handled by other constructor... CONSTRUCTOR: owner:" + owner + " " + constructorDesc);
        }
      } else if (owner.equals(meta.getSuperClassName())) {
        addConstructorInit(owner);
      } else {
        if (meta.isLog(3)) {
          meta.log("... skipping intercept <init> ... incorrect type " + owner);
        }
      }
    }
  }

  private void addConstructorInit(String owner) {

    if (meta.isLog(2)) {
      meta.log("... adding intercept <init> in CONSTRUCTOR:" + constructorDesc + " OWNER/SUPER:" + owner);
    }

    if (constructorInitializationDone) {
      // hopefully this is never called but put it in here to be on the safe side.
      String msg = "Error in Enhancement. Only expecting to add <init> of intercept object"
        + " once but it is trying to add it twice for " + meta.getClassName() + " CONSTRUCTOR:"
        + constructorDesc + " OWNER:" + owner;
      System.err.println(msg);

    } else {
      // add the initialisation of the intercept object
      super.visitVarInsn(ALOAD, 0);
      super.visitTypeInsn(NEW, C_INTERCEPT);
      super.visitInsn(DUP);
      super.visitVarInsn(ALOAD, 0);

      super.visitMethodInsn(INVOKESPECIAL, C_INTERCEPT, INIT, "(Ljava/lang/Object;)V", false);
      super.visitFieldInsn(PUTFIELD, className, INTERCEPT_FIELD, EnhanceConstants.L_INTERCEPT);

      if (meta.isLog(8)) {
        meta.log("... constructorInitializationDone " + owner);
      }
      constructorInitializationDone = true;
    }
  }


  @Override
  public void visitParameter(String name, int access) {
    deferredCode.flush();
    super.visitParameter(name, access);
  }

  @Override
  public AnnotationVisitor visitAnnotationDefault() {
    deferredCode.flush();
    return super.visitAnnotationDefault();
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    deferredCode.flush();
    return super.visitAnnotation(desc, visible);
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    deferredCode.flush();
    return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
  }

  @Override
  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
    deferredCode.flush();
    return super.visitParameterAnnotation(parameter, desc, visible);
  }

  @Override
  public void visitAttribute(Attribute attr) {
    deferredCode.flush();
    super.visitAttribute(attr);
  }

  @Override
  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    deferredCode.flush();
    super.visitFrame(type, nLocal, local, nStack, stack);
  }

  @Override
  public void visitIntInsn(int opcode, int operand) {
    deferredCode.flush();
    super.visitIntInsn(opcode, operand);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    deferredCode.flush();
    super.visitMethodInsn(opcode, owner, name, desc);
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
    deferredCode.flush();
    super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
  }

  @Override
  public void visitJumpInsn(int opcode, Label label) {
    deferredCode.flush();
    super.visitJumpInsn(opcode, label);
  }

  @Override
  public void visitLabel(Label label) {
    deferredCode.flush();
    super.visitLabel(label);
  }

  @Override
  public void visitLdcInsn(Object cst) {
    deferredCode.flush();
    super.visitLdcInsn(cst);
  }

  @Override
  public void visitIincInsn(int var, int increment) {
    deferredCode.flush();
    super.visitIincInsn(var, increment);
  }

  @Override
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    deferredCode.flush();
    super.visitTableSwitchInsn(min, max, dflt, labels);
  }

  @Override
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    deferredCode.flush();
    super.visitLookupSwitchInsn(dflt, keys, labels);
  }

  @Override
  public void visitMultiANewArrayInsn(String desc, int dims) {
    deferredCode.flush();
    super.visitMultiANewArrayInsn(desc, dims);
  }

  @Override
  public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    deferredCode.flush();
    return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
  }

  @Override
  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    deferredCode.flush();
    super.visitTryCatchBlock(start, end, handler, type);
  }

  @Override
  public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    deferredCode.flush();
    return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
  }

  @Override
  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    deferredCode.flush();
    super.visitLocalVariable(name, desc, signature, start, end, index);
  }

  @Override
  public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
    deferredCode.flush();
    return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
  }

  @Override
  public void visitLineNumber(int line, Label start) {
    deferredCode.flush();
    super.visitLineNumber(line, start);
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    deferredCode.flush();
    super.visitMaxs(maxStack, maxLocals);
  }

  @Override
  public void visitEnd() {
    deferredCode.flush();
    super.visitEnd();
  }
}
