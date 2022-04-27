package io.ebean.enhance.entity;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.VisitUtil;

import java.util.List;

import static io.ebean.enhance.common.EnhanceConstants.CLINIT;
import static io.ebean.enhance.common.EnhanceConstants.INIT;
import static io.ebean.enhance.common.EnhanceConstants.L_OBJECT;
import static io.ebean.enhance.common.EnhanceConstants.NOARG_VOID;

/**
 * Generate the methods based on the list of fields.
 * <p>
 * This includes the createCopy, getField and setField methods etc.
 * </p>
 */
class IndexFieldWeaver implements Opcodes {

  private static final String _EBEAN_PROPS = "_ebean_props";

  static void addPropertiesField(ClassVisitor cv, ClassMeta meta) {
    FieldVisitor fv = cv.visitField(meta.accPublic() + ACC_STATIC, _EBEAN_PROPS, "[Ljava/lang/String;", null, null);
    fv.visitEnd();
  }

  static void addPropertiesInit(ClassVisitor cv, ClassMeta classMeta) {
    MethodVisitor mv = cv.visitMethod(ACC_STATIC, CLINIT, NOARG_VOID, null, null);
    mv.visitCode();
    addPropertiesInit(mv, classMeta);

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(1, l1);
    mv.visitInsn(RETURN);
    mv.visitMaxs(4, 0);
    mv.visitEnd();
  }

  static void addPropertiesInit(MethodVisitor mv, ClassMeta classMeta) {
    List<FieldMeta> fields = classMeta.getAllFields();

    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    VisitUtil.visitIntInsn(mv, fields.size());
    mv.visitTypeInsn(ANEWARRAY, "java/lang/String");

    if (fields.isEmpty()) {
      if (classMeta.isLog(4)) {
        classMeta.log("Has no fields?");
      }
    } else {
      for (int i = 0; i < fields.size(); i++) {
        FieldMeta field = fields.get(i);
        mv.visitInsn(DUP);
        VisitUtil.visitIntInsn(mv, i);
        mv.visitLdcInsn(field.getName());
        mv.visitInsn(AASTORE);
      }
    }

    mv.visitFieldInsn(PUTSTATIC, classMeta.getClassName(), _EBEAN_PROPS, "[Ljava/lang/String;");
  }


