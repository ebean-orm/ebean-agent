package io.ebean.enhance.entity;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.common.EnhanceContext;
import io.ebean.enhance.common.NoEnhancementRequiredException;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

/**
 * ClassAdapter for enhancing entities.
 * <p>
 * Used for javaagent or ant etc to modify the class with field interception.
 * </p>
 * <p>
 * This is NOT used for subclass generation.
 * </p>
 */
public class ClassAdapterEntity extends ClassVisitor implements EnhanceConstants {

  private final EnhanceContext enhanceContext;

  private final ClassLoader classLoader;

  private final ClassMeta classMeta;

  private boolean firstMethod = true;

  public ClassAdapterEntity(ClassVisitor cv, ClassLoader classLoader, EnhanceContext context) {
    super(EBEAN_ASM_VERSION, cv);
    this.classLoader = classLoader;
    this.enhanceContext = context;
    this.classMeta = context.createClassMeta();
  }

  /**
   * Log that the class has been enhanced.
   */
  public void logEnhanced() {
    classMeta.logEnhanced();
  }

  public boolean isLog(int level) {
    return classMeta.isLog(level);
  }

  public void log(String msg) {
    classMeta.log(msg);
  }

  /**
   * Create the class definition replacing the className and super class.
   */
  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    skipMockitoMock(name);
    classMeta.setClassName(name, superName);

