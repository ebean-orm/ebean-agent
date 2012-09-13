/**
 * Copyright (C) 2006  Robin Bygrave
 * 
 * This file is part of Ebean.
 * 
 * Ebean is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *  
 * Ebean is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ebean; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA  
 */
package com.avaje.ebean.enhance.agent;

import java.util.List;

import com.avaje.ebean.enhance.asm.ClassVisitor;
import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Generate the methods based on the list of fields.
 * <p>
 * This includes the createCopy, getField and setField methods etc.
 * </p>
 */
public class IndexFieldWeaver implements Opcodes {

	public static void addMethods(ClassVisitor cv, ClassMeta classMeta) {

		List<FieldMeta> fields = classMeta.getAllFields();
		if (fields.size() == 0) {
			classMeta.log("Has no fields?");
			return;
		}

		if (classMeta.isLog(3)) {
			classMeta.log("fields size:" + fields.size()+" "+fields.toString());
		}
		
		generateCreateCopy(cv, classMeta, fields);
		generateGetField(cv, classMeta, fields, false);
		generateGetField(cv, classMeta, fields, true);

		generateSetField(cv, classMeta, fields, false);
		generateSetField(cv, classMeta, fields, true);

		generateGetDesc(cv, classMeta, fields);

		if (classMeta.hasEqualsOrHashCode()) {
			// equals or hashCode is already implemented
			if (classMeta.isLog(1)) {
				classMeta.log("... skipping add equals() ... already has equals() hashcode() methods");
			}
			return;
		}

		// search for the id field...
		int idIndex = -1;
		FieldMeta idFieldMeta = null;

		// find id field only local to this class
		for (int i = 0; i < fields.size(); i++) {
			FieldMeta fieldMeta = fields.get(i);
			if (fieldMeta.isId() && fieldMeta.isLocalField(classMeta)) {
				if (idIndex == -1) {
					// we have found an id field
					idIndex = i;
					idFieldMeta = fieldMeta;
				} else {
					// there are 2 or more id fields
					idIndex = -2;
				}
			}
		}

		if (idIndex == -2) {
			// there are 2 or more id fields?
			if (classMeta.isLog(1)) {
				classMeta.log("has 2 or more id fields. Not adding equals() method.");
			}

		} else if (idIndex == -1) {
			// there are no id fields local to this type
			if (classMeta.isLog(1)) {
				classMeta.log("has no id fields on this type. Not adding equals() method. Expected when Id property on superclass.");
			}

		} else {
			// add the _ebean_getIdentity(), equals() and hashCode() methods
			MethodEquals.addMethods(cv, classMeta, idIndex, idFieldMeta);
		}
	}

	/**
	 * Generate the invokeGet method.
	 */
	private static void generateGetField(ClassVisitor cv, ClassMeta classMeta, List<FieldMeta> fields,
			boolean intercept) {

		String className = classMeta.getClassName();

		MethodVisitor mv = null;
		if (intercept) {
			mv = cv.visitMethod(ACC_PUBLIC, "_ebean_getFieldIntercept", "(ILjava/lang/Object;)Ljava/lang/Object;",null, null);
		} else {
			mv = cv.visitMethod(ACC_PUBLIC, "_ebean_getField", "(ILjava/lang/Object;)Ljava/lang/Object;", null, null);
		}

		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitTypeInsn(CHECKCAST, className);
		mv.visitVarInsn(ASTORE, 3);
		Label l1 = new Label();
		mv.visitLabel(l1);

		mv.visitLineNumber(1, l1);
		mv.visitVarInsn(ILOAD, 1);

		Label[] switchLabels = new Label[fields.size()];
		for (int i = 0; i < switchLabels.length; i++) {
			switchLabels[i] = new Label();
		}

		int maxIndex = switchLabels.length - 1;

		Label labelException = new Label();
		mv.visitTableSwitchInsn(0, maxIndex, labelException, switchLabels);

		for (int i = 0; i < fields.size(); i++) {

			FieldMeta fieldMeta = fields.get(i);

			mv.visitLabel(switchLabels[i]);
			mv.visitLineNumber(1, switchLabels[i]);
			mv.visitVarInsn(ALOAD, 3);

			fieldMeta.appendSwitchGet(mv, classMeta, intercept);

			mv.visitInsn(ARETURN);
		}

		mv.visitLabel(labelException);
		mv.visitLineNumber(1, labelException);
		mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("Invalid index ");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
		mv.visitVarInsn(ILOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V");
		mv.visitInsn(ATHROW);
		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitLocalVariable("this", "L" + className + ";", null, l0, l5, 0);
		mv.visitLocalVariable("index", "I", null, l0, l5, 1);
		mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l0, l5, 2);
		mv.visitLocalVariable("p", "L" + className + ";", null, l1, l5, 3);
		mv.visitMaxs(5, 4);
		mv.visitEnd();
	}

