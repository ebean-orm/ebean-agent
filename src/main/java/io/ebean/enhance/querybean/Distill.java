package io.ebean.enhance.querybean;

import java.util.Collection;

/**
 * Helper for parsing, merging and converting packages.
 */
public class Distill {

  /**
   * Split using delimiter and convert to slash notation.
   */
  static String[] parsePackages(String packages) {

    if (packages == null || packages.trim().length() == 0) {
      return new String[0];
    }
    String[] commaSplit = packages.split(",");
    String[] processPackages = new String[commaSplit.length];
    for (int i = 0; i < commaSplit.length; i++) {
      processPackages[i] = convert(commaSplit[i]);
    }
    return processPackages;
  }

  /**
   * Merge the packages to include in the enhancement (all other packages will now be ignored).
   */
  static String[] mergePackages(String[] packages1, String[] packages2) {
    String[] all = new String[packages1.length + packages2.length];
    System.arraycopy(packages1, 0, all, 0, packages1.length);
    System.arraycopy(packages2, 0, all, packages1.length, packages2.length);
    return all;
  }

  /**
   * Convert the dot notation entity bean packages to slash notation.
   *
   * @param packages entity bean packages
   */
  public static DetectQueryBean convert(Collection<String> packages) {

    String[] asArray = packages.toArray(new String[packages.size()]);
    for (int i = 0; i < asArray.length; i++) {
      asArray[i] = convert(asArray[i]);
    }
    return new DetectQueryBean(asArray);
  }

  /**
   * Concert package to slash notation taking into account trailing wildcard.
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
