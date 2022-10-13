package io.ebean.enhance.common;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.entity.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;
import static io.ebean.enhance.common.EnhanceConstants.*;

/**
 * Holds the meta data for an entity bean class that is being enhanced.
 */
public class ClassMeta {

  private static final Logger logger = Logger.getLogger(ClassMeta.class.getName());

  private final MessageOutput logout;
  private final int logLevel;
  private String className;
  private String superClassName;
  private ClassMeta superMeta;
  /**
   * Set to true if the class implements th GroovyObject interface.
   */
  private boolean hasGroovyInterface;
  /**
   * Set to true if the class implements the ScalaObject interface.
   */
  private boolean hasScalaInterface;
  /**
   * Set to true if the class already implements the EntityBean interface.
   */
  private boolean hasEntityBeanInterface;
  private boolean alreadyEnhanced;
  private boolean hasEqualsOrHashcode;
  private boolean hasToString;
  private boolean hasDefaultConstructor;
  private boolean hasStaticInit;

  /**
   * If enhancement is adding a default constructor - only single type is supported initialising transient fields.
   */
  private final Set<String> unsupportedTransientMultipleTypes = new LinkedHashSet<>();
  /**
   * If enhancement is adding a default constructor - only default constructors are supported initialising transient fields.
   */
  private final Set<String> unsupportedTransientInitialisation = new LinkedHashSet<>();
  private final Map<String, CapturedInitCode> transientInitCode = new LinkedHashMap<>();
  private final LinkedHashMap<String, FieldMeta> fields = new LinkedHashMap<>();
  private final HashSet<String> classAnnotation = new HashSet<>();
  private final AnnotationInfo annotationInfo = new AnnotationInfo(null);
  private final ArrayList<MethodMeta> methodMetaList = new ArrayList<>();
  private final EnhanceContext enhanceContext;
  private List<FieldMeta> allFields;
  private boolean recordType;

  public ClassMeta(EnhanceContext enhanceContext, int logLevel, MessageOutput logout) {
    this.enhanceContext = enhanceContext;
    this.logLevel = logLevel;
    this.logout = logout;
  }

  /**
   * Return the enhance context which has options for enhancement.
   */
  public EnhanceContext context() {
    return enhanceContext;
  }

  /**
   * Return the AnnotationInfo collected on methods.
   * Used to determine Transactional method enhancement.
   */
  public AnnotationInfo annotationInfo() {
    return annotationInfo;
  }

  public boolean isAllowNullableDbArray() {
    return enhanceContext.isAllowNullableDbArray();
  }

  /**
   * Return the transactional annotation information for a matching interface method.
   */
  public AnnotationInfo interfaceTransactionalInfo(String methodName, String methodDesc) {
    AnnotationInfo annotationInfo = null;
    for (int i = 0; i < methodMetaList.size(); i++) {
      MethodMeta meta = methodMetaList.get(i);
      if (meta.isMatch(methodName, methodDesc)) {
        if (annotationInfo != null) {
          String msg = "Error in [" + className + "] searching the transactional methods[" + methodMetaList
            + "] found more than one match for the transactional method:" + methodName + " "
            + methodDesc;

          logger.log(Level.SEVERE, msg);
          log(msg);
        } else {
          annotationInfo = meta.getAnnotationInfo();
          if (isLog(9)) {
            log("... found transactional info from interface " + className + " " + methodName + " " + methodDesc);
          }
        }
      }
    }
    return annotationInfo;
  }

  public boolean isCheckSuperClassForEntity() {
    return !superClassName.equals(C_OBJECT) && isCheckEntity();
  }

  @Override
  public String toString() {
    return className;
  }

  public boolean isTransactional() {
    return classAnnotation.contains(TRANSACTIONAL_ANNOTATION);
  }

  public void setClassName(String className, String superClassName) {
    this.className = className;
    this.superClassName = superClassName;
    if (superClassName.equals(C_RECORDTYPE)) {
      recordType = true;
    }
  }

  public String superClassName() {
    return superClassName;
  }

  public boolean isLog(int level) {
    return level <= logLevel;
  }

