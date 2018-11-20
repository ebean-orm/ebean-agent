package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.Opcodes;

import java.util.List;

/**
 * Add generated 'property access' methods for the fields.
 */
public class TypeQueryAddMethods implements Opcodes {

  /**
  * Add the generated 'property access' methods.
  */
  public static void add(ClassVisitor  cw, ClassInfo classInfo, boolean typeQueryRootBean) {

    List<FieldInfo> fields = classInfo.getFields();

    if (fields != null) {
      for (FieldInfo field : fields) {
        field.writeMethod(cw, typeQueryRootBean);
      }
    }
  }

}
