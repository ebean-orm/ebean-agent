package io.ebean.enhance.common;

/**
 * When classpath issue means a common superclass can not be determined
 * this captures the types involved.
 */
public class CommonSuperUnresolved {

  private final String type1;
  private final String type2;
  private final String error;

  public CommonSuperUnresolved(String type1, String type2, String error) {
    this.type1 = type1;
    this.type2 = type2;
    this.error = error;
  }

  @Override
  public String toString() {
    return error;
  }

  public String getType1() {
    return type1;
  }

  public String getType2() {
    return type2;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return "defaulted common supertype for type1:" + type1 + " type2:" + type2 + " due to err:" + error;
  }
}
