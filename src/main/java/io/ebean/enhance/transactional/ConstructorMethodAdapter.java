package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Adapts constructor method code with profile location for query beans and finders.
 */
class ConstructorMethodAdapter extends FinallyAdapter implements EnhanceConstants {

  private static final String QP_FIELD_PREFIX = ClassAdapterTransactional.QP_FIELD_PREFIX;

  final ClassAdapterTransactional classAdapter;

  ConstructorMethodAdapter(ClassAdapterTransactional classAdapter, final MethodVisitor mv, final int access, final String name, final String desc) {
    super(Opcodes.ASM7, mv, access, name, desc);
    this.classAdapter = classAdapter;
  }

  @Override
  public String toString() {
    return classAdapter.className();
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

    if (!classAdapter.isEnableProfileLocation()) {
      super.visitMethodInsn(opcode, owner, name, desc, itf);

    } else if (INIT.equals(name) && classAdapter.isQueryBean(owner)) {
      super.visitMethodInsn(opcode, owner, name, desc, itf);

      int fieldIdx = classAdapter.nextQueryProfileLocation();
      if (classAdapter.isLog(2)) {
        classAdapter.log("add profile location " + fieldIdx);
      }
      mv.visitFieldInsn(GETSTATIC, classAdapter.className(), QP_FIELD_PREFIX + fieldIdx, "Lio/ebean/ProfileLocation;");
      mv.visitMethodInsn(INVOKEVIRTUAL, owner, "setProfileLocation", "(Lio/ebean/ProfileLocation;)Ljava/lang/Object;", false);
      mv.visitTypeInsn(CHECKCAST, owner);

    } else if (!classAdapter.isFinder()) {
      super.visitMethodInsn(opcode, owner, name, desc, itf);

    } else {
      // enhance method in Finder with profileLocation awareness
      if (isNewQuery(name, desc)) {
        int fieldIdx = classAdapter.nextQueryProfileLocation();
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
        mv.visitFieldInsn(GETSTATIC, classAdapter.className(), QP_FIELD_PREFIX + fieldIdx, "Lio/ebean/ProfileLocation;");
        mv.visitMethodInsn(INVOKEINTERFACE, "io/ebean/Query", "setProfileLocation", "(Lio/ebean/ProfileLocation;)Lio/ebean/Query;", true);

      } else if (isNewUpdateQuery(name, desc)) {
        int fieldIdx = classAdapter.nextQueryProfileLocation();
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
        mv.visitFieldInsn(GETSTATIC, classAdapter.className(), QP_FIELD_PREFIX + fieldIdx, "Lio/ebean/ProfileLocation;");
        mv.visitMethodInsn(INVOKEINTERFACE, "io/ebean/UpdateQuery", "setProfileLocation", "(Lio/ebean/ProfileLocation;)Lio/ebean/UpdateQuery;", true);

      } else {
        super.visitMethodInsn(opcode, owner, name, desc, itf);
      }
    }
  }

  private boolean isNewUpdateQuery(String name, String desc) {
    return name.equals("update") && desc.equals("()Lio/ebean/UpdateQuery;");
  }

  private boolean isNewQuery(String name, String desc) {
    if (name.equals("query") && (desc.equals("()Lio/ebean/Query;") || desc.equals("(Ljava/lang/String;)Lio/ebean/Query;"))) {
      return true;
    }
    if (name.equals("nativeSql") && desc.equals("(Ljava/lang/String;)Lio/ebean/Query;")) {
      return true;
    }
    return false;
  }

}
