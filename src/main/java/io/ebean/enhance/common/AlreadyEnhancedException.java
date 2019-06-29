package io.ebean.enhance.common;

/**
 * Exception thrown during enhancement when it is detected that the enhancement has already taken place.
 * <p>
 * This is expected when off line enhancement (via ant task) is used.
 * </p>
 */
public class AlreadyEnhancedException extends RuntimeException {

  private static final long serialVersionUID = -831705721822834774L;

  private final String className;

  public AlreadyEnhancedException(String className) {
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

}
