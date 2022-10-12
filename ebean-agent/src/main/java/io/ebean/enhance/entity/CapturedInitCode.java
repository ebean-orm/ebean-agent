package io.ebean.enhance.entity;

import io.ebean.enhance.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Code that initialises a transient field.
 * <p>
 * Added into a default constructor when that is added to an entity bean.
 */
public final class CapturedInitCode {

  private final String name;
  private final String type;
  private final List<DeferredCode> code;

  CapturedInitCode(List<DeferredCode> codes, int opcode, String owner, String name, String desc, String type) {
    this.name = name;
    this.type = type;
    this.code = new ArrayList<>(codes);
    this.code.add(new PutField(opcode, owner, name, desc));
  }

  /**
   * Return the field name.
   */
  public String name() {
    return name;
  }

  /**
   * Return the concrete type the field is initialised to.
   */
  public String type() {
    return type;
  }

  /**
   * Write the code that initialises the field.
   */
  public void write(ConstructorAdapter mv) {
    for (DeferredCode deferredCode : code) {
      deferredCode.write(mv);
    }
  }

  public String mismatch(String type2) {
    return "type1:" + type + " type2:" + type2;
  }

  private static class PutField implements DeferredCode {
    final int opcode;
    final String owner, name, desc;

    public PutField(int opcode, String owner, String name, String desc) {
      this.opcode = opcode;
      this.owner = owner;
      this.name = name;
      this.desc = desc;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitFieldInsn(opcode, owner, name, desc);
    }
  }

}