	/**
	 * Generate the _ebean_setField or _ebean_setFieldBypass method.
	 * <p>
	 * Bypass will bypass the interception. The interception checks that the
	 * property has been loaded and creates oldValues if the bean is being made
	 * dirty for the first time.
	 * </p>
	 */
	private static void generateSetField(ClassVisitor cv, ClassMeta classMeta, List<FieldMeta> fields,boolean intercept) {

		
		String className = classMeta.getClassName();

		MethodVisitor mv = null;
		if (intercept) {
			mv = cv.visitMethod(ACC_PUBLIC, "_ebean_setFieldIntercept", "(ILjava/lang/Object;Ljava/lang/Object;)V",
				null, null);
		} else {
			mv = cv.visitMethod(ACC_PUBLIC, "_ebean_setField", "(ILjava/lang/Object;Ljava/lang/Object;)V", null, null);
		}

		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitTypeInsn(CHECKCAST, className);
		mv.visitVarInsn(ASTORE, 4);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(1, l1);
		mv.visitVarInsn(ILOAD, 1);

		Label[] switchLabels = new Label[fields.size()];
		for (int i = 0; i < switchLabels.length; i++) {
			switchLabels[i] = new Label();
		}

		Label labelException = new Label();

		int maxIndex = switchLabels.length - 1;

		mv.visitTableSwitchInsn(0, maxIndex, labelException, switchLabels);

		for (int i = 0; i < fields.size(); i++) {
			FieldMeta fieldMeta = fields.get(i);

			mv.visitLabel(switchLabels[i]);
			mv.visitLineNumber(1, switchLabels[i]);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 3);

			fieldMeta.appendSwitchSet(mv, classMeta, intercept);

			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLineNumber(1, l6);
			mv.visitInsn(RETURN);

		}

		mv.visitLabel(labelException);
		mv.visitLineNumber(1, labelException);
		mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("Invalid index ");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
		mv.visitVarInsn(ILOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V");
		mv.visitInsn(ATHROW);
		Label l9 = new Label();
		mv.visitLabel(l9);
		mv.visitLocalVariable("this", "L" + className + ";", null, l0, l9, 0);
		mv.visitLocalVariable("index", "I", null, l0, l9, 1);
		mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l0, l9, 2);
		mv.visitLocalVariable("arg", "Ljava/lang/Object;", null, l0, l9, 3);
		mv.visitLocalVariable("p", "L" + className + ";", null, l1, l9, 4);
		mv.visitMaxs(5, 5);
		mv.visitEnd();
	}

	/**
	 * Generate the _ebean_createCopy() method.
	 */
	private static void generateCreateCopy(ClassVisitor cv, ClassMeta classMeta, List<FieldMeta> fields) {

		String className = classMeta.getClassName();

		String copyClassName = className;
		if (classMeta.isSubclassing()) {
			copyClassName = classMeta.getSuperClassName();
		}

		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "_ebean_createCopy", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitTypeInsn(NEW, copyClassName);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, copyClassName, "<init>", "()V");
		mv.visitVarInsn(ASTORE, 1);

		Label l1 = null;
		for (int i = 0; i < fields.size(); i++) {

			FieldMeta fieldMeta = fields.get(i);
			if (fieldMeta.isPersistent()){
				// only copy persistent fields
				Label label = new Label();
				if (i == 0) {
					l1 = label;
				}
				mv.visitLabel(label);
				mv.visitLineNumber(1, label);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 0);
	
				// get put the fields or if using subclassing
				// then use the getter setter methods on the
				// super object
				fieldMeta.addFieldCopy(mv, classMeta);
			}
		}

		Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitLineNumber(1, l4);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitInsn(ARETURN);
		Label l5 = new Label();
		mv.visitLabel(l5);
		if (l1 == null){
			l1 = l4;
		}
		mv.visitLocalVariable("this", "L" + className + ";", null, l0, l5, 0);
		mv.visitLocalVariable("p", "L" + copyClassName + ";", null, l1, l5, 1);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private static void generateGetDesc(ClassVisitor cv, ClassMeta classMeta, List<FieldMeta> fields) {

		String className = classMeta.getClassName();

		int size = fields.size();

		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "_ebean_getFieldNames", "()[Ljava/lang/String;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		visitIntInsn(mv, size);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/String");

		for (int i = 0; i < size; i++) {
			FieldMeta fieldMeta = fields.get(i);
			mv.visitInsn(DUP);
			visitIntInsn(mv, i);
			mv.visitLdcInsn(fieldMeta.getName());
			mv.visitInsn(AASTORE);
		}

		mv.visitInsn(ARETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + className + ";", null, l0, l1, 0);
		mv.visitMaxs(4, 1);
		mv.visitEnd();

	}

	/**
	 * Helper method for visiting an int value.
	 * <p>
	 * This can use special constant values for int values from 0 to 5.
	 * </p>
	 */
	public static void visitIntInsn(MethodVisitor mv, int value) {

		switch (value) {
		case 0:
			mv.visitInsn(ICONST_0);
			break;
		case 1:
			mv.visitInsn(ICONST_1);
			break;
		case 2:
			mv.visitInsn(ICONST_2);
			break;
		case 3:
			mv.visitInsn(ICONST_3);
			break;
		case 4:
			mv.visitInsn(ICONST_4);
			break;
		case 5: 
			mv.visitInsn(ICONST_5);
			break;
		default:
			if (value <= Byte.MAX_VALUE){
				mv.visitIntInsn(BIPUSH, value);
			} else {
				mv.visitIntInsn(SIPUSH, value);	
			}
		}
	}
}
