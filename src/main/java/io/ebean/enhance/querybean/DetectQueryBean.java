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

  private final String[] entityPackages;

  DetectQueryBean(String[] entityPackages) {
    this.entityPackages = entityPackages;
  }

  public String toString() {
    return Arrays.toString(entityPackages);
  }

  /**
   * Return true if there are no known packages.
   */
  public boolean isEmpty() {
    return entityPackages.length == 0;
  }

  /**
   * Return the packages that entity beans are expected.
   * Query beans are expected to be in a query sub-package.
   */
  String[] getEntityPackages() {
    return entityPackages;
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
