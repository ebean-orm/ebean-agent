package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

public class MethodStaticInitAdapter extends MethodVisitor {

  protected final ClassMeta classMeta;

  public MethodStaticInitAdapter(final MethodVisitor mv, ClassMeta classMeta) {
    super(Opcodes.ASM5, mv);
    this.classMeta = classMeta;
  }

  @Override
  public void visitCode() {
    super.visitCode();
    IndexFieldWeaver.addPropertiesInit(mv, classMeta);
  }
}
