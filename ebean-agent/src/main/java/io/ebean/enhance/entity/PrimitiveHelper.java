package io.ebean.enhance.entity;

import io.ebean.enhance.asm.Type;

/**
 * Maps primitive types to their Object types.
 */
final class PrimitiveHelper {

  private static final Type INTEGER_OBJECT = Type.getType(Integer.class);
  private static final Type SHORT_OBJECT = Type.getType(Short.class);
  private static final Type CHARACTER_OBJECT = Type.getType(Character.class);
  private static final Type LONG_OBJECT = Type.getType(Long.class);
  private static final Type DOUBLE_OBJECT = Type.getType(Double.class);
  private static final Type FLOAT_OBJECT = Type.getType(Float.class);
  private static final Type BYTE_OBJECT = Type.getType(Byte.class);
  private static final Type BOOLEAN_OBJECT = Type.getType(Boolean.class);

  static Type getObjectWrapper(Type primitiveAsmType) {
    int sort = primitiveAsmType.getSort();
    switch (sort) {
      case Type.INT:
        return INTEGER_OBJECT;
      case Type.SHORT:
        return SHORT_OBJECT;
      case Type.CHAR:
        return CHARACTER_OBJECT;
      case Type.LONG:
        return LONG_OBJECT;
      case Type.DOUBLE:
        return DOUBLE_OBJECT;
      case Type.FLOAT:
        return FLOAT_OBJECT;
      case Type.BYTE:
        return BYTE_OBJECT;
      case Type.BOOLEAN:
        return BOOLEAN_OBJECT;

      default:
        throw new RuntimeException("Expected primative? " + primitiveAsmType);
    }
  }

}
