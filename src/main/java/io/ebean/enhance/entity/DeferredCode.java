package io.ebean.enhance.entity;

import io.ebean.enhance.asm.MethodVisitor;

/**
 * Bytecode instructions that are held/deferred so that they can be removed
 * entirely if desired (initialisation of OneToMany and ManyToMany properties).
 */
public interface DeferredCode {

  /**
  * Write the bytecode to the method visitor.
  * <p>
  * Called when it is deemed the instructions should not be removed.
  * </p>
  */
  void write(MethodVisitor mv);

}
