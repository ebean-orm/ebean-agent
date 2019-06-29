package io.ebean.enhance.common;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Opcodes;

/**
 * Reads the annotation information storing it in a AnnotationInfo.
 */
public class AnnotationInfoVisitor extends AnnotationVisitor {

  private final AnnotationInfo info;

  private final String prefix;

  public AnnotationInfoVisitor(String prefix, AnnotationInfo info, AnnotationVisitor av) {
    super(Opcodes.ASM7, av);
    this.info = info;
    this.prefix = prefix;
    if (prefix != null) {
      info.getArrayEntry(prefix); // register as empty
    }
  }

  @Override
  public void visit(String name, Object value) {
    super.visit(name, value);
    info.add(prefix, name, value);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String name, String desc) {
    return create(name, super.visitAnnotation(name, desc));
  }

  @Override
  public AnnotationVisitor visitArray(String name) {
    return create(name, super.visitArray(name));
  }

  private AnnotationInfoVisitor create(String name, AnnotationVisitor underlying){
    String newPrefix = prefix == null ? name: prefix+"."+name;
    return new AnnotationInfoVisitor(newPrefix, info, underlying);
  }

  @Override
  public void visitEnum(String name, String desc, String value) {
    super.visitEnum(name, desc, value);
    info.addEnum(prefix, name, value);
  }

}
