/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2007 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.avaje.ebean.enhance.asm.commons;

import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;
import com.avaje.ebean.enhance.asm.Type;

/**
 * Created as a modification of AdviceAdpater by removing support for
 * Constructor.
 * <p>
 * I did this to simplify the code so I could see what was happening.
 * </p>
 * 
 * @author AdviceAdpater by Eugene Kuleshov
 * @author AdviceAdpater by Eric Bruneton
 * @author Adapted from AdviceAdpater by Rob Bygrave
 */
public abstract class MethodAdviceAdapter extends GeneratorAdapter implements Opcodes {

	protected int methodAccess;
	
	protected String methodName;

	protected String methodDesc;

	/**
	 * Creates a new {@link MethodAdviceAdapter}.
	 * 
	 * @param mv
	 *            the method visitor to which this adapter delegates calls.
	 * @param access
	 *            the method's access flags (see {@link Opcodes}).
	 * @param name
	 *            the method's name.
	 * @param desc
	 *            the method's descriptor (see {@link Type Type}).
	 */
	protected MethodAdviceAdapter(final MethodVisitor mv, final int access, final String name, final String desc) {
		super(mv, access, name, desc);
		methodAccess = access;
		methodDesc = desc;
		methodName = name;

	}

	public void visitCode() {
		mv.visitCode();
		onMethodEnter();
	}

	public void visitLabel(final Label label) {
		mv.visitLabel(label);
	}

	public void visitInsn(final int opcode) {

		switch (opcode) {
		case RETURN:
		case IRETURN:
		case FRETURN:
		case ARETURN:
		case LRETURN:
		case DRETURN:
		case ATHROW:
			onMethodExit(opcode);
			break;
		}
		mv.visitInsn(opcode);
	}

	public void visitVarInsn(final int opcode, final int var) {
		super.visitVarInsn(opcode, var);
	}

	public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
		mv.visitFieldInsn(opcode, owner, name, desc);
	}

	public void visitIntInsn(final int opcode, final int operand) {
		mv.visitIntInsn(opcode, operand);
	}

	public void visitLdcInsn(final Object cst) {
		mv.visitLdcInsn(cst);
	}

	public void visitMultiANewArrayInsn(final String desc, final int dims) {
		mv.visitMultiANewArrayInsn(desc, dims);
	}

	public void visitTypeInsn(final int opcode, final String type) {
		mv.visitTypeInsn(opcode, type);
	}

	public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
		mv.visitMethodInsn(opcode, owner, name, desc);
	}

	public void visitJumpInsn(final int opcode, final Label label) {
		mv.visitJumpInsn(opcode, label);
	}

	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		mv.visitLookupSwitchInsn(dflt, keys, labels);
	}

	public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
		mv.visitTableSwitchInsn(min, max, dflt, labels);
	}

	/**
	 * Called at the beginning of the method or after super class class call in
	 * the constructor. <br>
	 * <br>
	 * 
	 * <i>Custom code can use or change all the local variables, but should not
	 * change state of the stack.</i>
	 */
	protected void onMethodEnter() {
	}

	/**
	 * Called before explicit exit from the method using either return or throw.
	 * Top element on the stack contains the return value or exception instance.
	 * For example:
	 * 
	 * <pre>
	 *   public void onMethodExit(int opcode) {
	 *     if(opcode==RETURN) {
	 *         visitInsn(ACONST_NULL);
	 *     } else if(opcode==ARETURN || opcode==ATHROW) {
	 *         dup();
	 *     } else {
	 *         if(opcode==LRETURN || opcode==DRETURN) {
	 *             dup2();
	 *         } else {
	 *             dup();
	 *         }
	 *         box(Type.getReturnType(this.methodDesc));
	 *     }
	 *     visitIntInsn(SIPUSH, opcode);
	 *     visitMethodInsn(INVOKESTATIC, owner, &quot;onExit&quot;, &quot;(Ljava/lang/Object;I)V&quot;);
	 *   }
	 * 
	 *   // an actual call back method
	 *   public static void onExit(int opcode, Object param) {
	 *     ...
	 * </pre>
	 * 
	 * <br>
	 * <br>
	 * 
	 * <i>Custom code can use or change all the local variables, but should not
	 * change state of the stack.</i>
	 * 
	 * @param opcode
	 *            one of the RETURN, IRETURN, FRETURN, ARETURN, LRETURN, DRETURN
	 *            or ATHROW
	 * 
	 */
	protected void onMethodExit(int opcode) {
	}

	// TODO onException, onMethodCall

}
