package com.avaje.ebean.enhance.agent;

import com.avaje.ebean.enhance.asm.ClassWriter;

/**
 * ClassWriter that uses a specific ClassLoader.
 * <p>
 * The ClassLoader is used specifically to assist the inherited getCommonSuperClass() method.
 */
public class CLAwareClassWriter extends ClassWriter {

  protected final ClassLoader classLoader;

  /**
   * Construct with flags and a ClassLoader which is used for supporting the getCommonSuperClass() method.
   */
  public CLAwareClassWriter(int flags, ClassLoader classLoader) {
    super(flags);
    this.classLoader = classLoader;
  }

  /**
   * Return the class using the supplied ClassLoader.
   */
  @Override
  protected Class<?> classForName(String name) throws ClassNotFoundException {
    try {
      return Class.forName(name, false, classLoader);
    } catch (Throwable e) {
      // ignore and just use Class.forName original behavior
      return super.classForName(name);
    }
  }

}