  public void log(String msg) {
    if (className != null) {
      msg = "cls: " + className + "  msg: " + msg;
    }
    logout.println("ebean-enhance> " + msg);
  }

  public void logEnhanced() {
    String m = "enhanced ";
    if (hasScalaInterface()) {
      m += " (scala)";
    }
    if (hasGroovyInterface()) {
      m += " (groovy)";
    }
    log(m);
  }

  public void setSuperMeta(ClassMeta superMeta) {
    this.superMeta = superMeta;
  }

  /**
   * Set to true if the class has an existing equals() or hashcode() method.
   */
  public void setHasEqualsOrHashcode(boolean hasEqualsOrHashcode) {
    this.hasEqualsOrHashcode = hasEqualsOrHashcode;
  }

  public void setHasToString() {
    this.hasToString = true;
  }

  /**
   * Return true if Equals/hashCode is implemented on this class or a super class.
   */
  public boolean hasEqualsOrHashCode() {
    if (hasEqualsOrHashcode) {
      return true;
    } else {
      return (superMeta != null && superMeta.hasEqualsOrHashCode());
    }
  }

  public boolean hasToString() {
    if (hasToString) {
      return true;
    } else {
      return (superMeta != null && superMeta.hasToString());
    }
  }

  /**
   * Return true if the field is a persistent field.
   */
  public boolean isFieldPersistent(String fieldName) {
    FieldMeta f = field(fieldName);
    return (f != null) && f.isPersistent();
  }

  public boolean isTransient(String fieldName) {
    FieldMeta f = field(fieldName);
    return (f != null && f.isTransient());
  }

  public boolean isInitTransient(String fieldName) {
    if (!enhanceContext.isTransientInit()) {
      return false;
    }
    return isTransient(fieldName);
  }

  /**
   * Return true if the field is a persistent many field that we want to consume the init on.
   */
  public boolean isConsumeInitMany(String fieldName) {
    FieldMeta f = field(fieldName);
    return (f != null && f.isPersistent() && f.isInitMany());
  }

  /**
   * Return the field - null when not found.
   */
  public FieldMeta field(String fieldName) {
    FieldMeta f = fields.get(fieldName);
    if (f != null) {
      return f;
    }
    return (superMeta == null) ? null : superMeta.field(fieldName);
  }

  /**
   * Return the list of fields local to this type (not inherited).
   */
  private List<FieldMeta> localFields() {
    List<FieldMeta> list = new ArrayList<>();
    for (FieldMeta fm : fields.values()) {
      if (!fm.isObjectArray()) {
        // add field local to this entity type
        list.add(fm);
      }
    }
    return list;
  }

  /**
   * Return the list of fields inherited from super types that are entities.
   */
  private void addInheritedFields(List<FieldMeta> list) {
    if (superMeta != null) {
      superMeta.addFieldsForInheritance(list);
    }
  }

  /**
   * Add all fields to the list.
   */
  private void addFieldsForInheritance(List<FieldMeta> list) {
    if (isEntity()) {
      list.addAll(0, fields.values());
      if (superMeta != null) {
        superMeta.addFieldsForInheritance(list);
      }
    }
  }

  /**
   * Return true if the class contains persistent fields.
   */
  public boolean hasPersistentFields() {
    for (FieldMeta fieldMeta : fields.values()) {
      if (fieldMeta.isPersistent() || fieldMeta.isTransient()) {
        return true;
      }
    }
    return superMeta != null && superMeta.hasPersistentFields();
  }

  /**
   * Return the list of all fields including ones inherited from entity super
   * types and mappedSuperclasses.
   */
  public List<FieldMeta> allFields() {
    if (allFields != null) {
      return allFields;
    }
    List<FieldMeta> list = localFields();
    addInheritedFields(list);

    this.allFields = list;
    for (int i = 0; i < allFields.size(); i++) {
      allFields.get(i).setIndexPosition(i);
    }
    return list;
  }

