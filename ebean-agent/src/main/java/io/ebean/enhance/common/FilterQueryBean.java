package io.ebean.enhance.common;

import java.util.Arrays;

/**
 * Filters classes for query bean enhancement.
 */
class FilterQueryBean {

  private final boolean ignoreAll;
  private final boolean detectOnAll;
  private final String[] topLevelPackages;

  FilterQueryBean(AgentManifest manifest) {
    // if no packages for either then run detection on everything
    ignoreAll = manifest.isQueryBeanNone();
    // if no query beans packages we need to run detection on everything
    detectOnAll = manifest.querybeanPackages().isEmpty();
    DistillPackages distill = new DistillPackages().add(manifest.entityPackages());
    if (!manifest.isQueryBeanNone()) {
      distill.add(manifest.querybeanPackages());
    }
    this.topLevelPackages = distill.distill();
  }

  @Override
  public String toString() {
    return "ignoreAll:" + ignoreAll + " detectOnAll:" + detectOnAll + " topLevelPackages:" + Arrays.toString(topLevelPackages);
  }

  /**
  * Return true if the enhancement/detection should be performed on this class.
  */
  boolean detectEnhancement(String className) {
    if (ignoreAll) {
      return false;
    }
    if (detectOnAll) {
      return true;
    }
    for (String pkg :topLevelPackages) {
      if (className.startsWith(pkg)) {
        return true;
      }
    }
    return false;
  }
}
