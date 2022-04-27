package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.asm.Type;
import io.ebean.enhance.common.AnnotationInfo;
import io.ebean.enhance.common.AnnotationInfoVisitor;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.common.VisitUtil;

import java.util.ArrayList;

/**
 * Adapts a method to support Transactional and profile location.
 * <p>
 * Adds a TxScope and ScopeTrans local variables. On normal exit makes a call
 * out via InternalServer to end the scopeTrans depending on the exit type
 * opcode (ATHROW vs ARETURN etc) and whether particular throwable's cause a
 * rollback or not.
 * </p>
 */
class MethodAdapter extends FinallyAdapter implements EnhanceConstants, Opcodes {

  private static final String TX_FIELD_PREFIX = ClassAdapterTransactional.TX_FIELD_PREFIX;
  private static final Type txScopeType = Type.getType("L" + C_TXSCOPE + ";");
  private static final Type helpScopeTrans = Type.getType(L_HELPSCOPETRANS);

  private final AnnotationInfo annotationInfo;
  private final ClassAdapterTransactional classAdapter;
  private final ProfileMethodInstruction profileMethod;
  private boolean transactional;
  private int posTxScope;

  MethodAdapter(ClassAdapterTransactional classAdapter, final MethodVisitor mv, final int access, final String name, final String desc) {
    super(mv, access, name, desc);
    this.classAdapter = classAdapter;
    this.profileMethod = new ProfileMethodInstruction(classAdapter, mv);
    // inherit from class level Transactional annotation
    AnnotationInfo parentInfo = classAdapter.getClassAnnotationInfo();

    // inherit from interface method transactional annotation
    AnnotationInfo interfaceInfo = classAdapter.getInterfaceTransactionalInfo(name, desc);
    if (parentInfo == null) {
      parentInfo = interfaceInfo;
    } else {
      parentInfo.setParent(interfaceInfo);
    }

    // inherit transactional annotations from parentInfo
    annotationInfo = new AnnotationInfo(parentInfo);

    // default based on whether Transactional annotation
    // is at the class level or on interface method
    transactional = parentInfo != null;
  }

  @Override
  public void visitCode() {
    super.visitCode();
    if (transactional) {
      finallyVisitStart();
    }
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    if (transactional) {
      finallyVisitMaxs(maxStack, maxLocals);
    } else {
      super.visitMaxs(maxStack, maxLocals);
    }
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    AnnotationVisitor av = super.visitAnnotation(desc, visible);
    if (desc.equals(TRANSACTIONAL_ANNOTATION)) {
      transactional = true;
      return new AnnotationInfoVisitor(null, annotationInfo, av);
    } else {
      return av;
    }
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    profileMethod.visitMethodInsn(opcode, owner, name, desc, itf);
  }