  /**
   * Add field level get set methods for each field.
   */
  public void addFieldGetSetMethods(ClassVisitor cv) {
    if (isEntityEnhancementRequired()) {
      for (FieldMeta fm : fields.values()) {
        fm.addGetSetMethods(cv, this);
      }
    }
  }

  /**
   * Return true if this is a mapped superclass.
   */
  boolean isMappedSuper() {
    return classAnnotation.contains(EnhanceConstants.MAPPEDSUPERCLASS_ANNOTATION);
  }

  /**
   * Return true if this is a query bean.
   */
  public boolean isQueryBean() {
    return classAnnotation.contains(TYPEQUERYBEAN_ANNOTATION);
  }

  /**
   * Return true if the class has an Entity, Embeddable or MappedSuperclass.
   */
  public boolean isEntity() {
    return EntityCheck.hasEntityAnnotation(classAnnotation);
  }

  /**
   * Return true if the class has an Entity, Embeddable, or MappedSuperclass.
   */
  private boolean isCheckEntity() {
    return EntityCheck.hasEntityAnnotation(classAnnotation);
  }

  /**
   * Return true for classes not already enhanced and yet annotated with entity, embeddable or mappedSuperclass.
   */
  public boolean isEntityEnhancementRequired() {
    return !alreadyEnhanced && isEntity();
  }

  /**
   * Return true if the bean is already enhanced.
   */
  public boolean isAlreadyEnhanced() {
    return alreadyEnhanced;
  }

  /**
   * Return the className of this entity class.
   */
  public String className() {
    return className;
  }

  /**
   * Return true if this entity bean has a super class that is an entity.
   */
  public boolean isSuperClassEntity() {
    return superMeta != null && superMeta.isEntity();
  }

  /**
   * Add a class annotation.
   */
  public void addClassAnnotation(String desc) {
    classAnnotation.add(desc);
  }

  MethodVisitor createMethodVisitor(MethodVisitor mv, String name, String desc) {
    MethodMeta methodMeta = new MethodMeta(annotationInfo, name, desc);
    methodMetaList.add(methodMeta);
    return new MethodReader(mv, methodMeta);
  }

  /**
   * ACC_PUBLIC with maybe ACC_SYNTHETIC.
   */
  public int accPublic() {
    return enhanceContext.accPublic();
  }

  /**
   * ACC_PROTECTED with maybe ACC_SYNTHETIC.
   */
  public int accProtected() {
    return enhanceContext.accProtected();
  }

  /**
   * ACC_PRIVATE with maybe ACC_SYNTHETIC.
   */
  public int accPrivate() {
    return enhanceContext.accPrivate();
  }

  public boolean isToManyGetField() {
    return enhanceContext.isToManyGetField();
  }

  /**
   * Return the EntityBeanIntercept type that will be new'ed up for the EntityBean.
   * For version 140+ EntityBeanIntercept is an interface and instead we new up InterceptReadWrite.
   */
  public String interceptNew() {
    return enhanceContext.interceptNew();
  }

  /**
   * Invoke a method on EntityBeanIntercept.
   * For version 140+ EntityBeanIntercept is an interface and this uses INVOKEINTERFACE.
   */
  public void visitMethodInsnIntercept(MethodVisitor mv, String name, String desc) {
    enhanceContext.visitMethodInsnIntercept(mv, name, desc);
  }

  /**
   * If 141+ Add InterceptReadOnly support.
   */
  public boolean interceptAddReadOnly() {
    return enhanceContext.interceptAddReadOnly();
  }

  public boolean isRecordType() {
    return recordType;
  }

  public void addTransientInit(CapturedInitCode deferredInitCode) {
    CapturedInitCode old = transientInitCode.put(deferredInitCode.name(), deferredInitCode);
    if (old != null && !old.type().equals(deferredInitCode.type())) {
      transientInitCode.put(deferredInitCode.name(), old);
      unsupportedTransientMultipleTypes.add("field: " + old.name() + " types: " + old.type() + " " + deferredInitCode.type());
    }
  }

  public Collection<CapturedInitCode> transientInit() {
    return transientInitCode.values();
  }

