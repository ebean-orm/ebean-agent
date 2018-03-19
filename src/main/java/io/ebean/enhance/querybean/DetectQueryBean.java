package io.ebean.enhance.querybean;

import java.util.Arrays;

/**
 * Detects if a class is a query bean.
 * <p>
 * Used by enhancement to detect when GETFIELD access on query beans should be replaced by
 * appropriate method calls.
 * </p>
 */
public class DetectQueryBean {

  private final String[] queryBeanPackages;

  DetectQueryBean(String[] queryBeanPackages) {
    this.queryBeanPackages = queryBeanPackages;
  }

  public String toString() {
    return Arrays.toString(queryBeanPackages);
  }

  /**
   * Return true if there are no known packages.
   */
  public boolean isEmpty() {
    return queryBeanPackages.length == 0;
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
    for (String aPackage : queryBeanPackages) {
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
