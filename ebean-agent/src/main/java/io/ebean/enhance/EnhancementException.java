package io.ebean.enhance;

/**
 * Some unexpected bytecode that can't be supported.
 * <p>
 * For example, some unsupported OneToMany collection initialisation.
 */
public class EnhancementException extends RuntimeException {
  public EnhancementException(String message) {
    super(message);
  }
}
