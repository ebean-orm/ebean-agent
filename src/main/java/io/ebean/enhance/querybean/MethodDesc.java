package io.ebean.enhance.querybean;


import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.MethodVisitor;

/**
 * Describes the method that is potentially a getter (for Kotlin query beans).
 */
class MethodDesc {

  private final int access;
  private final String name;
  private final String desc;
  private final String signature;
  private final String[] exceptions;

  MethodDesc(int access, String name, String desc, String signature, String[] exceptions) {
    this.access = access;
    this.name = name;
    this.desc = desc;
    this.signature = signature;
    this.exceptions = exceptions;
  }

  /**
  * Return true if this is a getter for a property.
  */
  boolean isGetter() {
    return name.length() > 3
      && name.startsWith("get")
      && Character.isUpperCase(name.charAt(3))
      && desc.startsWith("()Lio/ebean/typequery/");
  }

  /**
  * Start the method visit.
  */
  MethodVisitor visitMethod(ClassVisitor cv) {
    return cv.visitMethod(access, name, desc, signature, exceptions);
  }

  /**
  * Return the method description.
  */
  String getDesc() {
    return desc;
  }

  /**
  * Return the name of the generated method the 'getter' is effectively a proxy for.
  */
  String proxyMethodName() {
    return "_" + Character.toLowerCase(name.charAt(3)) + name.substring(4);
  }
}
