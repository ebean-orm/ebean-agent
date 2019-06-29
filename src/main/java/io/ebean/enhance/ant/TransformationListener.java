package io.ebean.enhance.ant;

/**
 * Mechanism for caller (ANT Task, Maven plugin, Hudson plugin, etc.) to
 * report events back to the user interface.
 *
 * @author Paul Mendelson
 */
public interface TransformationListener {

  /**
   * Report normal processing event
   */
  void logEvent(String msg);

  /**
   * Report processing "error"
   */
  void logError(String msg);

}
