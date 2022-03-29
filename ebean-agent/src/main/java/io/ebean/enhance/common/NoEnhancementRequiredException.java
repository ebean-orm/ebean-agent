package io.ebean.enhance.common;

/**
 * Thrown when a Class does not require entity or transaction enhancement.
 */
public class NoEnhancementRequiredException extends RuntimeException {

  private static final long serialVersionUID = 7222178323991228946L;

  public NoEnhancementRequiredException() {
    super();
  }

  public NoEnhancementRequiredException(String msg) {
    super(msg);
  }
}
