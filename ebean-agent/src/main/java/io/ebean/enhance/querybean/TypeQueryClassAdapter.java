package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.ClassWriter;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.*;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;
import static io.ebean.enhance.common.EnhanceConstants.C_ENTITYBEAN;
import static io.ebean.enhance.common.EnhanceConstants.INIT;

/**
 * Reads/visits the class and performs the appropriate enhancement if necessary.
 */
public final class TypeQueryClassAdapter extends ClassVisitor implements Constants {

  private final EnhanceContext enhanceContext;
  private final ClassLoader loader;
  private boolean typeQueryRootBean;
  private String className;
  private String superName;
  private String signature;
  private ClassInfo classInfo;
  private final AnnotationInfo annotationInfo = new AnnotationInfo(null);

  public TypeQueryClassAdapter(ClassWriter cw, EnhanceContext enhanceContext, ClassLoader loader) {
    super(EBEAN_ASM_VERSION, cw);
    this.enhanceContext = enhanceContext;
    this.loader = loader;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    super.visit(version, access, name, signature, superName, interfaces);
    this.typeQueryRootBean = TypeQueryUtil.isQueryBean(superName);
    this.superName = superName;
    this.className = name;
    this.signature = signature;
    this.classInfo = new ClassInfo(enhanceContext, name);
    for (String interfaceType : interfaces) {
      if (interfaceType.equals(C_ENTITYBEAN)) {
        classInfo.setEntityBean();
      }
    }
  }

  /**
   * Extract and return the associated entity bean class from the signature.
   */
  private String getDomainClass() {
    int posStart = signature.indexOf('<');
    int posEnd = signature.indexOf(';', posStart + 1);
    return signature.substring(posStart + 2, posEnd);
  }

  /**
   * Look for TypeQueryBean annotation.
   */
  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    boolean queryBean = classInfo.checkTypeQueryAnnotation(desc);
    AnnotationVisitor av = super.visitAnnotation(desc, visible);
    return (queryBean) ? new AnnotationInfoVisitor(null, annotationInfo, av) : av;
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    if (classInfo.isAlreadyEnhanced()) {
      throw new AlreadyEnhancedException(className);
    }
    if (classInfo.isTypeQueryBean()) {
      // collect type query bean fields
      classInfo.addField(access, name, desc, signature);
    }
    return super.visitField(access, name, desc, signature, value);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    if (classInfo.isTypeQueryBean()) {
      if ((access & Opcodes.ACC_STATIC) != 0) {
        if (isLog(5)) {
          log("ignore static methods on type query bean " + name + " " + desc);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
      }
      if (classInfo.addMarkerAnnotation()) {
        addMarkerAnnotation();
      }
      if (name.equals(INIT) && desc.startsWith("(Lio/ebean/Query;")) {
        // skip query bean constructor that takes Query (as used only for building FetchGroup)
        return super.visitMethod(access, name, desc, signature, exceptions);
      }
      if (name.equals(INIT)) {
        if (desc.equals("(Z)V")) {
          // Constructor for alias initialises all the properties/fields
          return new TypeQueryConstructorForAlias(classInfo, cv, superName);
        }
        if (hasVersion()) {
          // no enhancement on constructors required
          return super.visitMethod(access, name, desc, signature, exceptions);
        }
        if (!typeQueryRootBean) {
          if (enhanceContext.improvedQueryBeans()) {
            return super.visitMethod(access, name, desc, signature, exceptions);
          } else {
             return handleAssocBeanConstructor(access, name, desc, signature, exceptions);
          }
        }
        return new TypeQueryConstructorAdapter(classInfo, superName, getDomainClass(), cv, desc, signature);
      }
      if (!desc.startsWith("()L")) {
        if (isLog(5)) {
          log("leaving method as is - " + name + " " + desc + " " + signature);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
      }
      MethodDesc methodDesc = new MethodDesc(access, name, desc, signature, exceptions);
      if (methodDesc.isGetter()) {
        if (isLog(4)) {
          log("overwrite getter method - " + name + " " + desc + " " + signature);
        }
        return new TypeQueryGetterAdapter(cv, classInfo, methodDesc);
      }
    }

    if (isLog(8)) {
      log("... checking method " + name + " " + desc);
    }
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    return new MethodAdapter(mv, enhanceContext, classInfo, loader);
  }

  /**
   * Return true if the TypeQueryBean has a value attribute with version description.
   * True means we are using 11.39+ query bean generation.
   */
  private boolean hasVersion() {
    return annotationInfo.getValue("value") != null;
  }

  /**
   * Handle the constructors for assoc type query beans.
   */
  private MethodVisitor handleAssocBeanConstructor(int access, String name, String desc, String signature, String[] exceptions) {
    if (desc.equals(ASSOC_BEAN_BASIC_CONSTRUCTOR_DESC)) {
      classInfo.setHasBasicConstructor();
      return new TypeQueryAssocBasicConstructor(superName, classInfo, cv, desc, signature);
    }
    if (desc.equals(ASSOC_BEAN_MAIN_CONSTRUCTOR_DESC)) {
      classInfo.setHasMainConstructor();
      return new TypeQueryAssocMainConstructor(superName, classInfo, cv, desc, signature);
    }
    // leave as is
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  @Override
  public void visitEnd() {
    if (classInfo.isAlreadyEnhanced()) {
      throw new AlreadyEnhancedException(className);
    }
    if (classInfo.isTypeQueryBean()) {
      if (!typeQueryRootBean) {
        if (!enhanceContext.improvedQueryBeans()) {
          classInfo.addAssocBeanExtras(cv, superName);
        }
      } else {
        enhanceContext.summaryQueryBean(className);
      }
      TypeQueryAddMethods.add(cv, classInfo, typeQueryRootBean);
      if (isLog(2)) {
        classInfo.log("enhanced as query bean");
      }
    } else if (classInfo.isFieldAccessUser()) {
      enhanceContext.summaryFieldAccessUser(className);
    } else if (classInfo.isTypeQueryUser()) {
      if (isLog(4)) {
        classInfo.log("enhanced - getfield calls replaced");
      }
    } else {
      throw new NoEnhancementRequiredException("No query bean enhancement");
    }
    super.visitEnd();
  }

  /**
   * Add the marker annotation so that we don't enhance the type query bean twice.
   */
  private void addMarkerAnnotation() {
    if (isLog(4)) {
      log("... adding marker annotation");
    }
    AnnotationVisitor av = cv.visitAnnotation(ANNOTATION_ALREADY_ENHANCED_MARKER, true);
    if (av != null) {
      av.visitEnd();
    }
  }

  public boolean isLog(int level) {
    return enhanceContext.isLog(level);
  }

  public void log(String msg) {
    enhanceContext.log(className, msg);
  }
}
