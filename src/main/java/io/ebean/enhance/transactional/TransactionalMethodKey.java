package io.ebean.enhance.transactional;

/**
 * Holds profileId and details for a given @Transactional method.
 */
public class TransactionalMethodKey {

  private final String className;
  private final String methodName;
  private final String methodDesc;
  private int profileId;
  private int lineNumber;

  public TransactionalMethodKey(String className, String methodName, String methodDesc) {
    this.className = className;
    this.methodName = methodName;
    this.methodDesc = methodDesc;
  }

  @Override
  public String toString() {
    return "profileId:" + profileId + " method:" + className + "." + methodName + methodDesc + ":" + lineNumber;
  }

  int getProfileId() {
    return profileId;
  }

  public void setProfileId(int profileId) {
    this.profileId = profileId;
  }

  void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  int getLineNumber() {
    return lineNumber;
  }

  public String getMethodName() {
    return methodName;
  }
}