    String[] c = new String[interfaces.length + 1];
    for (int i = 0; i < interfaces.length; i++) {
      c[i] = interfaces[i];
      if (c[i].equals(C_ENTITYBEAN)) {
        throw new NoEnhancementRequiredException();
      }
      if (c[i].equals(C_SCALAOBJECT)) {
        classMeta.setScalaInterface(true);
      }
      if (c[i].equals(C_GROOVYOBJECT)) {
        classMeta.setGroovyInterface(true);
      }
    }
    // add the EntityBean interface
    c[c.length - 1] = C_ENTITYBEAN;
    if (classMeta.isLog(8)) {
      classMeta.log("... add EntityBean interface");
    }
    if (!superName.equals("java/lang/Object")) {
      // read information about superClasses...
      if (classMeta.isLog(7)) {
        classMeta.log("read information about superClasses " + superName + " to see if it is entity/embedded/mappedSuperclass");
      }
      ClassMeta superMeta = enhanceContext.getSuperMeta(superName, classLoader);
      if (superMeta != null && superMeta.isEntity()) {
        // the superClass is an entity/embedded/mappedSuperclass...
        classMeta.setSuperMeta(superMeta);
      }
    }
    super.visit(version, access, name, signature, superName, c);
  }

  /**
   * Do not enhance a Mockito mock or spy.
   */
  private void skipMockitoMock(String name) {
    if (name.contains(MOCKITO_MOCK)) {
      throw new NoEnhancementRequiredException();
    }
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    classMeta.addClassAnnotation(desc);
    return super.visitAnnotation(desc, visible);
  }

  private boolean isPropertyChangeListenerField(String desc) {
    return desc.equals("Ljava/beans/PropertyChangeSupport;");
  }

  /**
   * The ebeanIntercept field is added once but thats all. Note the other
   * fields are defined in the superclass.
   */
  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    if ((access & Opcodes.ACC_STATIC) != 0) {
      // no interception of static fields
      return super.visitField(access, name, desc, signature, value);
    }
    if (isPropertyChangeListenerField(desc)) {
      if (isLog(4)) {
        classMeta.log("Found existing PropertyChangeSupport field " + name);
      }
      // no interception on PropertyChangeSupport field
      return super.visitField(access, name, desc, signature, value);
    }
    if ((access & Opcodes.ACC_TRANSIENT) != 0) {
      // no interception of transient fields
      return super.visitField(access, name, desc, signature, value);
    }
    if ((access & Opcodes.ACC_FINAL) != 0) {
      // remove final modifier from fields (for lazy loading partials in Java9+)
      access = (access ^ Opcodes.ACC_FINAL);
    }
    FieldVisitor fv = super.visitField(access, name, desc, signature, value);
    return classMeta.createLocalFieldVisitor(fv, name, desc);
  }

  /**
   * Replace the method code with field interception.
   */
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    if (firstMethod) {
      if (classMeta.isAlreadyEnhanced()) {
        throw new NoEnhancementRequiredException();
      }
      if (classMeta.hasEntityBeanInterface()) {
        log("Enhancing when EntityBean interface already exists!");
      }
      IndexFieldWeaver.addPropertiesField(cv, classMeta);
      if (isLog(4)) {
        log("... add _ebean_props field");
      }
      if (!classMeta.isSuperClassEntity()) {
        // only add the intercept and identity fields if
        // the superClass is not also enhanced
        if (isLog(4)) {
          log("... add intercept and identity fields");
        }
        InterceptField.addField(cv, classMeta, enhanceContext.isTransientInternalFields());
        MethodEquals.addIdentityField(cv, classMeta);

      }
      firstMethod = false;
    }

    if (isLog(4)) {
      log("--- #### method name[" + name + "] desc[" + desc + "] sig[" + signature + "]");
    }

    if (isConstructor(name, desc)) {
      if (desc.equals(NOARG_VOID)) {
        // ensure public access on the default constructor
        access = Opcodes.ACC_PUBLIC;
      }
      MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
      return new ConstructorAdapter(mv, classMeta, desc);
    }

    if (isStaticInit(name, desc)) {
      if (isLog(4)) {
        log("... --- #### enhance existing static init method");
      }
      MethodVisitor mv = super.visitMethod(Opcodes.ACC_STATIC, name, desc, signature, exceptions);
      return new MethodStaticInitAdapter(mv, classMeta);
    }

    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    if (interceptEntityMethod(access, name, desc)) {
      // change the method replacing the relevant GETFIELD PUTFIELD with
      // our special field methods with interception...
      return new MethodFieldAdapter(mv, classMeta, name + " " + desc);
    }
    // just leave as is, no interception etc
    return mv;
  }

  /**
   * Add methods to get and set the entityBeanIntercept. Also add the
   * writeReplace method to control serialisation.
   */
  @Override
  public void visitEnd() {
    if (!classMeta.isEntityEnhancementRequired()) {
      throw new NoEnhancementRequiredException();
    }
    if (!classMeta.hasStaticInit()) {
      IndexFieldWeaver.addPropertiesInit(cv, classMeta);
    }
    if (!classMeta.hasDefaultConstructor()) {
      DefaultConstructor.add(cv, classMeta);
    }
    if (isLog(4)) {
      log("... add _ebean_getPropertyNames() and _ebean_getPropertyName()");
    }
    IndexFieldWeaver.addGetPropertyNames(cv, classMeta);
    IndexFieldWeaver.addGetPropertyName(cv, classMeta);

    if (!classMeta.isSuperClassEntity()) {
      if (isLog(8)) {
        log("... add _ebean_getIntercept() and _ebean_setIntercept()");
      }
      InterceptField.addGetterSetter(cv, classMeta);
    }

    // Add the field set/get methods which are used in place
    // of GETFIELD PUTFIELD instructions
    classMeta.addFieldGetSetMethods(cv);

    //Add the getField(index) and setField(index) methods
    IndexFieldWeaver.addMethods(cv, classMeta);

    MethodSetEmbeddedLoaded.addMethod(cv, classMeta);
    MethodIsEmbeddedNewOrDirty.addMethod(cv, classMeta);
    MethodNewInstance.addMethod(cv, classMeta);
    MethodNewInstanceReadOnly.interceptAddReadOnly(cv, classMeta);

    // register with the agentContext
    enhanceContext.addClassMeta(classMeta);
    enhanceContext.summaryEntity(classMeta.getClassName());
    super.visitEnd();
  }

  private boolean isConstructor(String name, String desc) {
    if (name.equals(INIT)) {
      if (desc.equals(NOARG_VOID)) {
        classMeta.setHasDefaultConstructor(true);
      }
      return true;
    }
    return false;
  }

  private boolean isStaticInit(String name, String desc) {
    if (name.equals(CLINIT) && desc.equals(NOARG_VOID)) {
      classMeta.setHasStaticInit(true);
      return true;
    }
    return false;
  }

  private boolean interceptEntityMethod(int access, String name, String desc) {
    if ((access & Opcodes.ACC_STATIC) != 0) {
      // no interception of static methods?
      if (isLog(4)) {
        log("Skip intercepting static method " + name);
      }
      return false;
    }
    if (name.equals("hashCode") && desc.equals("()I")) {
      classMeta.setHasEqualsOrHashcode(true);
      return true;
    }
    if (name.equals("equals") && desc.equals("(Ljava/lang/Object;)Z")) {
      classMeta.setHasEqualsOrHashcode(true);
      return true;
    }
    // don't intercept toString as its is used during debugging etc
    return !name.equals("toString") || !desc.equals("()Ljava/lang/String;");
  }
}
