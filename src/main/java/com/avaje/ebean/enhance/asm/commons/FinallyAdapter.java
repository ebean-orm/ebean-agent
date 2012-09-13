package com.avaje.ebean.enhance.asm.commons;

import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodVisitor;

public class FinallyAdapter extends AdviceAdapter {
	
	private String name;
	private Label startFinally = new Label();

	public FinallyAdapter(MethodVisitor mv, int acc, String name, String desc) {
		super(mv, acc, name, desc);
		this.name = name;
	}

	public void visitCode() {
		super.visitCode();
		mv.visitLabel(startFinally);
	}

	public void visitMaxs(int maxStack, int maxLocals) {

		Label endFinally = new Label();
		mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
		mv.visitLabel(endFinally);
		onFinally(ATHROW);
		mv.visitInsn(ATHROW);
		mv.visitMaxs(maxStack, maxLocals);
	}

	protected void onMethodExit(int opcode) {
		if (opcode != ATHROW) {
			onFinally(opcode);
		}
	}

	private void onFinally(int opcode) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("Exiting " + name);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
	}

}
