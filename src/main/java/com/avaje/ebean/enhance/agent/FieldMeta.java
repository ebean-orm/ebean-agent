package com.avaje.ebean.enhance.agent;

import java.util.HashSet;

import com.avaje.ebean.enhance.asm.ClassVisitor;
import com.avaje.ebean.enhance.asm.Label;
import com.avaje.ebean.enhance.asm.MethodVisitor;
import com.avaje.ebean.enhance.asm.Opcodes;
import com.avaje.ebean.enhance.asm.Type;

/**
 * Holds meta data for a field.
 * <p>
 * This can then generate the appropriate byte code for this field.
 * </p>
 */
public class FieldMeta implements Opcodes, EnhanceConstants {

  private static final Type BOOLEAN_OBJECT_TYPE = Type.getType(Boolean.class);

  private final ClassMeta classMeta;
  private final String fieldClass;
  private final String fieldName;
  private final String fieldDesc;

  private final HashSet<String> annotations = new HashSet<String>();

  private final Type asmType;

  private final boolean primativeType;
  private final boolean objectType;

  private final String getMethodName;
  private final String getMethodDesc;
  private final String setMethodName;
  private final String setMethodDesc;
  private final String getNoInterceptMethodName;
  private final String setNoInterceptMethodName;

  private final String publicSetterName;
  private final String publicGetterName;

  int indexPosition;

  /**
   * Construct based on field name and desc from reading byte code.
   * <p>
   * Used for reading local fields (not inherited) via visiting the class bytes.
   * </p>
   */
  public FieldMeta(ClassMeta classMeta, String name, String desc, String fieldClass) {
    this.classMeta = classMeta;
    this.fieldName = name;
    this.fieldDesc = desc;
    this.fieldClass = fieldClass;

    asmType = Type.getType(desc);

    int sort = asmType.getSort();
    primativeType = sort > Type.VOID && sort <= Type.DOUBLE;
    objectType = sort == Type.OBJECT;

    getMethodName = "_ebean_get_" + name;
    getMethodDesc = "()" + desc;

    setMethodName = "_ebean_set_" + name;
    setMethodDesc = "(" + desc + ")V";

    getNoInterceptMethodName = "_ebean_getni_" + name;
    setNoInterceptMethodName = "_ebean_setni_" + name;

    if (classMeta != null && classMeta.hasScalaInterface()) {
      // use scala property name
      publicSetterName = name + "_$eq";
      publicGetterName = name;

    } else {
      String publicFieldName = getFieldName(name, asmType);
      // use java bean property name convention
      String initCap = Character.toUpperCase(publicFieldName.charAt(0)) + publicFieldName.substring(1);
      publicSetterName = "set" + initCap;

      if (fieldDesc.equals("Z")) {
        publicGetterName = "is" + initCap;
      } else {
        publicGetterName = "get" + initCap;
      }
    }

    if (classMeta != null && classMeta.isLog(6)) {
      classMeta.log(" ... public getter [" + publicGetterName + "]");
      classMeta.log(" ... public setter [" + publicSetterName + "]");
    }
  }

  /**
   * Handle the case where a boolean variable starts with 'is'.
   */
  private String getFieldName(String name, Type asmType) {
    if ((BOOLEAN_OBJECT_TYPE.equals(asmType) || Type.BOOLEAN_TYPE.equals(asmType))
        && name.startsWith("is") && name.length() > 2) {

      // boolean starting with "is" ... maybe trim off the "is"
      char c = name.charAt(2);
      if (Character.isUpperCase(c)) {
        if (classMeta.isLog(6)) {
          classMeta.log("trimming off \"is\" from boolean field name " + name + "]");
        }
        return name.substring(2);
      }
    }
    return name;
  }

  public void setIndexPosition(int indexPosition) {
    this.indexPosition = indexPosition;
  }

  /**
   * The index position of the field in the bean properties array.
   */
  public int getIndexPosition() {
    return indexPosition;
  }

  public String toString() {
    return fieldName;
  }

  /**
   * Return the field name.
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Return true if this is a primativeType.
   */
  public boolean isPrimativeType() {
    return primativeType;
  }

  /**
   * The expected public getter name following bean naming convention.
   * <p>
   * This is generally used for subclassing rather than javaagent enhancement.
   * </p>
   */
  public String getPublicGetterName() {
    return publicGetterName;
  }