  static void addGetPropertyNames(ClassVisitor cv, ClassMeta classMeta) {
    MethodVisitor mv = cv.visitMethod(classMeta.accPublic(), "_ebean_getPropertyNames", "()[Ljava/lang/String;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(13, l0);
    mv.visitFieldInsn(GETSTATIC, classMeta.getClassName(), _EBEAN_PROPS, "[Ljava/lang/String;");
    mv.visitInsn(ARETURN);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + classMeta.getClassName() + ";", null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  static void addGetPropertyName(ClassVisitor cv, ClassMeta classMeta) {
    MethodVisitor mv = cv.visitMethod(classMeta.accPublic(), "_ebean_getPropertyName", "(I)Ljava/lang/String;", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(16, l0);
    mv.visitFieldInsn(GETSTATIC, classMeta.getClassName(), _EBEAN_PROPS, "[Ljava/lang/String;");
    mv.visitVarInsn(ILOAD, 1);
    mv.visitInsn(AALOAD);
    mv.visitInsn(ARETURN);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", "L" + classMeta.getClassName() + ";", null, l0, l1, 0);
    mv.visitLocalVariable("pos", "I", null, l0, l1, 1);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  static void addMethods(ClassVisitor cv, ClassMeta classMeta) {
    List<FieldMeta> fields = classMeta.getAllFields();
    if (fields.isEmpty()) {
      return;
    }

    generateGetField(cv, classMeta, fields, false);
    generateGetField(cv, classMeta, fields, true);

    generateSetField(cv, classMeta, fields, false);
    generateSetField(cv, classMeta, fields, true);

    if (classMeta.hasEqualsOrHashCode()) {
      // equals or hashCode is already implemented
      if (classMeta.isLog(4)) {
        classMeta.log("... skipping add equals() ... already has equals() hashcode() methods");
      }
      return;
    }

    // search for the id field...
    int idIndex = -1;
    FieldMeta idFieldMeta = null;

    // find id field only local to this class
    for (int i = 0; i < fields.size(); i++) {
      FieldMeta fieldMeta = fields.get(i);
      if (fieldMeta.isId() && fieldMeta.isLocalField(classMeta)) {
        if (idIndex == -1) {
          // we have found an id field
          idIndex = i;
          idFieldMeta = fieldMeta;
        } else {
          // there are 2 or more id fields
          idIndex = -2;
        }
      }
    }

    if (idIndex == -2) {
      // there are 2 or more id fields?
      if (classMeta.isLog(2)) {
        classMeta.log("has 2 or more id fields. Not adding equals() method.");
      }

    } else if (idIndex == -1) {
      // there are no id fields local to this type
      if (classMeta.isLog(4)) {
        classMeta.log("has no id fields on this type. Not adding equals() method. Expected when Id property on superclass.");
      }

    } else {
      // add the _ebean_getIdentity(), equals() and hashCode() methods
      MethodEquals.addMethods(cv, classMeta, idIndex, idFieldMeta);
    }
  }

  /**
   * Generate the invokeGet method.
   */
  private static void generateGetField(ClassVisitor cv, ClassMeta classMeta, List<FieldMeta> fields, boolean intercept) {
    String className = classMeta.getClassName();

    MethodVisitor mv;
    if (intercept) {
      mv = cv.visitMethod(classMeta.accPublic(), "_ebean_getFieldIntercept", "(I)Ljava/lang/Object;", null, null);
    } else {
      mv = cv.visitMethod(classMeta.accPublic(), "_ebean_getField", "(I)Ljava/lang/Object;", null, null);
    }

    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);
    mv.visitVarInsn(ILOAD, 1);

    Label[] switchLabels = new Label[fields.size()];
    for (int i = 0; i < switchLabels.length; i++) {
      switchLabels[i] = new Label();
    }

    int maxIndex = switchLabels.length - 1;

    Label labelException = new Label();
    mv.visitTableSwitchInsn(0, maxIndex, labelException, switchLabels);

    for (int i = 0; i < fields.size(); i++) {

      FieldMeta fieldMeta = fields.get(i);

      mv.visitLabel(switchLabels[i]);
      mv.visitLineNumber(1, switchLabels[i]);
      mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
      mv.visitVarInsn(ALOAD, 0);

      fieldMeta.appendSwitchGet(mv, classMeta, intercept);

      mv.visitInsn(ARETURN);
    }

    mv.visitLabel(labelException);
    mv.visitLineNumber(1, labelException);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
    mv.visitInsn(DUP);
    mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
    mv.visitInsn(DUP);
    mv.visitLdcInsn("Invalid index ");
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", INIT, "(Ljava/lang/String;)V", false);
    mv.visitVarInsn(ILOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", INIT, "(Ljava/lang/String;)V", false);
    mv.visitInsn(ATHROW);

    Label l5 = new Label();
    mv.visitLabel(l5);
    mv.visitLocalVariable("this", "L" + className + ";", null, l0, l5, 0);
    mv.visitLocalVariable("index", "I", null, l0, l5, 1);
    mv.visitMaxs(5, 2);
    mv.visitEnd();
  }

  /**
   * Generate the _ebean_setField or _ebean_setFieldBypass method.
   * <p>
   * Bypass will bypass the interception. The interception checks that the
   * property has been loaded and creates oldValues if the bean is being made
   * dirty for the first time.
   * </p>
   */
  private static void generateSetField(ClassVisitor cv, ClassMeta classMeta, List<FieldMeta> fields, boolean intercept) {
    String className = classMeta.getClassName();

    MethodVisitor mv;
    if (intercept) {
      mv = cv.visitMethod(classMeta.accPublic(), "_ebean_setFieldIntercept", "(ILjava/lang/Object;)V",
        null, null);
    } else {
      mv = cv.visitMethod(classMeta.accPublic(), "_ebean_setField", "(ILjava/lang/Object;)V", null, null);
    }

    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(1, l0);

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(1, l1);
    mv.visitVarInsn(ILOAD, 1);

    Label[] switchLabels = new Label[fields.size()];
    for (int i = 0; i < switchLabels.length; i++) {
      switchLabels[i] = new Label();
    }

    Label labelException = new Label();

    int maxIndex = switchLabels.length - 1;

    mv.visitTableSwitchInsn(0, maxIndex, labelException, switchLabels);

    for (int i = 0; i < fields.size(); i++) {

      FieldMeta fieldMeta = fields.get(i);

      mv.visitLabel(switchLabels[i]);
      mv.visitLineNumber(1, switchLabels[i]);

      mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 2);

      fieldMeta.appendSwitchSet(mv, classMeta, intercept);

      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLineNumber(1, l6);
      mv.visitInsn(RETURN);
    }

    mv.visitLabel(labelException);
    mv.visitLineNumber(1, labelException);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
    mv.visitInsn(DUP);
    mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
    mv.visitInsn(DUP);
    mv.visitLdcInsn("Invalid index ");
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", INIT, "(Ljava/lang/String;)V", false);
    mv.visitVarInsn(ILOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", INIT, "(Ljava/lang/String;)V", false);
    mv.visitInsn(ATHROW);
    Label l9 = new Label();
    mv.visitLabel(l9);
    mv.visitLocalVariable("this", "L" + className + ";", null, l0, l9, 0);
    mv.visitLocalVariable("index", "I", null, l0, l9, 1);
    mv.visitLocalVariable("o", L_OBJECT, null, l0, l9, 2);
    mv.visitLocalVariable("arg", L_OBJECT, null, l0, l9, 3);
    mv.visitLocalVariable("p", "L" + className + ";", null, l1, l9, 4);
    mv.visitMaxs(5, 5);
    mv.visitEnd();
  }

}
