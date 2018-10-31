package io.ebean.enhance.entity;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;

public class MethodStaticInitAdapter extends MethodVisitor {

  protected final ClassMeta classMeta;

  public MethodStaticInitAdapter(final MethodVisitor mv, ClassMeta classMeta) {
    super(Opcodes.ASM7, mv);
    this.classMeta = classMeta;
  }

  @Override
  public void visitCode() {
    super.visitCode();
    IndexFieldWeaver.addPropertiesInit(mv, classMeta);
  }
}
