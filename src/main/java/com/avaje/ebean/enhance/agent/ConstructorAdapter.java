package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;

/**
 * Modify the constructor to additionally initialise the entityBeanIntercept
 * field.
 * 
 * <pre class="code">
 * // added into constructor
 * _ebean_intercept = new EntityBeanIntercept(this);
 * </pre>
 */
public class ConstructorAdapter extends MethodVisitor implements EnhanceConstants, Opcodes {

	private final ClassMeta meta;

	private final String className;

	private final String constructorDesc;

	private boolean constructorInitializationDone;

  /**
   * Holds an init instruction to see if it is an init of a persistent
   * many property. Init of a ArrayList, HashSet, LinkedHashSet.
   */
  private ConsumedInit consumedInit;

	public ConstructorAdapter(MethodVisitor mv, ClassMeta meta, String constructorDesc) {
		super(Opcodes.ASM5, mv);
		this.meta = meta;
		this.className = meta.getClassName();
		this.constructorDesc = constructorDesc;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

    if (isCollectionInit(opcode, owner, name, desc)) {
      // consume this init to see if the next call is a PUTFIELD to a persistent many property
      // and if it is we consume both instructions (getters against collections are intercepted)
      consumedInit = new ConsumedInit(opcode, owner, name, desc, itf);
    } else {
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      addInitialisationIfRequired(opcode, owner, name, desc);
    }
	}

  /**
   * Return true if this is an init of a ArrayList, HashSet, LinkedHashSet.
   * <p>
   *   If so we hold this to see if the next instruction is a PUTFIELD on
   *   a persistent many and if so we consume both instructions.
   * </p>
   */
  private boolean isCollectionInit(int opcode, String owner, String name, String desc) {
    if (opcode == INVOKESPECIAL && name.equals("<init>") && desc.equals("()V")) {
      if ("java/util/ArrayList".equals(owner)
          || "java/util/HashSet".equals(owner)
          || "java/util/LinkedHashSet".equals(owner)) {
        return true;
      }
      if (meta.isLog(4)) {
        meta.log("... not consuming many owner:" + owner + " name:" + name + " desc:" + desc);
      }
    }
    return false;
  }

  public void visitFieldInsn(int opcode, String owner, String name, String desc) {

    if (opcode != Opcodes.PUTFIELD) {
      if (meta.isLog(3)) {
        meta.log("... visitFieldInsn - " + opcode + " owner:" + owner + ":" + name + ":" + desc);
      }
      super.visitFieldInsn(opcode, owner, name, desc);
      consumedInit = null;

    } else {
      if (consumedInit != null) {
        // check for a PUTFIELD on a persistent many
        if (meta.isFieldPersistentMany(name)) {
          // consuming both the init and the putfield as the getter on
          // the many will initialise the many when needed
          if (meta.isLog(2)) {
            meta.log("... consumed collection init and PUTFIELD on persistent many:" + name + " from constructor, type:"+consumedInit.owner);
          }
          consumedInit = null;
          return;

        } else {
          if (meta.isLog(2)) {
            meta.log("... restore collection init on field "+name+" (non persistent field) " + consumedInit);
          }
          super.visitMethodInsn(consumedInit.opcode, consumedInit.owner, consumedInit.name, consumedInit.desc, consumedInit.itf);
          consumedInit = null;
        }
      }

      // intercept any PUTFIELD that happen in the constructor
      String methodName = "_ebean_set_" + name;
      String methodDesc = "(" + desc + ")V";
      if (meta.isLog(2)) {
        meta.log("... Constructor PUTFIELD replaced with:" + methodName + methodDesc);
      }
      super.visitMethodInsn(INVOKEVIRTUAL, className, methodName, methodDesc, false);
    }
  }
	
	/**
	 * Add initialisation of EntityBeanIntercept to constructor.
	 * 
	 * <pre>
	 * _ebean_intercept = new EntityBeanIntercept(this);
	 * </pre>
	 */
	public void addInitialisationIfRequired(int opcode, String owner, String name, String desc) {

		if (opcode == INVOKESPECIAL && name.equals("<init>") && desc.equals("()V")) {
			if (meta.isSuperClassEntity()) {
				if (meta.isLog(3)) {
          meta.log("... skipping intercept <init> ... handled by super class... CONSTRUCTOR: owner:" + owner + " " + constructorDesc);
        }
			} else if (owner.equals(meta.getClassName())) {
				if (meta.isLog(3)) {
          meta.log("... skipping intercept <init> ... handled by other constructor... CONSTRUCTOR: owner:" + owner + " " + constructorDesc);
        }
			} else if (owner.equals(meta.getSuperClassName())){
				if (meta.isLog(2)) {
					meta.log("... adding intercept <init> in CONSTRUCTOR:" + constructorDesc + " OWNER/SUPER:" + owner);
				}

				if (constructorInitializationDone) {
					// hopefully this is never called but put it in here to be
					// on the safe side.
					String msg = "Error in Enhancement. Only expecting to add <init> of intercept object"
							+ " once but it is trying to add it twice for " + meta.getClassName() + " CONSTRUCTOR:"
							+ constructorDesc+ " OWNER:" + owner;
          System.err.println(msg);

				} else {
          // add the initialisation of the intercept object
					super.visitVarInsn(ALOAD, 0);
          super.visitTypeInsn(NEW, C_INTERCEPT);
          super.visitInsn(DUP);
          super.visitVarInsn(ALOAD, 0);

          super.visitMethodInsn(INVOKESPECIAL, C_INTERCEPT, "<init>", "(Ljava/lang/Object;)V", false);
          super.visitFieldInsn(PUTFIELD, className, INTERCEPT_FIELD, EnhanceConstants.L_INTERCEPT);

          if (meta.isLog(8)) {
            meta.log("... constructorInitializationDone " + owner);
          }
          constructorInitializationDone = true;
				}
			} else {
				if (meta.isLog(3)) {
					meta.log("... skipping intercept <init> ... incorrect type "+owner);
				}				
			}
		}
	}

  /**
   * Used to hold an init of an ArrayList or HashSet in case it
   * is just set to a 'many' property in which case we can just
   * consume it (as gets against the collection types are intercepted
   * and collections always initialised to handle change detection).
   */
  static class ConsumedInit {

    int opcode;
    String owner;
    String name;
    String desc;
    boolean itf;

    public ConsumedInit(int opcode, String owner, String name, String desc, boolean itf) {
      this.opcode = opcode;
      this.owner = owner;
      this.name = name;
      this.desc = desc;
      this.itf = itf;
    }

    public String toString() {
      return owner + " name:"+name+" desc:"+desc;
    }
  }

}
