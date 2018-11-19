package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;

/**
 * Adds a default constructor for the cases when there is not one already defined.
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
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classMeta.getSuperClassName(), "<init>", "()V", false);
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
