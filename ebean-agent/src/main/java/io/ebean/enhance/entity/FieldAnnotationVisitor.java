package io.ebean.enhance.entity;

import io.ebean.enhance.asm.AnnotationVisitor;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

final class FieldAnnotationVisitor extends AnnotationVisitor {

  private final FieldMeta fieldMeta;

  FieldAnnotationVisitor(FieldMeta fieldMeta, AnnotationVisitor visitor) {
    super(EBEAN_ASM_VERSION, visitor);
    this.fieldMeta = fieldMeta;
  }

  @Override
  public void visit(String name, Object value) {
    super.visit(name, value);
    if ("nullable".equals(name) && "false".equals(String.valueOf(value))) {
      fieldMeta.setNotNull();
    }
  }
}
