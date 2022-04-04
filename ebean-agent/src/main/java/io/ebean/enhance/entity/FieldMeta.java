package io.ebean.enhance.entity;

import io.ebean.enhance.asm.*;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.common.VisitUtil;

import java.util.HashSet;

/**
 * Holds meta data for a field.
 * <p>
 * This can then generate the appropriate byte code for this field.
 * </p>
 */
public class FieldMeta implements Opcodes, EnhanceConstants {

  private final ClassMeta classMeta;
  private final String fieldClass;
  private final String fieldName;
  private final String fieldDesc;

  private final HashSet<String> annotations = new HashSet<>();

  private final Type asmType;

  private final boolean primitiveType;
  private final boolean objectType;

  private final String getMethodName;
  private final String getMethodDesc;
  private final String setMethodName;
  private final String setMethodDesc;
  private final String getNoInterceptMethodName;
  private final String setNoInterceptMethodName;

  private int indexPosition;

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
    this.asmType = Type.getType(desc);

    int sort = asmType.getSort();
    this.primitiveType = sort > Type.VOID && sort <= Type.DOUBLE;
    this.objectType = sort == Type.OBJECT;
    this.getMethodDesc = "()" + desc;
    this.setMethodDesc = "(" + desc + ")V";
    this.getMethodName = "_ebean_get_" + name;
    this.setMethodName = "_ebean_set_" + name;
    this.getNoInterceptMethodName = "_ebean_getni_" + name;
    this.setNoInterceptMethodName = "_ebean_setni_" + name;
  }

  public void setIndexPosition(int indexPosition) {
    this.indexPosition = indexPosition;
  }

  @Override
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
   * Return true if this is a primitiveType.
   */
  public boolean isPrimitiveType() {
    return primitiveType;
  }

  /**
   * Add a field annotation.
   */
  void addAnnotationDesc(String desc) {
    annotations.add(desc);
  }

  /**
   * Return the field name.
   */
  public String getName() {
    return fieldName;
  }

  private boolean isInterceptGet() {
    return !isId() && !isTransient();
  }

  private boolean isInterceptSet() {
    return !isId() && !isTransient() && !isToMany();
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
    return annotations.contains("Ljavax/persistence/Transient;")
      || annotations.contains(L_DRAFT);
  }

  /**
   * Return true if this is an ID field.
   * <p>
   * ID fields are used in generating equals() logic based on identity.
   * </p>
   */
  public boolean isId() {
    return (annotations.contains("Ljavax/persistence/Id;")
      || annotations.contains("Ljavax/persistence/EmbeddedId;"));
  }

  /**
   * Return true if this is a OneToMany or ManyToMany field.
   */
  public boolean isToMany() {
    return annotations.contains("Ljavax/persistence/OneToMany;")
      || annotations.contains("Ljavax/persistence/ManyToMany;");
  }

  private boolean isManyToMany() {
    return annotations.contains("Ljavax/persistence/ManyToMany;");
  }

  /**
   * Control initialisation of ToMany and DbArray collection properties.
   * This means these properties are lazy initialised on demand.
   */
  public boolean isInitMany() {
    return isToMany() || isDbArray();
  }

  private boolean isDbArray() {
    return annotations.contains("Lio/ebean/annotation/DbArray;");
  }

  /**
   * Return true if this is an Embedded field.
   */
  boolean isEmbedded() {
    return annotations.contains("Ljavax/persistence/Embedded;");
  }

  boolean hasOrderColumn() {
    return annotations.contains("Ljavax/persistence/OrderColumn;");
  }

  /**
   * Return true if the field is local to this class. Returns false if the field
   * is actually on a super class.
   */
  boolean isLocalField(ClassMeta classMeta) {
    return fieldClass.equals(classMeta.getClassName());
  }

  /**
   * Append byte code to return the Id value (for primitives).
   */
  void appendGetPrimitiveIdValue(MethodVisitor mv, ClassMeta classMeta) {
    mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getMethodName, getMethodDesc, false);
  }

  /**
   * Append compare instructions if its a long, float or double.
   */
  void appendCompare(MethodVisitor mv, ClassMeta classMeta) {
    if (primitiveType) {
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
      }
      // no extra instructions required for
      // int, short, byte, char
    }
  }

  /**
   * Append code to get the Object value of a primitive.
   * <p>
   * This becomes a Integer.valueOf(someInt); or similar.
   * </p>
   */
  void appendValueOf(MethodVisitor mv) {
    if (primitiveType) {
      // use valueOf methods to return primitives as objects
      Type objectWrapperType = PrimitiveHelper.getObjectWrapper(asmType);
      String objDesc = objectWrapperType.getInternalName();
      String primDesc = asmType.getDescriptor();
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, objDesc, "valueOf", "(" + primDesc + ")L" + objDesc + ";", false);
    }
  }

  /**
   * As part of the switch statement to read the fields generate the get code.
   */
  void appendSwitchGet(MethodVisitor mv, ClassMeta classMeta, boolean intercept) {
    if (intercept) {
      // use the special get method with interception...
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getMethodName, getMethodDesc, false);
    } else {
      if (isLocalField(classMeta)) {
        mv.visitFieldInsn(GETFIELD, classMeta.getClassName(), fieldName, fieldDesc);
      } else {
        // field is on a superclass... so use virtual getNoInterceptMethodName
        mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), getNoInterceptMethodName, getMethodDesc, false);
      }
    }
    if (primitiveType) {
      appendValueOf(mv);
    }
  }

  void appendSwitchSet(MethodVisitor mv, ClassMeta classMeta, boolean intercept) {
    if (primitiveType) {
      // convert Object to primitive first...
      Type objectWrapperType = PrimitiveHelper.getObjectWrapper(asmType);
      String primDesc = asmType.getDescriptor();
      String primType = asmType.getClassName();
      String objInt = objectWrapperType.getInternalName();
      mv.visitTypeInsn(CHECKCAST, objInt);
      mv.visitMethodInsn(INVOKEVIRTUAL, objInt, primType + "Value", "()" + primDesc, false);
    } else {
      // check correct object type
      mv.visitTypeInsn(CHECKCAST, asmType.getInternalName());
    }

    if (intercept) {
      // go through the set method to check for interception...
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), setMethodName, setMethodDesc, false);
    } else {
      mv.visitMethodInsn(INVOKEVIRTUAL, classMeta.getClassName(), setNoInterceptMethodName, setMethodDesc, false);
    }
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

  private String getInitCollectionClass() {
    final boolean dbArray = isDbArray();
    if (fieldDesc.equals("Ljava/util/List;")) {
      return dbArray ? ARRAYLIST : BEANLIST;
    }
    if (fieldDesc.equals("Ljava/util/Set;")) {
      return dbArray ? LINKEDHASHSET : BEANSET;
    }
    if (fieldDesc.equals("Ljava/util/Map;")) {
      return dbArray ? LINKEDHASHMAP : BEANMAP;
    }
    return null;
  }

  /**
   * Add a get field method with interception.
   */
  private void addGet(ClassVisitor cw, ClassMeta classMeta) {
    MethodVisitor mv = cw.visitMethod(classMeta.accProtected(), getMethodName, getMethodDesc, null, null);
    mv.visitCode();

    if (isInitMany()) {
      addGetForMany(mv);
      return;
    }

    // ARETURN or IRETURN
    int iReturnOpcode = asmType.getOpcode(Opcodes.IRETURN);

    String className = classMeta.getClassName();

    Label labelEnd = new Label();
    Label labelStart = null;

    int maxVars = 1;
    if (isId()) {
      labelStart = new Label();
      mv.visitLabel(labelStart);
      mv.visitLineNumber(5, labelStart);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
      classMeta.visitMethodInsnIntercept(mv, "preGetId", NOARG_VOID);

    } else if (isInterceptGet()) {
      maxVars = 2;
      labelStart = new Label();
      mv.visitLabel(labelStart);
      mv.visitLineNumber(6, labelStart);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
      VisitUtil.visitIntInsn(mv, indexPosition);
      classMeta.visitMethodInsnIntercept(mv, "preGetter", "(I)V");
    }
    if (labelStart == null) {
      labelStart = labelEnd;
    }
    mv.visitLabel(labelEnd);
    mv.visitLineNumber(7, labelEnd);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, fieldName, fieldDesc);
    mv.visitInsn(iReturnOpcode);// ARETURN or IRETURN
    Label labelEnd1 = new Label();
    mv.visitLabel(labelEnd1);
    mv.visitLocalVariable("this", "L" + className + ";", null, labelStart, labelEnd1, 0);
    mv.visitMaxs(maxVars, 1);
    mv.visitEnd();
  }

  private void addGetForMany(MethodVisitor mv) {
    String className = classMeta.getClassName();
    String ebCollection = getInitCollectionClass();

    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
    VisitUtil.visitIntInsn(mv, indexPosition);
    classMeta.visitMethodInsnIntercept(mv, "preGetter", "(I)V");

    Label l4 = new Label();
    if (classMeta.getEnhanceContext().isCheckNullManyFields()) {
      if (ebCollection == null) {
        String msg = "Unexpected collection type [" + Type.getType(fieldDesc).getClassName() + "] for ["
        + classMeta.getClassName() + "." + fieldName + "] expected either java.util.List, java.util.Set or java.util.Map ";
        throw new RuntimeException(msg);
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
      mv.visitMethodInsn(INVOKESPECIAL, ebCollection, INIT, NOARG_VOID, false);
      mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);

      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, INTERCEPT_FIELD, L_INTERCEPT);
      VisitUtil.visitIntInsn(mv, indexPosition);
      classMeta.visitMethodInsnIntercept(mv, "initialisedMany", "(I)V");

      if (isManyToMany() || hasOrderColumn()) {
        // turn on modify listening for ManyToMany
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(4, l6);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, fieldName, fieldDesc);
        mv.visitTypeInsn(CHECKCAST, C_BEANCOLLECTION);
        mv.visitFieldInsn(GETSTATIC, C_BEANCOLLECTION + "$ModifyListenMode", "ALL", "L" + C_BEANCOLLECTION + "$ModifyListenMode;");
        mv.visitMethodInsn(INVOKEINTERFACE, C_BEANCOLLECTION, "setModifyListening", "(L" + C_BEANCOLLECTION + "$ModifyListenMode;)V", true);
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

    MethodVisitor mv = cw.visitMethod(classMeta.accProtected(), getNoInterceptMethodName, getMethodDesc, null, null);
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
   * <pre>
   * public void _ebean_set_propname(String newValue) {
   *   ebi.preSetter(true, propertyIndex, _ebean_get_propname(), newValue);
   *   this.propname = newValue;
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
    MethodVisitor mv = cw.visitMethod(classMeta.accProtected(), setMethodName, setMethodDesc, null, null);
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
    VisitUtil.visitIntInsn(mv, indexPosition);
    mv.visitVarInsn(ALOAD, 0);
    if (isId() || isToManyGetField(classMeta)) {
      // skip getter on Id as we now intercept that via preGetId() for automatic jdbc batch flushing
      mv.visitFieldInsn(GETFIELD, fieldClass, fieldName, fieldDesc);
    } else {
      mv.visitMethodInsn(INVOKEVIRTUAL, fieldClass, getMethodName, getMethodDesc, false);
    }
    mv.visitVarInsn(iLoadOpcode, 1);
    String preSetterMethod = "preSetter";
    if (isToMany()) {
      preSetterMethod = "preSetterMany";
    }
    classMeta.visitMethodInsnIntercept(mv, preSetterMethod, "(ZI" + preSetterArgTypes + ")V");
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(2, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(iLoadOpcode, 1);// ALOAD or ILOAD
    mv.visitFieldInsn(PUTFIELD, fieldClass, fieldName, fieldDesc);

    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLineNumber(4, l3);
    mv.visitInsn(RETURN);
    Label l4 = new Label();
    mv.visitLabel(l4);
    mv.visitLocalVariable("this", "L" + fieldClass + ";", null, l0, l4, 0);
    mv.visitLocalVariable("newValue", fieldDesc, null, l0, l4, 1);
    mv.visitMaxs(5, 2);
    mv.visitEnd();
  }

  private boolean isToManyGetField(ClassMeta meta) {
    return isToMany() && meta.isToManyGetField();
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
    MethodVisitor mv = cw.visitMethod(classMeta.accProtected(), setNoInterceptMethodName, setMethodDesc, null, null);
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
    VisitUtil.visitIntInsn(mv, indexPosition);
    classMeta.visitMethodInsnIntercept(mv, "setLoadedProperty", "(I)V");

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
