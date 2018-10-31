package io.ebean.enhance.entity;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.Attribute;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Used to collect information about a field (specifically from field annotations).
 */
public class LocalFieldVisitor extends FieldVisitor implements EnhanceConstants {

	private final FieldMeta fieldMeta;

	/**
	 * Constructor used for entity class enhancement.
   *
	 * @param fv the fieldVisitor used to write
	 * @param fieldMeta the fieldMeta data
	 */
	public LocalFieldVisitor(FieldVisitor fv, FieldMeta fieldMeta) {
    super(Opcodes.ASM7, fv);
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
		if (fv != null){
			if (!visible && desc.equals(L_JETBRAINS_NOTNULL)) {
				fv.visitAnnotation(L_EBEAN_NOTNULL, true);
			}
			return fv.visitAnnotation(desc, visible);
		} else {
			return null;
		}
	}

	@Override
	public void visitAttribute(Attribute attr) {
		if (fv != null){
			fv.visitAttribute(attr);
		}
	}

	@Override
	public void visitEnd() {
		if (fv != null){
			fv.visitEnd();
		}
	}

}
