package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.asm.Type;
import io.ebean.enhance.asm.commons.FinallyAdapter;
import io.ebean.enhance.common.AnnotationInfo;
import io.ebean.enhance.common.AnnotationInfoVisitor;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.common.VisitUtil;

import java.util.ArrayList;

/**
 * Adapts a method to support Transactional.
 * <p>
 * Adds a TxScope and ScopeTrans local variables. On normal exit makes a call
 * out via InternalServer to end the scopeTrans depending on the exit type
 * opcode (ATHROW vs ARETURN etc) and whether particular throwable's cause a
 * rollback or not.
 * </p>
 */
public class ScopeTransAdapter extends FinallyAdapter implements EnhanceConstants {


	private static final Type txScopeType = Type.getType("L"+C_TXSCOPE+";");
	private static final Type scopeTransType = Type.getType(L_SCOPETRANS);
	private static final Type helpScopeTrans = Type.getType(L_HELPSCOPETRANS);

	private final AnnotationInfo annotationInfo;

	private final ClassAdapterTransactional owner;

  private final String methodName;

	private boolean transactional;

	private int posTxScope;
	private int posScopeTrans;
	private int lineNumber;
	private TransactionalMethodKey methodKey;

	ScopeTransAdapter(ClassAdapterTransactional owner, final MethodVisitor mv, final int access, final String name, final String desc) {
		super(Opcodes.ASM5, mv, access, name, desc);
		this.owner = owner;
		this.methodName = name;

		// inherit from class level Transactional annotation
		AnnotationInfo parentInfo = owner.getClassAnnotationInfo();
		
		// inherit from interface method transactional annotation
		AnnotationInfo interfaceInfo = owner.getInterfaceTransactionalInfo(name, desc);
		if (parentInfo == null){
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
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		if (lineNumber == 0 && methodKey != null) {
			lineNumber = line;
			methodKey.setLineNumber(lineNumber);
		}
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationVisitor av = super.visitAnnotation(desc, visible);
		if (desc.equals(AVAJE_TRANSACTIONAL_ANNOTATION)) {
			transactional = true;
			return new AnnotationInfoVisitor(null, annotationInfo, av);
		} else {
			return av;
		}
	}

	private void setTxType(Object txType){
		
		mv.visitVarInsn(ALOAD, posTxScope);
		mv.visitLdcInsn(txType.toString());
		mv.visitMethodInsn(INVOKESTATIC, C_TXTYPE, "valueOf", "(Ljava/lang/String;)L"+C_TXTYPE+";", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setType", "(L"+C_TXTYPE+";)L"+C_TXSCOPE+";", false);
		mv.visitInsn(POP);
	}
	
	private void setTxIsolation(Object txIsolation){
		
		mv.visitVarInsn(ALOAD, posTxScope);
		mv.visitLdcInsn(txIsolation.toString());
		mv.visitMethodInsn(INVOKESTATIC, C_TXISOLATION, "valueOf", "(Ljava/lang/String;)L"+C_TXISOLATION+";", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setIsolation", "(L"+C_TXISOLATION+";)L"+C_TXSCOPE+";", false);
		mv.visitInsn(POP);
	}

  private void setBatch(Object batch){

    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitLdcInsn(batch.toString());
    mv.visitMethodInsn(INVOKESTATIC, C_PERSISTBATCH, "valueOf", "(Ljava/lang/String;)L"+C_PERSISTBATCH+";", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setBatch", "(L"+C_PERSISTBATCH+";)L"+C_TXSCOPE+";", false);
    mv.visitInsn(POP);
  }

  private void setBatchOnCascade(Object batch){

    mv.visitVarInsn(ALOAD, posTxScope);
    mv.visitLdcInsn(batch.toString());
    mv.visitMethodInsn(INVOKESTATIC, C_PERSISTBATCH, "valueOf", "(Ljava/lang/String;)L"+C_PERSISTBATCH+";", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setBatchOnCascade", "(L"+C_PERSISTBATCH+";)L"+C_TXSCOPE+";", false);
    mv.visitInsn(POP);
  }

	private void setProfileId(int profileId){

		mv.visitVarInsn(ALOAD, posTxScope);
		VisitUtil.visitIntInsn(mv, profileId);
		mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setProfileId", "(I)L"+C_TXSCOPE+";", false);
		mv.visitInsn(POP);
	}

  private void setBatchSize(Object batchSize){

    mv.visitVarInsn(ALOAD, posTxScope);
    VisitUtil.visitIntInsn(mv, Integer.parseInt(batchSize.toString()));
    mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setBatchSize", "(I)L"+C_TXSCOPE+";", false);
    mv.visitInsn(POP);
  }
	
	private void setServerName(Object serverName){
		
		mv.visitVarInsn(ALOAD, posTxScope);
		mv.visitLdcInsn(serverName.toString());
		mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setServerName", "(Ljava/lang/String;)L"+C_TXSCOPE+";", false);
		mv.visitInsn(POP);
	}

  private void setGetGeneratedKeys(Object getGeneratedKeys){
    boolean getKeys = (Boolean)getGeneratedKeys;
    if (!getKeys) {
      mv.visitVarInsn(ALOAD, posTxScope);
      mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setSkipGeneratedKeys", "()L"+C_TXSCOPE+";", false);
    }
  }

	private void setReadOnly(Object readOnlyObj){

		boolean readOnly = (Boolean)readOnlyObj;
		mv.visitVarInsn(ALOAD, posTxScope);
		if (readOnly){
			mv.visitInsn(ICONST_1);
		} else {
			mv.visitInsn(ICONST_0);
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setReadOnly", "(Z)L"+C_TXSCOPE+";", false);
	}

	private void setFlushOnQuery(Object flushObj){
		boolean flushOnQuery = (Boolean)flushObj;
		if (!flushOnQuery){
			mv.visitVarInsn(ALOAD, posTxScope);
			mv.visitInsn(ICONST_0);
			mv.visitMethodInsn(INVOKEVIRTUAL, C_TXSCOPE, "setFlushOnQuery", "(Z)L"+C_TXSCOPE+";", false);
		}
	}

	/**
	 * Add bytecode to add the noRollbackFor throwable types to the TxScope.
	 */
	private void setNoRollbackFor(Object noRollbackFor){

		ArrayList<?> list = (ArrayList<?>)noRollbackFor;
		
		for (int i = 0; i < list.size(); i++) {
			
			Type throwType =  (Type)list.get(i);
			
			mv.visitVarInsn(ALOAD, posTxScope);
			mv.visitLdcInsn(throwType);
			mv.visitMethodInsn(INVOKEVIRTUAL, txScopeType.getInternalName(), "setNoRollbackFor", "(Ljava/lang/Class;)L"+C_TXSCOPE+";", false);
			mv.visitInsn(POP);
		}
	}
	
	/**
	 * Add bytecode to add the rollbackFor throwable types to the TxScope.
	 */
	private void setRollbackFor(Object rollbackFor){

		ArrayList<?> list = (ArrayList<?>)rollbackFor;
		
		for (int i = 0; i < list.size(); i++) {
			
			Type throwType =  (Type)list.get(i);
			
			mv.visitVarInsn(ALOAD, posTxScope);
			mv.visitLdcInsn(throwType);
			mv.visitMethodInsn(INVOKEVIRTUAL, txScopeType.getInternalName(), "setRollbackFor", "(Ljava/lang/Class;)L"+C_TXSCOPE+";", false);
			mv.visitInsn(POP);
		}
	}

	/**
	 * Return the profileId from the transactional annotation.
	 */
	private int annotationProfileId() {
		Object value = annotationInfo.getValue("profileId");
		if (value == null) {
			return 0;
		} else {
			return (int)value;
		}
	}

	@Override
	protected void onMethodEnter() {

		if (!transactional) {
			return;
		}

		methodKey = owner.createMethodKey(methodName, methodDesc, annotationProfileId());

		posTxScope = newLocal(txScopeType);
		posScopeTrans = newLocal(scopeTransType);

		mv.visitTypeInsn(NEW, txScopeType.getInternalName());
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, txScopeType.getInternalName(), "<init>", "()V", false);
		mv.visitVarInsn(ASTORE, posTxScope);

		Object txType = annotationInfo.getValue("type");
		if (txType != null){
			setTxType(txType);
		}
		int profileId = methodKey.getProfileId();
		if (profileId > 0) {
			setProfileId(profileId);
		}

		Object txIsolation = annotationInfo.getValue("isolation");
		if (txIsolation != null){
			setTxIsolation(txIsolation);
		}

    Object batch = annotationInfo.getValue("batch");
    if (batch != null){
      setBatch(batch);
    }

    Object batchOnCascade = annotationInfo.getValue("batchOnCascade");
    if (batchOnCascade != null){
      setBatchOnCascade(batchOnCascade);
    }

    Object batchSize = annotationInfo.getValue("batchSize");
    if (batchSize != null){
      setBatchSize(batchSize);
    }

		Object getGeneratedKeys = annotationInfo.getValue("getGeneratedKeys");
		if (getGeneratedKeys != null){
			setGetGeneratedKeys(getGeneratedKeys);
		}

    Object readOnly = annotationInfo.getValue("readOnly");
		if (readOnly != null){
			setReadOnly(readOnly);
		}

		Object flushOnQuery = annotationInfo.getValue("flushOnQuery");
		if (flushOnQuery != null){
			setFlushOnQuery(flushOnQuery);
		}

		
		Object noRollbackFor = annotationInfo.getValue("noRollbackFor");
		if (noRollbackFor != null){
			setNoRollbackFor(noRollbackFor);
		}
		
		Object rollbackFor = annotationInfo.getValue("rollbackFor");
		if (rollbackFor != null){
			setRollbackFor(rollbackFor);
		}

		Object serverName = annotationInfo.getValue("serverName");
		if (serverName != null && !serverName.equals("")){
			setServerName(serverName);
		}

		mv.visitVarInsn(ALOAD, posTxScope);
		mv.visitMethodInsn(INVOKESTATIC, helpScopeTrans.getInternalName(), "createScopeTrans", "("
				+ txScopeType.getDescriptor() + ")" + scopeTransType.getDescriptor(), false);
		mv.visitVarInsn(ASTORE, posScopeTrans);
	}


	@Override
  protected void onFinally(int opcode) {

		if (!transactional) {
			return;
		}

		owner.transactionalMethod(methodKey);
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
			box(Type.getReturnType(this.methodDesc));
		}
		visitIntInsn(SIPUSH, opcode);
		loadLocal(posScopeTrans);

		visitMethodInsn(INVOKESTATIC, helpScopeTrans.getInternalName(), "onExitScopeTrans", "(Ljava/lang/Object;I"
				+ scopeTransType.getDescriptor() + ")V", false);
	}

}
