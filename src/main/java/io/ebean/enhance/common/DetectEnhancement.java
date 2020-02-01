package io.ebean.enhance.common;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;

import static io.ebean.enhance.common.EnhanceConstants.TRANSACTIONAL_ANNOTATION;

/**
 * ClassAdapter used to detect if this class needs enhancement for entity or
 * transactional support.
 */
public class DetectEnhancement extends ClassVisitor {

  private final ClassLoader classLoader;

  private final EnhanceContext enhanceContext;

  private final DetectTransactionalMethod detectTransactionalMethod = new DetectTransactionalMethod();

  private String className;

  private boolean entity;

  private boolean enhancedEntity;

  private boolean transactional;

  private boolean enhancedTransactional;

  public DetectEnhancement(ClassLoader classLoader, EnhanceContext context) {
    super(Opcodes.ASM7);
    this.classLoader = classLoader;
    this.enhanceContext = context;
  }

  private boolean isLog(int level) {
    return enhanceContext.isLog(level);
  }

  private void log(String msg) {
    enhanceContext.log(className, msg);
  }

  public void log(int level, String msg) {
    if (isLog(level)) {
      log(msg);
    }
  }

  public boolean isEnhancedEntity() {
    return enhancedEntity;
  }

  public boolean isEnhancedTransactional() {
    return enhancedTransactional;
  }

  /**
  * Return true if this is an entity bean or embeddable bean.
  */
  public boolean isEntity() {
    return entity;
  }

  /**
  * Return true if ANY method has the transactional annotation.
  */
  public boolean isTransactional() {
    return transactional;
  }

  /**
  * Visit the class with interfaces.
  */
  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

    if ((access & Opcodes.ACC_INTERFACE) != 0) {
      throw new NoEnhancementRequiredException("Interface type");
    }
    if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
      throw new NoEnhancementRequiredException("Synthetic type");
    }

    this.className = name;
    for (String anInterface : interfaces) {
      if (anInterface.equals(EnhanceConstants.C_ENTITYBEAN)) {
        enhancedEntity = true;
        entity = true;

      } else if (anInterface.equals(EnhanceConstants.C_ENHANCEDTRANSACTIONAL)) {
        enhancedTransactional = true;

      } else {
        ClassMeta interfaceMeta = enhanceContext.getInterfaceMeta(anInterface, classLoader);
        if (interfaceMeta != null && interfaceMeta.isTransactional()) {
          transactional = true;
          if (isLog(9)) {
            log("detected implements transactional interface " + interfaceMeta);
          }
        }
      }
    }

    if (isLog(4)) {
      log("interfaces:  enhancedEntity[" + enhancedEntity + "] transactional[" + enhancedTransactional + "]");
    }
  }

  /**
  * Visit class level annotations.
  */
  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    if (isEntityAnnotation(desc)) {
      if (isLog(5)) {
        log("found entity annotation " + desc);
      }
      entity = true;

    } else if (desc.equals(TRANSACTIONAL_ANNOTATION)) {
      if (isLog(5)) {
        log("found transactional annotation " + desc);
      }
      transactional = true;
    }
    return null;
  }

  /**
  * Return true if the annotation is for an Entity, Embeddable or MappedSuperclass.
  */
  private boolean isEntityAnnotation(String desc) {
    return EntityCheck.isEntityAnnotation(desc);
  }

  /**
  * Visit the methods specifically looking for method level transactional
  * annotations.
  */
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    return detectTransactionalMethod;
  }

  /**
  * Check methods for Transactional annotation.
  */
  private class DetectTransactionalMethod extends MethodVisitor {

    DetectTransactionalMethod() {
      super(Opcodes.ASM7);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      if (desc.equals(TRANSACTIONAL_ANNOTATION)) {
        transactional = true;
      }
      return null;
    }

  }
}
