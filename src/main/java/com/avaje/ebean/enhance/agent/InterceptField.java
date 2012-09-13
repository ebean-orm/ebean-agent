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

import com.avaje.ebean.enhance.asm.ClassVisitor;
import com.avaje.ebean.enhance.asm.FieldVisitor;
import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Generate the _ebean_getIntercept() method and field.
 */
public class InterceptField implements Opcodes, EnhanceConstants {

    /**
     * Add the _ebean_intercept field.
     */
    public static void addField(ClassVisitor cv, boolean transientInternalFields) {

        int access = ACC_PROTECTED + (transientInternalFields ? ACC_TRANSIENT : 0);
        FieldVisitor f1 = cv.visitField(access, INTERCEPT_FIELD, L_INTERCEPT, null, null);
        f1.visitEnd();
    }

    /**
     * Generate the _ebean_getIntercept() method.
     * 
     * <pre>
     * public EntityBeanIntercept _ebean_getIntercept() {
     *     return _ebean_intercept;
     * }
     * </pre>
     */
    public static void addGetterSetter(ClassVisitor cv, String className) {

        String lClassName = "L" + className + ";";

        MethodVisitor mv;
        Label l0, l1;

        mv = cv.visitMethod(ACC_PUBLIC, "_ebean_getIntercept", "()" + L_INTERCEPT, null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(1, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
        mv.visitInsn(ARETURN);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", lClassName, null, l0, l1, 0);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        addInitInterceptMethod(cv, className);
    }

    /**
     * Add _ebean_intercept() method that includes initialisation of the
     * EntityBeanIntercept.
     * <p>
     * This is only required when transientInternalFields=true with enhancement.
     * In that case the EntityBeanIntercept is transient and can be null after
     * deserialization - in which case it needs to be initialised.
     * </p>
     * 
     * <pre>
     * public EntityBeanIntercept _ebean_intercept() {
     *     if (_ebean_intercept == null) {
     *         _ebean_intercept = new EntityBeanIntercept(this);
     *     }
     *     return _ebean_intercept;
     * }
     * </pre>
     */
    private static void addInitInterceptMethod(ClassVisitor cv, String className) {

        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "_ebean_intercept", "()" + L_INTERCEPT, null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(1, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(2, l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, C_INTERCEPT);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, C_INTERCEPT, "<init>", "(Ljava/lang/Object;)V");
        mv.visitFieldInsn(PUTFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
        mv.visitLabel(l1);
        mv.visitLineNumber(3, l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
        mv.visitInsn(ARETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("this", "L"+className+";", null, l0, l3, 0);
        mv.visitMaxs(4, 1);
        mv.visitEnd();
    }
}
