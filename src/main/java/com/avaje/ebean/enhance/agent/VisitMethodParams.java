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
