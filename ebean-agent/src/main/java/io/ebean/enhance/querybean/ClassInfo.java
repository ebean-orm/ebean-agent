package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds meta information for a class.
 */
class ClassInfo implements Constants {

  private final EnhanceContext enhanceContext;
  private final String className;
  private boolean addedMarkerAnnotation;
  private boolean typeQueryBean;
  private boolean typeQueryUser;
  private boolean fieldAccessUser;
  private boolean alreadyEnhanced;
  private List<FieldInfo> fields;
  private boolean hasBasicConstructor;
  private boolean hasMainConstructor;
  private boolean entityBean;

  public ClassInfo(EnhanceContext enhanceContext, String className) {
    this.enhanceContext = enhanceContext;
    this.className = className;
  }

  public void setEntityBean() {
    entityBean = true;
  }

  public boolean isEntityBean() {
    return entityBean;
  }

  /**
   * Return the className.
   */
  public String getClassName() {
    return className;
  }

  /**
   * Return the short name of the class.
   */
  String getShortName() {
    int pos = className.lastIndexOf("/");
    return className.substring(pos + 1);
  }

  /**
   * Return true if the bean is already enhanced.
   */
  boolean isAlreadyEnhanced() {
    return alreadyEnhanced;
  }

  boolean addMarkerAnnotation() {
    if (addedMarkerAnnotation) {
      return false;
    }
    addedMarkerAnnotation = true;
    return true;
  }

  /**
   * Return true if the bean is a type query bean.
   */
  boolean isTypeQueryBean() {
    return typeQueryBean;
  }

  /**
   * Return true if the class is explicitly annotated with TypeQueryUser annotation.
   */
  boolean isTypeQueryUser() {
    return typeQueryUser;
  }

  public boolean isFieldAccessUser() {
    return fieldAccessUser;
  }

  /**
   * Mark this class as having enhancement for query beans.
   */
  void markTypeQueryEnhanced() {
    typeQueryUser = true;
  }

  /**
   * Check for the type query bean and type query user annotations.
   */
  boolean checkTypeQueryAnnotation(String desc) {
    if (isTypeQueryBeanAnnotation(desc)) {
      typeQueryBean = true;
    } else if (isAlreadyEnhancedAnnotation(desc)) {
      alreadyEnhanced = true;
    }
    return typeQueryBean;
  }

  /**
   * Add the type query bean field. We will create a 'property access' method for each field.
   */
  void addField(int access, String name, String desc, String signature) {
    if (((access & Opcodes.ACC_PUBLIC) != 0)) {
      if (fields == null) {
        fields = new ArrayList<>();
      }
      if ((access & Opcodes.ACC_STATIC) == 0) {
        fields.add(new FieldInfo(this, name, desc, signature));
      }
    }
  }

  /**
   * Return true if the annotation is the TypeQueryBean annotation.
   */
  private boolean isAlreadyEnhancedAnnotation(String desc) {
    return ANNOTATION_ALREADY_ENHANCED_MARKER.equals(desc);
  }

  /**
   * Return true if the annotation is the TypeQueryBean annotation.
   */
  private boolean isTypeQueryBeanAnnotation(String desc) {
    return ANNOTATION_TYPE_QUERY_BEAN.equals(desc);
  }

  /**
   * Return the fields for a type query bean.
   */
  public List<FieldInfo> getFields() {
    return fields;
  }

  /**
   * Note that a GETFIELD call has been replaced to method call.
   */
  void addGetFieldIntercept(String owner, String name) {
    if (isLog(4)) {
      log("change getfield " + owner + " name:" + name);
    }
    typeQueryUser = true;
  }

  void markEntityFieldAccess(String type, String owner, String name) {
    if (isLog(2)) {
      log("replace " + type + " field access " + owner + "." + name);
    }
    fieldAccessUser = true;
  }

  public boolean isLog(int level) {
    return enhanceContext.isLog(level);
  }

  public void log(String msg) {
    enhanceContext.log(className, msg);
  }

  /**
   * There is a basic constructor on the assoc bean which is being overwritten (so don't need to add later).
   */
  void setHasBasicConstructor() {
    hasBasicConstructor = true;
  }

  /**
   * There is a main constructor on the assoc bean which is being overwritten (so don't need to add later).
   */
  void setHasMainConstructor() {
    hasMainConstructor = true;
  }

  /**
   * Add fields and constructors to assoc type query beans as necessary.
   */
  void addAssocBeanExtras(ClassVisitor cv, String superName) {
    if (isLog(4)) {
      String msg = "... add fields";
      if (!hasBasicConstructor) {
        msg += ", basic constructor";
      }
      if (!hasMainConstructor) {
        msg += ", main constructor";
      }
      log(msg);
    }

    if (!hasBasicConstructor) {
      // add the assoc bean basic constructor
      new TypeQueryAssocBasicConstructor(superName, this, cv, ASSOC_BEAN_BASIC_CONSTRUCTOR_DESC, ASSOC_BEAN_BASIC_SIG).visitCode();
    }
    if (!hasMainConstructor) {
      // add the assoc bean main constructor
      new TypeQueryAssocMainConstructor(superName, this, cv, ASSOC_BEAN_MAIN_CONSTRUCTOR_DESC, ASSOC_BEAN_MAIN_SIG).visitCode();
    }

  }

}