  /**
   * The expected public setter name following bean naming convention.
   * <p>
   * This is generally used for subclassing rather than javaagent enhancement.
   * </p>
   */
  public String getPublicSetterName() {
    return publicSetterName;
  }

  /**
   * Return true if this is the public setter for this field according to bean
   * naming convention.
   */
  public boolean isPersistentSetter(String methodDesc) {
    return setMethodDesc.equals(methodDesc) && isInterceptSet();
  }

  /**
   * Return true if this is the public getter for this field according to bean
   * naming convention.
   */
  public boolean isPersistentGetter(String methodDesc) {
    return getMethodDesc.equals(methodDesc) && isInterceptGet();
  }

  /**
   * Add a field annotation.
   */
  protected void addAnnotationDesc(String desc) {
    annotations.add(desc);
  }

  /**
   * Return the field name.
   */
  public String getName() {
    return fieldName;
  }

  /**
   * Return the field bytecode type description.
   */
  public String getDesc() {
    return fieldDesc;
  }

  private boolean isInterceptGet() {
    if (isId()) {
      return false;
    }
    if (isTransient()) {
      return false;
    }
    if (isMany()) {
      return true;
    }
    return true;
  }

  private boolean isInterceptSet() {
    if (isId()) {
      return false;
    }
    if (isTransient()) {
      return false;
    }
    if (isMany()) {
      return false;
    }
    return true;
  }

