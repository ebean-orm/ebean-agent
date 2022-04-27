package io.ebean.enhance.querybean;

import java.util.HashSet;
import java.util.Set;

/**
 * Detects if a class is a query bean.
 * <p>
 * Used by enhancement to detect when GETFIELD access on query beans should be replaced by
 * appropriate method calls.
 * </p>
 */
public class DetectQueryBean {

  private final Set<String> entityPackages = new HashSet<>();

  public DetectQueryBean() {
  }

  public void addAll(Set<String> rawEntityPackages) {
    for (String rawEntityPackage : rawEntityPackages) {
      entityPackages.add(convert(rawEntityPackage));
    }
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


  @Override
  public String toString() {
    return entityPackages.toString();
  }

  /**
   * Return true if there are no known packages.
   */
  public boolean isEmpty() {
    return entityPackages.isEmpty();
  }

  /**
   * Return true if this class is a query bean using naming conventions for query beans.
   */
  public boolean isQueryBean(String owner) {
    int subPackagePos = owner.lastIndexOf("/query/");
    if (subPackagePos > -1) {
      String suffix = owner.substring(subPackagePos);
      if (isQueryBeanSuffix(suffix)) {
        String domainPackage = owner.substring(0, subPackagePos + 1);
        return isQueryBeanPackage(domainPackage);
      }
    }
    return false;
  }

  /**
   * Check that the class is in an expected package (sub package of a package containing entity beans).
   */
  private boolean isQueryBeanPackage(String domainPackage) {
    for (String aPackage : entityPackages) {
      if (domainPackage.startsWith(aPackage)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check that the class follows query bean naming convention.
   */
  private boolean isQueryBeanSuffix(String suffix) {
    return (suffix.startsWith("/query/Q") || suffix.startsWith("/query/assoc/Q"));
  }
}
