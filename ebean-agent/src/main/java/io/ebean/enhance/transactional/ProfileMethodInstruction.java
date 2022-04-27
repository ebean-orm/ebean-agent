package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.EnhanceConstants;

/**
 * Used in constructor and method code to add profile location for query beans and finders.
 */
class ProfileMethodInstruction implements EnhanceConstants, Opcodes {

  private static final String QP_FIELD_PREFIX = ClassAdapterTransactional.QP_FIELD_PREFIX;

  private final ClassAdapterTransactional classAdapter;
  private final MethodVisitor mv;

  ProfileMethodInstruction(ClassAdapterTransactional classAdapter, final MethodVisitor mv) {
    this.classAdapter = classAdapter;
    this.mv = mv;
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    if (!classAdapter.isEnableProfileLocation()) {
      mv.visitMethodInsn(opcode, owner, name, desc, itf);

    } else if (INIT.equals(name) && classAdapter.isQueryBean(owner)) {
      mv.visitMethodInsn(opcode, owner, name, desc, itf);
      if (!isAssocQueryBean(owner)) {
        int fieldIdx = classAdapter.nextQueryProfileLocation();
        if (classAdapter.isLog(4)) {
          classAdapter.log("add profile location " + fieldIdx);
        }
        mv.visitFieldInsn(GETSTATIC, classAdapter.className(), QP_FIELD_PREFIX + fieldIdx, "Lio/ebean/ProfileLocation;");
        mv.visitMethodInsn(INVOKEVIRTUAL, owner, "setProfileLocation", "(Lio/ebean/ProfileLocation;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, owner);
      }

    } else if (!classAdapter.isFinder()) {
      mv.visitMethodInsn(opcode, owner, name, desc, itf);

    } else {
      // enhance method in Finder with profileLocation awareness
      if (isNewQuery(name, desc)) {
        int fieldIdx = classAdapter.nextQueryProfileLocation();
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
        mv.visitFieldInsn(GETSTATIC, classAdapter.className(), QP_FIELD_PREFIX + fieldIdx, "Lio/ebean/ProfileLocation;");
        mv.visitMethodInsn(INVOKEINTERFACE, "io/ebean/Query", "setProfileLocation", "(Lio/ebean/ProfileLocation;)Lio/ebean/Query;", true);
        if (classAdapter.isLog(4)) {
          classAdapter.log("add profile location " + fieldIdx);
        }
      } else if (isNewUpdateQuery(name, desc)) {
        int fieldIdx = classAdapter.nextQueryProfileLocation();
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
        mv.visitFieldInsn(GETSTATIC, classAdapter.className(), QP_FIELD_PREFIX + fieldIdx, "Lio/ebean/ProfileLocation;");
        mv.visitMethodInsn(INVOKEINTERFACE, "io/ebean/UpdateQuery", "setProfileLocation", "(Lio/ebean/ProfileLocation;)Lio/ebean/UpdateQuery;", true);
        if (classAdapter.isLog(4)) {
          classAdapter.log("add profile location " + fieldIdx);
        }
      } else {
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
      }
    }
  }

  private boolean isAssocQueryBean(String owner) {
    return owner.contains("/query/assoc/QAssoc");
  }

  private boolean isNewUpdateQuery(String name, String desc) {
    return name.equals("update") && desc.equals("()Lio/ebean/UpdateQuery;");
  }

  private boolean isNewQuery(String name, String desc) {
    if (name.equals("query") && (desc.equals("()Lio/ebean/Query;") || desc.equals("(Ljava/lang/String;)Lio/ebean/Query;"))) {
      return true;
    }
    return name.equals("nativeSql") && desc.equals("(Ljava/lang/String;)Lio/ebean/Query;");
  }

}
