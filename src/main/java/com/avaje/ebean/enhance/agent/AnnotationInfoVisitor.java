package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.AnnotationVisitor;

/**
 * Reads the annotation information storing it in a AnnotationInfo.
 */
public class AnnotationInfoVisitor implements AnnotationVisitor {

	final AnnotationVisitor av;
	
	final AnnotationInfo info;
	
	final String prefix;
	
	public AnnotationInfoVisitor(String prefix, AnnotationInfo info, AnnotationVisitor av) {
		this.av = av;
		this.info = info;
		this.prefix = prefix;
	}
	
	public void visit(String name, Object value) {
		info.add(prefix, name, value);
	}

	public AnnotationVisitor visitAnnotation(String name, String desc) {
		return create(name);
	}

	public AnnotationVisitor visitArray(String name) {
		
		return create(name);
	}

	private AnnotationInfoVisitor create(String name){
		String newPrefix = prefix == null ? name: prefix+"."+name;
		return new AnnotationInfoVisitor(newPrefix, info, av);
	}
	
	public void visitEnd() {
		av.visitEnd();
	}

	public void visitEnum(String name, String desc, String value) {
		
		info.addEnum(prefix, name, desc, value);
		av.visitEnum(name, desc, value);
	}

	
}
