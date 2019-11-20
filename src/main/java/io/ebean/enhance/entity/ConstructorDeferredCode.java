package io.ebean.enhance.entity;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;

import java.util.ArrayList;
import java.util.List;

import static io.ebean.enhance.common.EnhanceConstants.INIT;
import static io.ebean.enhance.common.EnhanceConstants.NOARG_VOID;

/**
 * This is a class that 'defers' bytecode instructions in the default constructor initialisation
 * such that code that initialises persistent many properties (Lists, Sets and Maps) is removed.
 * <p>
 * The purpose is to consume unwanted initialisation of Lists, Sets and Maps for OneToMany
 * and ManyToMany properties.
 * </p>
 * <pre>
 *
 *  mv.visitVarInsn(ALOAD, 0);
 *  mv.visitTypeInsn(NEW, "java/util/ArrayList");
 *  mv.visitInsn(DUP);
 *  mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
 *  mv.visitTypeInsn(CHECKCAST, "java/util/List"); // OPTIONAL
 *  mv.visitFieldInsn(PUTFIELD, "test/model/WithInitialisedCollections", "contacts", "Ljava/util/List;");
 *
 * </pre>
 */
class ConstructorDeferredCode implements Opcodes {

  private static final ALoad ALOAD_INSTRUCTION = new ALoad();

  private static final Dup DUP_INSTRUCTION = new Dup();

  private final ClassMeta meta;
  private final MethodVisitor mv;

  private final List<DeferredCode> codes = new ArrayList<>();

  ConstructorDeferredCode(ClassMeta meta, MethodVisitor mv) {
    this.meta = meta;
    this.mv = mv;
  }

  /**
   * Return true if this is an ALOAD 0 which we defer.
   */
  boolean deferVisitVarInsn(int opcode, int var) {
    flush();
    if (opcode == ALOAD && var == 0) {
      codes.add(ALOAD_INSTRUCTION);
      return true;
    }
    return false;
  }

  /**
   * Return true if we defer this based on it being a NEW or CHECKCAST on persistent many
   * and was proceeded by a deferred ALOAD (for NEW) or Collection init (for CHECKCAST).
   */
  boolean deferVisitTypeInsn(int opcode, String type) {
    if (opcode == NEW && isCollection(type) && stateAload()) {
      codes.add(new NewCollection(type));
      return true;
    }
    if (opcode == CHECKCAST && stateCollectionInit()) {
      codes.add(new CheckCastCollection(type));
      return true;
    }
    flush();
    return false;
  }

  /**
   * Return true if we defer this based on it being a DUP and was proceeded
   * by a deferred ALOAD and NEW.
   */
  boolean deferVisitInsn(int opcode) {
    if (opcode == DUP && stateNewCollection()) {
      codes.add(DUP_INSTRUCTION);
      return true;
    }
    flush();
    return false;
  }

  /**
   * Return true if we defer this based on it being an init of a collection
   * and was proceeded by a deferred DUP.
   */
  boolean deferVisitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

    if (opcode == INVOKESPECIAL && stateDup() && isCollectionInit(owner, name, desc)) {
      codes.add(new CollectionInit(opcode, owner, name, desc, itf));
      return true;
    }
    flush();
    return false;
  }

  /**
   * Return true if this is an init of a ArrayList, HashSet, LinkedHashSet.
   */
  private boolean isCollectionInit(String owner, String name, String desc) {
    return name.equals(INIT) && desc.equals(NOARG_VOID) && isCollection(owner);
  }

  /**
   * Return true if we have consumed all the deferred code that initialises a persistent collection.
   */
  boolean consumeVisitFieldInsn(int opcode, String name) {
    if (opcode == PUTFIELD && stateConsumeDeferred() && meta.isConsumeInitMany(name)) {
      if (meta.isLog(3)) {
        meta.log("... consumed init of many: " + name);
      }
      codes.clear();
      return true;
    }
    flush();
    return false;
  }


  /**
   * Flush all deferred instructions.
   */
  void flush() {
    if (!codes.isEmpty()) {
      for (DeferredCode code : codes) {
        if (meta.isLog(4)) {
          meta.log("... flush deferred: " + code);
        }
        code.write(mv);
      }
      codes.clear();
    }
  }

  private boolean stateAload() {
    // ALOAD always first deferred instruction
    return (codes.size() == 1);
  }

  private boolean stateNewCollection() {
    // New Collection always second deferred instruction
    return (codes.size() == 2);
  }

  private boolean stateDup() {
    // DUP always third deferred instruction
    return (codes.size() == 3);
  }

  private boolean stateCollectionInit() {
    // Checkcast proceeded by CollectionInit
    return (codes.size() == 4);
  }

  private boolean stateConsumeDeferred() {
    // Proceeded by CollectionInit with optional Checkcast
    int size = codes.size();
    return (size == 4 || size == 5);
  }

  /**
   * Return true if this is a collection type used to initialise persistent collections.
   */
  private boolean isCollection(String type) {
    return ("java/util/ArrayList".equals(type)
      || "java/util/LinkedHashSet".equals(type)
      || "java/util/HashSet".equals(type));
  }

  /**
   * ALOAD 0
   */
  static class ALoad implements DeferredCode {
    @Override
    public void write(MethodVisitor mv) {
      mv.visitVarInsn(ALOAD, 0);
    }

    @Override
    public String toString() {
      return "ALOAD 0";
    }
  }

  /**
   * DUP
   */
  static class Dup implements DeferredCode {
    @Override
    public void write(MethodVisitor mv) {
      mv.visitInsn(DUP);
    }

    @Override
    public String toString() {
      return "DUP";
    }
  }

  /**
   * Typically NEW java/util/ArrayList
   */
  static class NewCollection implements DeferredCode {

    final String type;

    NewCollection(String type) {
      this.type = type;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitTypeInsn(NEW, type);
    }

    @Override
    public String toString() {
      return "NEW " + type;
    }
  }

  /**
   * Typically CHECKCAST java/util/List
   */
  static class CheckCastCollection implements DeferredCode {

    final String type;

    CheckCastCollection(String type) {
      this.type = type;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitTypeInsn(CHECKCAST, type);
    }

    @Override
    public String toString() {
      return "CHECKCAST " + type;
    }
  }

  /**
   * Typically INVOKESPECIAL java/util/ArrayList.<init> ()V
   */
  static class CollectionInit implements DeferredCode {

    final int opcode;
    final String owner;
    final String name;
    final String desc;
    final boolean itf;

    CollectionInit(int opcode, String owner, String name, String desc, boolean itf) {
      this.opcode = opcode;
      this.owner = owner;
      this.name = name;
      this.desc = desc;
      this.itf = itf;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public String toString() {
      return "INVOKESPECIAL " + owner + ".<init> ()V";
    }
  }
}
