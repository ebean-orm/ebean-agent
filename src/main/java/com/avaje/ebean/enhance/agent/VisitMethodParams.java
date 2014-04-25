package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.ClassVisitor;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Utility object used to hold all the method parameters.
 */
public class VisitMethodParams {

    private final ClassVisitor cv;

    private int access;

    private final String name;

    private final String desc;

    private final String signiture;

    private final String[] exceptions;
    
    /**
     * Create with all the method parameters.
     */
    public VisitMethodParams(ClassVisitor cv, int access, String name, String desc,
            String signiture, String[] exceptions) {
        this.cv = cv;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.exceptions = exceptions;
        this.signiture = signiture;
    }
    
    /**
     * Force the access to the ACC_PUBLIC.
     */
    public boolean forcePublic() {
        if (access != Opcodes.ACC_PUBLIC){
            access = Opcodes.ACC_PUBLIC;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Visit the method.
     */
    public MethodVisitor visitMethod() {
        return cv.visitMethod(access, name, desc, signiture, exceptions);
    }

    /**
     * Return the method name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the method description.
     */
    public String getDesc() {
        return desc;
    }
    
    
}