  private void setTxType(Object txType) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitFieldInsn(GETSTATIC, C_TXTYPE, txType.toString(), "L" + C_TXTYPE + ";");
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setType", "(L" + C_TXTYPE + ";)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void setTxIsolation(Object txIsolation) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitFieldInsn(GETSTATIC, C_TXISOLATION, txIsolation.toString(), "L" + C_TXISOLATION + ";");
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setIsolation", "(L" + C_TXISOLATION + ";)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void setTxProfileLocation(int locationField) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitFieldInsn(GETSTATIC, classAdapter.className(), TX_FIELD_PREFIX + locationField, "Lio/ebean/ProfileLocation;");
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setProfileLocation", "(Lio/ebean/ProfileLocation;)Lio/ebean/TxScope;", false);
    mv.visitInsn(POP);
  }

  private void setBatch(Object batch) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitFieldInsn(GETSTATIC, C_PERSISTBATCH, batch.toString(), "L" + C_PERSISTBATCH + ";");
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setBatch", "(L" + C_PERSISTBATCH + ";)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void visitLabelLine() {
    Label l6 = new Label();
    mv.visitLabel(l6);
    mv.visitLineNumber(1, l6);
  }

  private void setBatchOnCascade(Object batch) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitFieldInsn(GETSTATIC, C_PERSISTBATCH, batch.toString(), "L" + C_PERSISTBATCH + ";");
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setBatchOnCascade", "(L" + C_PERSISTBATCH + ";)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void setAutoPersistUpdates(Object autoPersistUpdates) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitFieldInsn(GETSTATIC, C_TXOPTION, autoPersistUpdates.toString(), "L" + C_TXOPTION + ";");
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setAutoPersistUpdates", "(L" + C_TXOPTION + ";)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void setBatchSize(Object batchSize) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    VisitUtil.visitIntInsn(mv, Integer.parseInt(batchSize.toString()));
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setBatchSize", "(I)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void setGetGeneratedKeys(Object getGeneratedKeys) {
    boolean getKeys = (Boolean) getGeneratedKeys;
    if (!getKeys) {
      visitLabelLine();
      mv.visitVarInsn(ALOAD, posTxScope);
      mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setSkipGeneratedKeys", "()L" + C_TXSCOPE + ";", false);
      mv.visitInsn(POP);
    }
  }

  private void setReadOnly(Object readOnlyObj) {
    visitLabelLine();
    boolean readOnly = (Boolean) readOnlyObj;
    mv.visitVarInsn(ALOAD, posTxScope);
    if (readOnly) {
      mv.visitInsn(ICONST_1);
    } else {
      mv.visitInsn(ICONST_0);
    }
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setReadOnly", "(Z)L" + C_TXSCOPE + ";", false);
    mv.visitInsn(POP);
  }

  private void setFlushOnQuery(Object flushObj) {
    boolean flushOnQuery = (Boolean) flushObj;
    if (!flushOnQuery) {
      visitLabelLine();
      mv.visitVarInsn(ALOAD, posTxScope);
      mv.visitInsn(ICONST_0);
      mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setFlushOnQuery", "(Z)L" + C_TXSCOPE + ";", false);
      mv.visitInsn(POP);
    }
  }

  private void setSkipCache(Object skipCacheObj) {
    boolean skipCache = (Boolean) skipCacheObj;
    if (skipCache) {
      visitLabelLine();
      mv.visitVarInsn(ALOAD, posTxScope);
      mv.visitInsn(ICONST_1);
      mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setSkipCache", "(Z)L" + C_TXSCOPE + ";", false);
      mv.visitInsn(POP);
    }
  }

  private void setLabel(String label) {
    visitLabelLine();
    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitLdcInsn(label);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setLabel", "(Ljava/lang/String;)Lio/ebean/TxScope;", false);
    mv.visitInsn(POP);
  }

  /**
   * Add bytecode to add the noRollbackFor throwable types to the TxScope.
   */
  private void setNoRollbackFor(Object noRollbackFor) {
    for (Object aList : (ArrayList<?>) noRollbackFor) {
      visitLabelLine();
      Type throwType = (Type) aList;
      mv.visitVarInsn(ALOAD, posTxScope);
      mv.visitLdcInsn(throwType);
      mv.visitMethodInsn(INVOKEVIRTUAL, txScopeType.getInternalName(), "setNoRollbackFor", "(Ljava/lang/Class;)L" + C_TXSCOPE + ";", false);
      mv.visitInsn(POP);
    }
  }

  /**
   * Add bytecode to add the rollbackFor throwable types to the TxScope.
   */
  private void setRollbackFor(Object rollbackFor) {
    for (Object aList : (ArrayList<?>) rollbackFor) {
      visitLabelLine();
      Type throwType = (Type) aList;
      mv.visitVarInsn(ALOAD, posTxScope);
      mv.visitLdcInsn(throwType);
      mv.visitMethodInsn(INVOKEVIRTUAL, txScopeType.getInternalName(), "setRollbackFor", "(Ljava/lang/Class;)L" + C_TXSCOPE + ";", false);
      mv.visitInsn(POP);
    }
  }

  @Override
  protected void onMethodEnter() {
    if (!transactional) {
      return;
    }
    int locationField = classAdapter.nextTransactionLocation();
    posTxScope = newLocal(txScopeType);

    mv.visitTypeInsn(NEW, txScopeType.getInternalName());
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, txScopeType.getInternalName(), INIT, NOARG_VOID, false);
    mv.visitVarInsn(ASTORE, posTxScope);

    Object txType = annotationInfo.getValue("type");
    if (txType != null) {
      setTxType(txType);
    }
    if (classAdapter.isEnableProfileLocation()) {
      setTxProfileLocation(locationField);
    }
    String txLabel = (String) annotationInfo.getValue("label");
    if (txLabel != null && !txLabel.isEmpty()) {
      classAdapter.putTxnLabel(locationField, txLabel);
      setLabel(txLabel);
    }

    Object txIsolation = annotationInfo.getValue("isolation");
    if (txIsolation != null) {
      setTxIsolation(txIsolation);
    }

    Object autoPersistUpdates = annotationInfo.getValue("autoPersistUpdates");
    if (autoPersistUpdates != null && !"DEFAULT".equals(autoPersistUpdates.toString())) {
      setAutoPersistUpdates(autoPersistUpdates);
    }

    Object batch = annotationInfo.getValue("batch");
    if (batch != null) {
      setBatch(batch);
    }

    Object batchOnCascade = annotationInfo.getValue("batchOnCascade");
    if (batchOnCascade != null) {
      setBatchOnCascade(batchOnCascade);
    }

    Object batchSize = annotationInfo.getValue("batchSize");
    if (batchSize != null) {
      setBatchSize(batchSize);
    }

    Object getGeneratedKeys = annotationInfo.getValue("getGeneratedKeys");
    if (getGeneratedKeys != null) {
      setGetGeneratedKeys(getGeneratedKeys);
    }

    Object readOnly = annotationInfo.getValue("readOnly");
    if (readOnly != null) {
      setReadOnly(readOnly);
    }

    Object flushOnQuery = annotationInfo.getValue("flushOnQuery");
    if (flushOnQuery != null) {
      setFlushOnQuery(flushOnQuery);
    }

    Object skipCache = annotationInfo.getValue("skipCache");
    if (skipCache != null) {
      setSkipCache(skipCache);
    }

    Object noRollbackFor = annotationInfo.getValue("noRollbackFor");
    if (noRollbackFor != null) {
      setNoRollbackFor(noRollbackFor);
    }

    Object rollbackFor = annotationInfo.getValue("rollbackFor");
    if (rollbackFor != null) {
      setRollbackFor(rollbackFor);
    }

    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitMethodInsn(INVOKESTATIC, helpScopeTrans.getInternalName(), "enter", "("
      + txScopeType.getDescriptor() + ")V", false);
  }


  @Override
  protected void onFinally(int opcode) {
    if (!transactional) {
      return;
    }
    if (opcode == RETURN) {
      visitInsn(ACONST_NULL);
    } else if (opcode == ARETURN || opcode == ATHROW) {
      dup();
    } else {
      if (opcode == LRETURN || opcode == DRETURN) {
        dup2();
      } else {
        dup();
      }
      box(getReturnType());
    }
    visitIntInsn(SIPUSH, opcode);
    visitMethodInsn(INVOKESTATIC, helpScopeTrans.getInternalName(), "exit", "(Ljava/lang/Object;I)V", false);
  }

}
