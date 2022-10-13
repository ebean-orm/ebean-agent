package io.ebean.enhance.entity;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.common.ClassMeta;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

final class MethodStaticInitAdapter extends MethodVisitor {

  private final ClassMeta classMeta;

  MethodStaticInitAdapter(final MethodVisitor mv, ClassMeta classMeta) {
    super(EBEAN_ASM_VERSION, mv);
    this.classMeta = classMeta;
  }

  @Override
  public void visitCode() {
    super.visitCode();
    IndexFieldWeaver.addPropertiesInit(mv, classMeta);
  }
}
