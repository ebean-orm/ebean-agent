package io.ebean.enhance.entity;

import io.ebean.enhance.asm.Label;
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
 *  mv.visitInsn(ICONST_0);         // (A) Optionally generated by Kotlin
 *  mv.visitVarInsn(ISTORE, 1);     // (A)
 *  mv.visitTypeInsn(NEW, "java/util/ArrayList");
 *  mv.visitInsn(DUP);
 *  mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
 *  mv.visitTypeInsn(CHECKCAST, "java/util/List"); // (B) Optionally generated by Kotlin
 *  Label label = new Label();      // (B)
 *  mv.visitLabel(label);           // (B)
 *  mv.visitLineNumber(__, label4); // (B)
 *  mv.visitFieldInsn(PUTFIELD, "test/model/WithInitialisedCollections", "contacts", "Ljava/util/List;");
 *
 * </pre>
 */
final class ConstructorDeferredCode implements Opcodes {

  enum State {
    UNSET,
    ALOAD,
    KT_ICONST,       // optional kotlin state
    NEW_COLLECTION,
    DUP,
    INVOKE_SPECIAL,
    KT_CHECKCAST,   // optional kotlin state
    KT_LABEL,        // optional kotlin state
    EMPTY,
    MAYBE_UNSUPPORTED
  }

  private static final ALoad ALOAD_INSTRUCTION = new ALoad();
  private static final Dup DUP_INSTRUCTION = new Dup();
  private static final Iconst0 ICONST0_INSTRUCTION = new Iconst0();

  private final ClassMeta meta;
  private final MethodVisitor mv;
  private final List<DeferredCode> codes = new ArrayList<>();
  private State state = State.UNSET;
  private String stateInitialiseType;

  ConstructorDeferredCode(ClassMeta meta, MethodVisitor mv) {
    this.meta = meta;
    this.mv = mv;
  }

  /**
   * Return true if this is an ALOAD 0 which we defer.
   */
  boolean deferVisitVarInsn(int opcode, int var) {
    if (state == State.KT_ICONST && opcode == ISTORE) {
      codes.add(new Istore(var));
      state = State.ALOAD;
      return true;
    }
    flush();
    if (opcode == ALOAD && var == 0) {
      codes.add(ALOAD_INSTRUCTION);
      state = State.ALOAD;
      return true;
    }
    if (opcode == ALOAD) {
      // maybe constructor initialisation of OneToMany
      state = State.ALOAD;
    }
    return false;
  }