  public void addUnsupportedTransientInit(String name) {
    unsupportedTransientInitialisation.add(name);
  }

  public boolean hasTransientFieldErrors() {
    return !unsupportedTransientMultipleTypes.isEmpty() || !unsupportedTransientInitialisation.isEmpty();
  }

  public String transientFieldErrorMessage() {
    String msg = "ERROR: Entity class without default constructor has unsupported initialisation of transient fields. Entity class: " + className;
    if (!unsupportedTransientMultipleTypes.isEmpty()) {
      msg += " - fields initialised in constructor with 2 different types - " + unsupportedTransientMultipleTypes;
    }
    if (!unsupportedTransientInitialisation.isEmpty()) {
      msg += " - Unsupported initialisation of transient fields - " + unsupportedTransientInitialisation;
    }
    msg += " Refer: https://ebean.io/docs/trouble-shooting#transient-initialisation";
    return msg;
  }

  private static final class MethodReader extends MethodVisitor {

    final MethodMeta methodMeta;

    MethodReader(MethodVisitor mv, MethodMeta methodMeta) {
      super(EBEAN_ASM_VERSION, mv);
      this.methodMeta = methodMeta;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      AnnotationVisitor av = null;
      if (mv != null) {
        av = mv.visitAnnotation(desc, visible);
      }
      if (!isInterestingAnnotation(desc)) {
        return av;
      }
      return new AnnotationInfoVisitor(null, methodMeta.getAnnotationInfo(), av);
    }

    private boolean isInterestingAnnotation(String desc) {
      return TRANSACTIONAL_ANNOTATION.equals(desc)
        || TYPEQUERYBEAN_ANNOTATION.equals(desc);
    }
  }

  /**
   * Create and return a read only fieldVisitor for subclassing option.
   */
  FieldVisitor createLocalFieldVisitor(String name, String desc) {
    return createLocalFieldVisitor(null, name, desc);
  }

  /**
   * Create and return a new fieldVisitor for use when enhancing a class.
   */
  public FieldVisitor createLocalFieldVisitor(FieldVisitor fv, String name, String desc) {
    FieldMeta fieldMeta = new FieldMeta(this, name, desc, className);
    LocalFieldVisitor localField = new LocalFieldVisitor(fv, fieldMeta);
    if (name.startsWith("_ebean")) {
      // can occur when reading inheritance information on
      // a entity that has already been enhanced
      if (isLog(5)) {
        log("... ignore field " + name);
      }
    } else {
      fields.put(localField.name(), fieldMeta);
    }
    return localField;
  }

  public void setAlreadyEnhanced(boolean alreadyEnhanced) {
    this.alreadyEnhanced = alreadyEnhanced;
  }

  public boolean hasDefaultConstructor() {
    return hasDefaultConstructor;
  }

  public void setHasDefaultConstructor(boolean hasDefaultConstructor) {
    this.hasDefaultConstructor = hasDefaultConstructor;
  }

  public void setHasStaticInit(boolean hasStaticInit) {
    this.hasStaticInit = hasStaticInit;
  }

  public boolean hasStaticInit() {
    return hasStaticInit;
  }

  public String description() {
    StringBuilder sb = new StringBuilder();
    appendDescription(sb);
    return sb.toString();
  }

  private void appendDescription(StringBuilder sb) {
    sb.append(className);
    if (superMeta != null) {
      sb.append(" : ");
      superMeta.appendDescription(sb);
    }
  }

  private boolean hasScalaInterface() {
    return hasScalaInterface;
  }

  public void setScalaInterface(boolean hasScalaInterface) {
    this.hasScalaInterface = hasScalaInterface;
  }

  public boolean hasEntityBeanInterface() {
    return hasEntityBeanInterface;
  }

  public void setEntityBeanInterface(boolean hasEntityBeanInterface) {
    this.hasEntityBeanInterface = hasEntityBeanInterface;
  }

  private boolean hasGroovyInterface() {
    return hasGroovyInterface;
  }

  public void setGroovyInterface(boolean hasGroovyInterface) {
    this.hasGroovyInterface = hasGroovyInterface;
  }

}
