package io.ebean.enhance.querybean;

/**
 * Reads the raw bytes for a given className.
 * <p>
 * Implement to suit the environment (e.g. Idea IDE specific)
 * </p>
 */
public interface ClassBytesReader {

  /**
   * Return the raw class bytes given the className and classLoader.
   */
  byte[] getClassBytes(String className, ClassLoader classLoader);
}
