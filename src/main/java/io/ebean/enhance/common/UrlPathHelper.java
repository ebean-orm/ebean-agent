package io.ebean.enhance.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Helper methods for URL class path conversion.
 */
public class UrlPathHelper {

  private static final String PROTOCAL_PREFIX = "file:";

  /**
  * Convert string paths into URL class paths.
  */
  public static URL[] convertToUrl(String[] paths) {
    ArrayList<URL> list = new ArrayList<URL>();
    for (int i = 0; i < paths.length; i++) {
      URL url = convertToUrl(paths[i]);
      if (url != null) {
        list.add(url);
      }
    }
    return list.toArray(new URL[list.size()]);
  }

  /**
  * Convert string path into URL class path.
  */
  public static URL convertToUrl(String path) {
    if (isEmpty(path)) {
      return null;
    }
    try {
      return new URL(PROTOCAL_PREFIX + convertUrlString(path));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
  * Convert a string path to be used in URL class path entry.
  */
  public static String convertUrlString(String classpath) {

    if (isEmpty(classpath)) {
      return "";
    }

    classpath = classpath.trim();
    if (classpath.length() < 2) {
      return "";
    }
    if (classpath.charAt(0) != '/' && classpath.charAt(1) == ':') {
      // add leading slash for windows platform
      // assuming drive letter path
      classpath = "/" + classpath;
    }
    if (!classpath.endsWith("/")) {
      File file = new File(classpath);
      if (file.exists() && file.isDirectory()) {
        classpath = classpath.concat("/");
      }
    }
    return classpath;
  }

  private static boolean isEmpty(String s) {
    return s == null || s.trim().length() == 0;
  }
}
