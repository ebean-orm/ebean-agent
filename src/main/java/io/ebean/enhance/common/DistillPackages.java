package io.ebean.enhance.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Distill packages into distinct top level packages for searching.
 */
class DistillPackages {

  private final TreeSet<String> treeSet = new TreeSet<>();

  /**
  * Add packages that we want to distill.
  */
  DistillPackages add(Collection<String> packages) {
    if (packages != null) {
      treeSet.addAll(packages);
    }
    return this;
  }

  /**
  * Add a raw entry splitting it into individual packages by delimiters.
  */
  DistillPackages addRaw(String packages) {
    if (packages != null) {
      for (String s : packages.split(",|;| ")) {
        String pkg = s.trim();
        if (!pkg.isEmpty()) {
          treeSet.add(pkg);
        }
      }
    }
    return this;
  }

  /**
  * Return the top level packages (with trailing slash) as an Array.
  */
  String[] distill() {
    return convertToArray(deriveTopLevel());
  }

  /**
  * Distill the list of packages into distinct top level packages.
  */
  private List<String> deriveTopLevel() {

    List<String> distilled = new ArrayList<>();

    // build the distilled list
    for (String pack : treeSet) {
      if (notAlreadyContained(distilled, pack)) {
        distilled.add(pack);
      }
    }

    return distilled;
  }

  /**
  * Convert the dot notation entity bean packages to slash notation.
  *
  * @param packages entity bean packages
  */
  private String[] convertToArray(Collection<String> packages) {

    String[] asArray = packages.toArray(new String[0]);
    for (int i = 0; i < asArray.length; i++) {
      asArray[i] = convert(asArray[i]);
    }
    return asArray;
  }

  /**
  * Convert package to slash notation taking into account trailing wildcard.
  */
  private String convert(String pkg) {

    pkg = pkg.trim();
    if (pkg.endsWith("*")) {
      pkg = pkg.substring(0, pkg.length() - 1);
    }
    pkg = pkg.replace('.', '/');
    return pkg.endsWith("/") ? pkg : pkg + "/";
  }

  /**
  * Return true if the package is not already contained in the distilled list.
  */
  private boolean notAlreadyContained(List<String> distilled, String pack) {

    for (String aDistilled : distilled) {
      if (pack.startsWith(aDistilled)) {
        return false;
      }
    }
    return true;
  }
}
