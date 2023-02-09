package io.ebean.enhance.entity;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Attribute;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.common.EnhanceConstants;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

/**
 * Used to collect information about a field (specifically from field annotations).
 */
public final class LocalFieldVisitor extends FieldVisitor implements EnhanceConstants {

  private final FieldMeta fieldMeta;

  /**
   * Constructor used for entity class enhancement.
   *
   * @param fv        the fieldVisitor used to write
   * @param fieldMeta the fieldMeta data
   */
  public LocalFieldVisitor(FieldVisitor fv, FieldMeta fieldMeta) {
    super(EBEAN_ASM_VERSION, fv);
    this.fieldMeta = fieldMeta;
  }

  /**
   * Return the field name.
   */
  public String name() {
    return fieldMeta.name();
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    fieldMeta.addAnnotationDesc(desc);
    if (fv != null) {
      if (!visible && desc.equals(L_JETBRAINS_NOTNULL)) {
        fv.visitAnnotation(L_EBEAN_NOTNULL, true);
        fieldMeta.setNotNull();
      }
      if (fieldMeta.isNullable() && annotationWithNullable(desc)) {
        // looking for nullable=false attribute on Column or DbArray
        return new FieldAnnotationVisitor(fieldMeta, fv.visitAnnotation(desc, visible));
      }
      return fv.visitAnnotation(desc, visible);
    } else {
      return null;
    }
  }

  private boolean annotationWithNullable(String desc) {
    return Javax.Column.equals(desc) || Jakarta.Column.equals(desc) || L_DBARRAY.equals(desc);
  }

  @Override
  public void visitAttribute(Attribute attr) {
    if (fv != null) {
      fv.visitAttribute(attr);
    }
  }

  @Override
  public void visitEnd() {
    if (fv != null) {
      fv.visitEnd();
    }
  }

}
