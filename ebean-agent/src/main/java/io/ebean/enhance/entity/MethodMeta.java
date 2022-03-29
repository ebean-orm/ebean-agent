package io.ebean.enhance.entity;

import io.ebean.enhance.common.AnnotationInfo;

public class MethodMeta {

  private final String name;
  private final String desc;

  private final AnnotationInfo annotationInfo;

  public MethodMeta(AnnotationInfo classAnnotationInfo, String name, String desc) {
    this.annotationInfo = new AnnotationInfo(classAnnotationInfo);
    this.name = name;
    this.desc = desc;
  }

  @Override
  public String toString() {
    return name + " " + desc;
  }

  public boolean isMatch(String methodName, String methodDesc) {
    return name.equals(methodName) && desc.equals(methodDesc);
  }

  public AnnotationInfo getAnnotationInfo() {
    return annotationInfo;
  }

}