  /**
   * Return true if we defer this based on it being a NEW or CHECKCAST on persistent many
   * and was proceeded by a deferred ALOAD (for NEW) or Collection init (for CHECKCAST).
   */
  boolean deferVisitTypeInsn(int opcode, String type) {
    if (opcode == NEW && stateAload()) { // && isCollection(type)) {
      codes.add(new NewCollection(type));
      state = State.NEW_COLLECTION;
      return true;
    }
    if (opcode == CHECKCAST && stateInvokeSpecial()) {
      codes.add(new CheckCastCollection(type));
      state = State.KT_CHECKCAST;
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
    if (opcode == ICONST_0 && stateAload()) {
      codes.add(ICONST0_INSTRUCTION);
      state = State.KT_ICONST;
      return true;
    }
    if (opcode == DUP && stateNewCollection()) {
      codes.add(DUP_INSTRUCTION);
      state = State.DUP;
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
    if (opcode == INVOKESPECIAL && stateDup() && isNoArgInit(name, desc)) {
      codes.add(new NoArgInit(opcode, owner, name, desc, itf));
      state = State.INVOKE_SPECIAL;
      stateInitialiseType = owner;
      return true;
    }
    if (opcode == INVOKESTATIC && stateAload()) {
      if (kotlinEmptyList(owner, name, desc) || emptyList(owner, name, desc)) {
        codes.add(new NoArgInit(opcode, owner, name, desc, itf));
        state = State.EMPTY;
        stateInitialiseType = "java/util/ArrayList";
        return true;
      }
      if (emptySet(owner, name, desc)) {
        codes.add(new NoArgInit(opcode, owner, name, desc, itf));
        state = State.EMPTY;
        stateInitialiseType = "java/util/LinkedHashSet";
        return true;
      }
      if (emptyMap(owner, name, desc)) {
        codes.add(new NoArgInit(opcode, owner, name, desc, itf));
        state = State.EMPTY;
        stateInitialiseType = "java/util/LinkedHashMap";
        return true;
      }
    }
    flush();
    state = State.MAYBE_UNSUPPORTED;
    return false;
  }

  private boolean isNoArgInit(String name, String desc) {
    return name.equals(INIT) && desc.equals(NOARG_VOID);
  }

  private boolean emptyList(String owner, String name, String desc) {
    return desc.equals("()Ljava/util/List;")
      && ((owner.equals("java/util/List") && name.equals("of"))
      || (owner.equals("java/util/Collections") && name.equals("emptyList")));
  }

  private boolean emptySet(String owner, String name, String desc) {
    return desc.equals("()Ljava/util/Set;")
      && ((owner.equals("java/util/Set") && name.equals("of"))
      || (owner.equals("java/util/Collections") && name.equals("emptySet")));
  }

  private boolean emptyMap(String owner, String name, String desc) {
    return desc.equals("()Ljava/util/Map;")
      && ((owner.equals("java/util/Map") && name.equals("of"))
      || (owner.equals("java/util/Collections") && name.equals("emptyMap")));
  }

  private boolean kotlinEmptyList(String owner, String name, String desc) {
    return owner.equals("kotlin/collections/CollectionsKt")
      && name.equals("emptyList")
      && desc.equals("()Ljava/util/List;");
  }

  /**
   * Return true if we have consumed all the deferred code that initialises a persistent collection.
   */
  boolean consumeVisitFieldInsn(int opcode, String owner, String name, String desc) {
    if (opcode == PUTFIELD) {
      if (meta.isConsumeInitMany(name) && unsupportedInitialisation()) {
        // a OneToMany/ManyToMany is initialised in an unsupported manor
        meta.addUnsupportedInitMany(name);
        flush();
        return false;
      }
      if (stateConsumeDeferred()) {
        if (meta.isConsumeInitMany(name) && isConsumeManyType()) {
          if (meta.isLog(3)) {
            meta.log("... consumed init of many: " + name);
          }
          state = State.UNSET;
          codes.clear();
          return true;
        } else if (meta.isInitTransient(name)) {
          // keep the initialisation code for transient to 'replay'
          // it when adding a default constructor if needed
          if (meta.isLog(3)) {
            meta.log("... init transient: " + name + " type: " + stateInitialiseType);
          }
          meta.addTransientInit(new CapturedInitCode(codes, opcode, owner, name, desc, stateInitialiseType));
        } else if (meta.isTransient(name)) {
          meta.addUnsupportedTransientInit(name);
        }
      } else if (meta.isTransient(name)) {
        meta.addUnsupportedTransientInit(name);
      }
    }
    flush();
    return false;
  }

  /**
   * Return true when a OneToMany or ManyToMany is not initialised in a supported manor.
   */
  private boolean unsupportedInitialisation() {
    if (state == State.ALOAD) {
      // allow constructor initialisation of a OneToMany
      return false;
    }
    return state == State.MAYBE_UNSUPPORTED
      || state == State.UNSET // proceeded by GETSTATIC field bytecode like Collections.EMPTY_LIST
      || state == State.INVOKE_SPECIAL && !isConsumeManyType(); // e.g. new BeanList()
  }

  boolean consumeVisitLabel(Label label) {
    if (state == State.KT_CHECKCAST) {
      codes.add(new DeferredLabel(label));
      state = State.KT_LABEL;
      return true;
    }
    return false;
  }

  public boolean consumeVisitLineNumber(int line, Label start) {
    if (state == State.KT_LABEL) {
      codes.add(new DeferredLineNumber(line, start));
      state = State.INVOKE_SPECIAL;
      return true;
    }
    return false;
  }

  /**
   * Flush all deferred instructions.
   */
  void flush() {
    state = State.UNSET;
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
    return state == State.ALOAD;
  }

  private boolean stateNewCollection() {
    return state == State.NEW_COLLECTION;
  }

  private boolean stateDup() {
    return state == State.DUP;
  }

  private boolean stateInvokeSpecial() {
    return state == State.INVOKE_SPECIAL;
  }

  private boolean stateConsumeDeferred() {
    return state == State.INVOKE_SPECIAL || state == State.KT_CHECKCAST || state == State.EMPTY;
  }

  /**
   * Return true if the type being initialised is valid for auto initialisation of ToMany or DbArray.
   */
  private boolean isConsumeManyType() {
    return "java/util/ArrayList".equals(stateInitialiseType)
      || "java/util/LinkedList".equals(stateInitialiseType)
      || "java/util/LinkedHashSet".equals(stateInitialiseType)
      || "java/util/HashSet".equals(stateInitialiseType)
      || "java/util/LinkedHashMap".equals(stateInitialiseType)
      || "java/util/HashMap".equals(stateInitialiseType);
  }

  private static class ALoad implements DeferredCode {
    @Override
    public void write(MethodVisitor mv) {
      mv.visitVarInsn(ALOAD, 0);
    }
  }

  private static class DeferredLabel implements DeferredCode {
    private final Label label;
    DeferredLabel(Label label) {
      this.label = label;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitLabel(label);
    }
  }

  private static class DeferredLineNumber implements DeferredCode {
    private final int line;
    private final Label label;
    DeferredLineNumber(int line, Label label) {
      this.line = line;
      this.label = label;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitLineNumber(line, label);
    }
  }

  private static class Iconst0 implements DeferredCode {
    @Override
    public void write(MethodVisitor mv) {
      mv.visitInsn(ICONST_0);
    }
  }

  private static class Istore implements DeferredCode {
    private final int value;
    Istore(int value) {
      this.value = value;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitVarInsn(ISTORE, value);
    }
  }

  /**
   * DUP
   */
  private static class Dup implements DeferredCode {
    @Override
    public void write(MethodVisitor mv) {
      mv.visitInsn(DUP);
    }
  }

  /**
   * Typically NEW java/util/ArrayList
   */
  private static class NewCollection implements DeferredCode {
    final String type;
    NewCollection(String type) {
      this.type = type;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitTypeInsn(NEW, type);
    }
  }

  /**
   * Typically CHECKCAST java/util/List
   */
  private static class CheckCastCollection implements DeferredCode {
    final String type;
    CheckCastCollection(String type) {
      this.type = type;
    }

    @Override
    public void write(MethodVisitor mv) {
      mv.visitTypeInsn(CHECKCAST, type);
    }
  }

  /**
   * Typically INVOKESPECIAL java/util/ArrayList.<init> ()V
   */
  private static class NoArgInit implements DeferredCode {

    final int opcode;
    final String owner;
    final String name;
    final String desc;
    final boolean itf;

    NoArgInit(int opcode, String owner, String name, String desc, boolean itf) {
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
  }
}
