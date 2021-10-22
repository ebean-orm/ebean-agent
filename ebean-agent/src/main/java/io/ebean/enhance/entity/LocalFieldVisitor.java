package io.ebean.enhance.entity;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Attribute;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.common.AnnotationInfoVisitor;
import io.ebean.enhance.common.EnhanceConstants;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

/**
 * Used to collect information about a field (specifically from field annotations).
 */
public class LocalFieldVisitor extends FieldVisitor implements EnhanceConstants {

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
  public String getName() {
    return fieldMeta.getFieldName();
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    fieldMeta.addAnnotationDesc(desc);
    if (fv != null) {
      if (!visible && desc.equals(L_JETBRAINS_NOTNULL)) {
        fv.visitAnnotation(L_EBEAN_NOTNULL, true);
      }
      AnnotationVisitor av = fv.visitAnnotation(desc, visible);
      if (desc.equals(NORMALIZE_ANNOTATION)) {
        av = new AnnotationInfoVisitor(null, fieldMeta.getNormalizeAnnotationInfo(), av);
      }
      return av;
    } else {
      return null;
    }
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
