/**
 * Copyright (C) 2009 Authors
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
import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Adds a default constructor for the cases when there is not one already defined.
 * 
 * @author rbygrave
 *
 */
public class DefaultConstructor {

    /**
     * Adds a default constructor.
     */
    public static void add(ClassVisitor cw, ClassMeta classMeta) {
        
        if (classMeta.isLog(3)) {
            classMeta.log("... adding default constructor, super class: "+classMeta.getSuperClassName());
        }
        
        MethodVisitor underlyingMV = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        
        ConstructorAdapter mv = new ConstructorAdapter(underlyingMV, classMeta, "()V");
        
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(1, l0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classMeta.getSuperClassName(), "<init>", "()V");
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(2, l1);
        mv.visitInsn(Opcodes.RETURN);
        
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", "L"+classMeta.getClassName()+";", null, l0, l2, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}
