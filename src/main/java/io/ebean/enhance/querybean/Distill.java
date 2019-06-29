package io.ebean.enhance.querybean;

import java.util.Collection;

/**
 * Helper for parsing, merging and converting packages.
 */
public class Distill {

  /**
   * Convert the dot notation entity bean packages to slash notation.
   *
   * @param packages entity bean packages
   */
  public static DetectQueryBean convert(Collection<String> packages) {

    String[] asArray = packages.toArray(new String[0]);
    for (int i = 0; i < asArray.length; i++) {
      asArray[i] = convert(asArray[i]);
    }
    return new DetectQueryBean(asArray);
  }

  /**
   * Convert package to slash notation taking into account trailing wildcard.
   */
  private static String convert(String pkg) {

    pkg = pkg.trim();
    if (pkg.endsWith("*")) {
      pkg = pkg.substring(0, pkg.length() - 1);
    }
    if (pkg.endsWith(".query")) {
      // always work with entity bean packages so trim
      pkg = pkg.substring(0, pkg.length() - 6);
    }
    pkg = pkg.replace('.', '/');
    return pkg.endsWith("/") ? pkg : pkg + "/";
  }
}
