package io.ebean.enhance.entity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class loader is used to load any classes (typically super classes)
 * during enhancement process (avoiding the application class loaders).
 */
public class LocalClassLoader extends URLClassLoader {

  public LocalClassLoader(URL[] urls, ClassLoader loader) {
    super(urls, loader);
  }

  @Override
  protected synchronized Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {

    if (name.startsWith("java.")) {
      // we cannot reload these classes due to security constraints
      return super.loadClass(name, resolve);
    }
    Class<?> c = super.findLoadedClass(name);
    if (c != null) {
      return c;
    }
    String resource = name.replace('.', '/') + ".class";


    try {
      // read the class bytes, and define the class
      URL url = super.getResource(resource);
      if (url == null) {
        throw new ClassNotFoundException(name);
      }

      File f = new File("build/bin/"+resource);
      System.out.println("FileLen:"+f.length()+"  "+f.getName());

      try (InputStream is = url.openStream()) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
          byte[] b = new byte[2048];

          int count;
          while ((count = is.read(b, 0, 2048)) != -1) {
            os.write(b, 0, count);
          }
          byte[] bytes = os.toByteArray();

          System.err.println("bytes: "+bytes.length+" "+resource);
          return defineClass(name, bytes, 0, bytes.length);
        }
      }
    } catch (SecurityException e) {
      return super.loadClass(name, resolve);
    } catch (IOException e) {
      throw new ClassNotFoundException(name, e);
    }
  }

}