  /**
   * Return true if this field type is an Array of Objects.
   * <p>
   * We can not support Object Arrays for field types.
   * </p>
   */
  public boolean isObjectArray() {
    if (fieldDesc.charAt(0) == '[') {
      if (fieldDesc.length() > 2) {
        if (!isTransient()) {
          System.err.println("ERROR: We can not support Object Arrays... for field: " + fieldName);
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Return true is this is a persistent field.
   */
  public boolean isPersistent() {
    return !isTransient();
  }

  /**
   * Return true if this is a transient field.
   */
  public boolean isTransient() {
    return annotations.contains("Ljavax/persistence/Transient;");
  }

  /**
   * Return true if this is an ID field.
   * <p>
   * ID fields are used in generating equals() logic based on identity.
   * </p>
   */
  public boolean isId() {
    boolean idField = (annotations.contains("Ljavax/persistence/Id;") 
        || annotations.contains("Ljavax/persistence/EmbeddedId;"));

    return idField;
  }

  /**
   * Return true if this is a OneToMany or ManyToMany field.
   */
  public boolean isMany() {
    return annotations.contains("Ljavax/persistence/OneToMany;")
        || annotations.contains("Ljavax/persistence/ManyToMany;");
  }

  public boolean isManyToMany() {
    return annotations.contains("Ljavax/persistence/ManyToMany;");
  }

  /**
   * Return true if this is an Embedded field.
   */
  public boolean isEmbedded() {
    return annotations.contains("Ljavax/persistence/Embedded;")
        || annotations.contains(L_EMBEDDEDCOLUMNS);
  }

  /**
   * Return true if the field is local to this class. Returns false if the field
   * is actually on a super class.
   */
  public boolean isLocalField(ClassMeta classMeta) {
    return fieldClass.equals(classMeta.getClassName());
  }

  /**
   * Append byte code to return the Id value (for primitives).
   */
  public void appendGetPrimitiveIdValue(MethodVisitor mv, ClassMeta classMeta) {
    mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getMethodName, getMethodDesc);
  }

  /**
   * Append compare instructions if its a long, float or double.
   */
  public void appendCompare(MethodVisitor mv, ClassMeta classMeta) {
    if (primativeType) {
      if (classMeta.isLog(4)) {
        classMeta.log(" ... getIdentity compare primitive field[" + fieldName + "] type[" + fieldDesc + "]");
      }
      if (fieldDesc.equals("J")) {
        // long compare to 0
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);

      } else if (fieldDesc.equals("D")) {
        // double compare to 0
        mv.visitInsn(DCONST_0);
        mv.visitInsn(DCMPL);

      } else if (fieldDesc.equals("F")) {
        // float compare to 0
        mv.visitInsn(FCONST_0);
        mv.visitInsn(FCMPL);

      } else {
        // no extra instructions required for
        // int, short, byte, char
      }
    }
  }

  /**
   * Append code to get the Object value of a primitive.
   * <p>
   * This becomes a Integer.valueOf(someInt); or similar.
   * </p>
   */
  public void appendValueOf(MethodVisitor mv, ClassMeta classMeta) {
    if (primativeType) {
      // use valueOf methods to return primitives as objects
      Type objectWrapperType = PrimitiveHelper.getObjectWrapper(asmType);

      String objDesc = objectWrapperType.getInternalName();
      String primDesc = asmType.getDescriptor();

      mv.visitMethodInsn(Opcodes.INVOKESTATIC, objDesc, "valueOf", "(" + primDesc + ")L" + objDesc + ";");
    }
  }

  /**
   * Used to copy field values from the current instance to another.
   */
  public void addFieldCopy(MethodVisitor mv, ClassMeta classMeta) {

    if (isLocalField(classMeta)) {
      mv.visitFieldInsn(GETFIELD, fieldClass, fieldName, fieldDesc);
      mv.visitFieldInsn(PUTFIELD, fieldClass, fieldName, fieldDesc);
    } else {
      if (classMeta.isLog(4)) {
        classMeta.log(" ... addFieldCopy on non-local field [" + fieldName + "] type[" + fieldDesc + "]");
      }
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getNoInterceptMethodName, getMethodDesc);
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), setNoInterceptMethodName, setMethodDesc);
    }
  }

  /**
   * As part of the switch statement to read the fields generate the get code.
   */
  public void appendSwitchGet(MethodVisitor mv, ClassMeta classMeta, boolean intercept) {

    if (intercept) {
      // use the special get method with interception...
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getMethodName, getMethodDesc);
    } else {
      if (isLocalField(classMeta)) {
        mv.visitFieldInsn(GETFIELD, classMeta.getClassName(), fieldName, fieldDesc);
      } else {
        // field is on a superclass... so use virtual getNoInterceptMethodName
        mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getNoInterceptMethodName, getMethodDesc);
      }
    }

    if (primativeType) {
      appendValueOf(mv, classMeta);
    }
  }

  public void appendSwitchSet(MethodVisitor mv, ClassMeta classMeta, boolean intercept) {

    if (primativeType) {
      // convert Object to primitive first...
      Type objectWrapperType = PrimitiveHelper.getObjectWrapper(asmType);

      String primDesc = asmType.getDescriptor();
      String primType = asmType.getClassName();
      String objInt = objectWrapperType.getInternalName();
      mv.visitTypeInsn(CHECKCAST, objInt);

      mv.visitMethodInsn(INVOKEVIRTUAL, objInt, primType + "Value", "()" + primDesc);
    } else {
      // check correct object type
      mv.visitTypeInsn(CHECKCAST, asmType.getInternalName());
    }

    if (intercept) {
      // go through the set method to check for interception...
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), setMethodName, setMethodDesc);

    } else {
      //if (isLocalField(classMeta)) {
      //  mv.visitFieldInsn(PUTFIELD, fieldClass, fieldName, fieldDesc);
      //} else {
        mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), setNoInterceptMethodName, setMethodDesc);
      //}
    }
  }

  /**
   * Only for subclass generation - add public getter and setter methods for
   * interception.
   */
  public void addPublicGetSetMethods(ClassVisitor cv, ClassMeta classMeta, boolean checkExisting) {

    if (isPersistent()) {

      if (isId()) {
        // don't intercept id properties
        // setter required for propertyChangeListener
        addPublicSetMethod(cv, classMeta, checkExisting);

      } else {
        addPublicGetMethod(cv, classMeta, checkExisting);
        addPublicSetMethod(cv, classMeta, checkExisting);
      }
    }
  }

  private void addPublicGetMethod(ClassVisitor cv, ClassMeta classMeta, boolean checkExisting) {

    if (checkExisting && !classMeta.isExistingSuperMethod(publicGetterName, getMethodDesc)) {
      if (classMeta.isLog(1)) {
        classMeta.log("excluding " + publicGetterName + " as not on super object");
      }
      return;
    }

    addPublicGetMethod(new VisitMethodParams(cv, ACC_PUBLIC, publicGetterName, getMethodDesc, null, null), classMeta);
  }

  private void addPublicGetMethod(VisitMethodParams params, ClassMeta classMeta) {

    MethodVisitor mv = params.visitMethod();
    int iReturnOpcode = asmType.getOpcode(Opcodes.IRETURN);

    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, classMeta.getClassName(), INTERCEPT_FIELD, L_INTERCEPT);

    IndexFieldWeaver.visitIntInsn(mv, indexPosition);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "preGetter", "(I)V");

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(1, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, classMeta.getSuperClassName(), params.getName(), params.getDesc());
    mv.visitInsn(iReturnOpcode);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", "L" + classMeta.getClassName() + ";", null, l0, l2, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }

  private void addPublicSetMethod(ClassVisitor cv, ClassMeta classMeta, boolean checkExisting) {

    if (checkExisting && !classMeta.isExistingSuperMethod(publicSetterName, setMethodDesc)) {
      if (classMeta.isLog(1)) {
        classMeta.log("excluding " + publicSetterName + " as not on super object");
      }
      return;
    }

    addPublicSetMethod(new VisitMethodParams(cv, ACC_PUBLIC, publicSetterName, setMethodDesc, null, null), classMeta);
  }

  private void addPublicSetMethod(VisitMethodParams params, ClassMeta classMeta) {

    MethodVisitor mv = params.visitMethod();

    String publicGetterName = getPublicGetterName();

    String preSetterArgTypes = "Ljava/lang/Object;Ljava/lang/Object;";
    if (primativeType) {
      // preSetter method overloaded for primitive type comparison
      preSetterArgTypes = fieldDesc + fieldDesc;
    }

    // ALOAD or ILOAD etc
    int iLoadOpcode = asmType.getOpcode(Opcodes.ILOAD);

    // double and long have a size of 2
    int iPosition = asmType.getSize();

    String className = classMeta.getClassName();
    String superClassName = classMeta.getSuperClassName();

    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
    if (isInterceptSet()) {
      mv.visitInsn(ICONST_1);
    } else {
      // id or OneToMany field etc
      mv.visitInsn(ICONST_0);
    }

    String preSetterMethod = "preSetter";
    if (isMany()) {
      preSetterMethod = "preSetterMany";
    }

    IndexFieldWeaver.visitIntInsn(mv, indexPosition);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, className, publicGetterName, getMethodDesc);
    mv.visitVarInsn(iLoadOpcode, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, preSetterMethod, "(ZI"+ preSetterArgTypes + ")Ljava/beans/PropertyChangeEvent;");
    mv.visitVarInsn(ASTORE, 1 + iPosition);

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(1, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(iLoadOpcode, 1);
    mv.visitMethodInsn(INVOKESPECIAL, superClassName, params.getName(), params.getDesc());

    Label levt = new Label();
    mv.visitLabel(levt);
    mv.visitLineNumber(3, levt);

    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
    mv.visitVarInsn(ALOAD, 1 + iPosition);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, superClassName, publicGetterName, getMethodDesc);
    if (primativeType) {
      appendValueOf(mv, classMeta);
    }
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "postSetter", "(Ljava/beans/PropertyChangeEvent;Ljava/lang/Object;)V");

    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(1, l2);
    mv.visitInsn(RETURN);

    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLocalVariable("this", "L" + className + ";", null, l0, l3, 0);
    mv.visitLocalVariable("newValue", fieldDesc, null, l0, l3, 1);
    mv.visitLocalVariable("evt", "Ljava/beans/PropertyChangeEvent;", null, l1, l3, 2);
    mv.visitMaxs(5, 3);
    mv.visitEnd();

  }

  /**
   * Add get and set methods for field access/interception.
   */
  public void addGetSetMethods(ClassVisitor cv, ClassMeta classMeta) {

    if (!isLocalField(classMeta)) {
      String msg = "ERROR: " + fieldClass + " != " + classMeta.getClassName() + " for field "
          + fieldName + " " + fieldDesc;
      throw new RuntimeException(msg);
    }
    // add intercepting methods that are used to replace the
    // standard GETFIELD PUTFIELD byte codes for field access
    addGet(cv, classMeta);
    addSet(cv, classMeta);

    // add non-interception methods... so that we can get access
    // to private fields on super classes
    addGetNoIntercept(cv, classMeta);
    addSetNoIntercept(cv, classMeta);
  }

  private String getEbeanCollectionClass() {
    if (fieldDesc.equals("Ljava/util/List;")) {
      return "com/avaje/ebean/common/BeanList";
    }
    if (fieldDesc.equals("Ljava/util/Set;")) {
      return "com/avaje/ebean/common/BeanSet";
    }
    if (fieldDesc.equals("Ljava/util/Map;")) {
      return "com/avaje/ebean/common/BeanMap";
    }
    return null;
  }

  /**
   * Return true if null check should be added to this many field.
   */
  private boolean isInterceptMany() {

    if (isMany() && !isTransient()) {

      String ebCollection = getEbeanCollectionClass();
      if (ebCollection != null) {
        return true;
      } else {
        classMeta.log("Error unepxected many type " + fieldDesc);
      }
    }
    return false;
  }

  /**
   * Add a get field method with interception.
   */
  private void addGet(ClassVisitor cw, ClassMeta classMeta) {

    if (classMeta.isLog(3)) {
      classMeta.log(getMethodName + " " + getMethodDesc + " intercept:" + isInterceptGet() + " " + annotations);
    }

    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, getMethodName, getMethodDesc, null, null);
    mv.visitCode();

    if (isInterceptMany()) {
      addGetForMany(mv);
      return;
    }

    // ARETURN or IRETURN
    int iReturnOpcode = asmType.getOpcode(Opcodes.IRETURN);

    String className = classMeta.getClassName();

    Label labelEnd = new Label();
    Label labelStart = null;

    if (isInterceptGet()) {
      labelStart = new Label();
      mv.visitLabel(labelStart);
      mv.visitLineNumber(4, labelStart);
      mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
      IndexFieldWeaver.visitIntInsn(mv, indexPosition);
      mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "preGetter", "(I)V");
    }
    if (labelStart == null) {
      labelStart = labelEnd;
    }
    mv.visitLabel(labelEnd);
    mv.visitLineNumber(5, labelEnd);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, fieldName, fieldDesc);
    mv.visitInsn(iReturnOpcode);// ARETURN or IRETURN
    Label labelEnd1 = new Label();
    mv.visitLabel(labelEnd1);
    mv.visitLocalVariable("this", "L" + className + ";", null, labelStart, labelEnd1, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }

  private void addGetForMany(MethodVisitor mv) {

    String className = classMeta.getClassName();
    String ebCollection = getEbeanCollectionClass();

    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
    IndexFieldWeaver.visitIntInsn(mv, indexPosition);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "preGetter", "(I)V");

    Label l4 = new Label();
    if (classMeta.getEnhanceContext().isCheckNullManyFields()) {

      if (classMeta.isLog(3)) {
        classMeta.log("... add Many null check on " + fieldName + " ebtype:" + ebCollection);
      }

      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(2, l3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, fieldName, fieldDesc);

      mv.visitJumpInsn(IFNONNULL, l4);
      Label l5 = new Label();
      mv.visitLabel(l5);
      mv.visitLineNumber(3, l5);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitTypeInsn(NEW, ebCollection);
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, ebCollection, "<init>", "()V");
      mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);

      if (isManyToMany()) {
        // turn on modify listening for ManyToMany
        if (classMeta.isLog(3)) {
          classMeta.log("... add ManyToMany modify listening to " + fieldName);
        }

        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(4, l6);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, fieldName, fieldDesc);
        mv.visitTypeInsn(CHECKCAST, C_BEANCOLLECTION);
        mv.visitFieldInsn(GETSTATIC, C_BEANCOLLECTION + "$ModifyListenMode", "ALL", "L"
            + C_BEANCOLLECTION + "$ModifyListenMode;");
        mv.visitMethodInsn(INVOKEINTERFACE, C_BEANCOLLECTION, "setModifyListening", "(L"
            + C_BEANCOLLECTION + "$ModifyListenMode;)V");
      }
    }

    mv.visitLabel(l4);
    mv.visitLineNumber(5, l4);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, fieldName, fieldDesc);
    mv.visitInsn(ARETURN);
    Label l7 = new Label();
    mv.visitLabel(l7);
    mv.visitLocalVariable("this", "L" + className + ";", null, l0, l7, 0);
    mv.visitMaxs(3, 1);
    mv.visitEnd();
  }

  /**
   * This is a get method with no interception.
   * <p>
   * It exists to be able to read private fields that are on super classes.
   * </p>
   */
  private void addGetNoIntercept(ClassVisitor cw, ClassMeta classMeta) {

    // ARETURN or IRETURN
    int iReturnOpcode = asmType.getOpcode(Opcodes.IRETURN);

    if (classMeta.isLog(3)) {
      classMeta.log(getNoInterceptMethodName + " " + getMethodDesc);
    }

    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, getNoInterceptMethodName, getMethodDesc, null, null);
    mv.visitCode();

    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, fieldClass, fieldName, fieldDesc);
    mv.visitInsn(iReturnOpcode);// ARETURN or IRETURN
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", "L" + fieldClass + ";", null, l0, l2, 0);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }

  /**
   * Setter method with interception.
   * 
   * <pre>
   * public void _ebean_set_propname(String newValue) {
   *   PropertyChangeEvent evt = ebi.preSetter(true, &quot;propname&quot;, _ebean_get_propname(), newValue);
   *   this.propname = newValue;
   *   ebi.postSetter(evt);
   * }
   * </pre>
   */
  private void addSet(ClassVisitor cw, ClassMeta classMeta) {

    String preSetterArgTypes = "Ljava/lang/Object;Ljava/lang/Object;";
    if (!objectType) {
      // preSetter method overloaded for primitive type comparison
      preSetterArgTypes = fieldDesc + fieldDesc;
    }

    // ALOAD or ILOAD etc
    int iLoadOpcode = asmType.getOpcode(Opcodes.ILOAD);

    // double and long have a size of 2
    int iPosition = asmType.getSize();

    if (classMeta.isLog(3)) {
      classMeta.log(setMethodName + " " + setMethodDesc + " intercept:" + isInterceptSet()
          + " opCode:" + iLoadOpcode + "," + iPosition + " preSetterArgTypes" + preSetterArgTypes);
    }

    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, setMethodName, setMethodDesc, null, null);
    mv.visitCode();

    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, fieldClass, INTERCEPT_FIELD, L_INTERCEPT);
    if (isInterceptSet()) {
      mv.visitInsn(ICONST_1);
    } else {
      // id or OneToMany field etc
      mv.visitInsn(ICONST_0);
    }
    IndexFieldWeaver.visitIntInsn(mv, indexPosition);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, fieldClass, getMethodName, getMethodDesc);
    mv.visitVarInsn(iLoadOpcode, 1);
    String preSetterMethod = "preSetter";
    if (isMany()) {
      preSetterMethod = "preSetterMany";
    }
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, preSetterMethod, "(ZI"+ preSetterArgTypes + ")Ljava/beans/PropertyChangeEvent;");
    mv.visitVarInsn(ASTORE, 1 + iPosition);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(2, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(iLoadOpcode, 1);// ALOAD or ILOAD
    mv.visitFieldInsn(PUTFIELD, fieldClass, fieldName, fieldDesc);

    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(3, l2);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, fieldClass, INTERCEPT_FIELD, L_INTERCEPT);
    mv.visitVarInsn(ALOAD, 1 + iPosition);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "postSetter", "(Ljava/beans/PropertyChangeEvent;)V");

    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLineNumber(4, l3);
    mv.visitInsn(RETURN);
    Label l4 = new Label();
    mv.visitLabel(l4);
    mv.visitLocalVariable("this", "L" + fieldClass + ";", null, l0, l4, 0);
    mv.visitLocalVariable("newValue", fieldDesc, null, l0, l4, 1);
    mv.visitLocalVariable("evt", "Ljava/beans/PropertyChangeEvent;", null, l1, l4, 2);
    mv.visitMaxs(5, 3);
    mv.visitEnd();
  }

  /**
   * Add a non-intercepting field set method.
   * <p>
   * So we can set private fields on super classes.
   * </p>
   */
  private void addSetNoIntercept(ClassVisitor cw, ClassMeta classMeta) {

    // ALOAD or ILOAD etc
    int iLoadOpcode = asmType.getOpcode(Opcodes.ILOAD);

    // double and long have a size of 2
    int iPosition = asmType.getSize();

    if (classMeta.isLog(3)) {
      classMeta.log(setNoInterceptMethodName + " " + setMethodDesc + " opCode:" + iLoadOpcode + "," + iPosition);
    }

    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, setNoInterceptMethodName, setMethodDesc, null, null);
    mv.visitCode();
    Label l0 = new Label();

    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(iLoadOpcode, 1);// ALOAD or ILOAD
    mv.visitFieldInsn(PUTFIELD, fieldClass, fieldName, fieldDesc);

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(2, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, fieldClass, INTERCEPT_FIELD, L_INTERCEPT);
    IndexFieldWeaver.visitIntInsn(mv, indexPosition);
    mv.visitMethodInsn(INVOKEVIRTUAL, C_INTERCEPT, "setLoadedProperty", "(I)V");
    
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(1, l2);
    mv.visitInsn(RETURN);
    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLocalVariable("this", "L" + fieldClass + ";", null, l0, l3, 0);
    mv.visitLocalVariable("_newValue", fieldDesc, null, l0, l3, 1);
    mv.visitMaxs(4, 2);
    mv.visitEnd();
  }

}