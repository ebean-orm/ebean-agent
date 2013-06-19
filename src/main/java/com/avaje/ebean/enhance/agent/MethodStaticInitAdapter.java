package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.MethodAdapter;
import com.avaje.ebean.enhance.asm.MethodVisitor;

public class MethodStaticInitAdapter extends MethodAdapter {

  protected final ClassMeta classMeta;
  
  public MethodStaticInitAdapter(final MethodVisitor mv, ClassMeta classMeta) {
      super(mv);
      this.classMeta = classMeta;
  }
  
  @Override
  public void visitCode() {
    super.visitCode();
    IndexFieldWeaver.addPropertiesInit(mv, classMeta);
  }
}
