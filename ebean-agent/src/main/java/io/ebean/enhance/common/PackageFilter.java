package io.ebean.enhance.common;

import java.util.ArrayList;
import java.util.List;

final class PackageFilter {

  private final String[] packagePrefixes;

  PackageFilter(String packages) {
    List<String> prefixes = new ArrayList<>();
    for (String pkg : packages.split(",")) {
      String replace = pkg.replace('.', '/').trim() + '/';
      if (!replace.isEmpty()) {
        prefixes.add(replace);
      }
    }
    packagePrefixes = prefixes.toArray(new String[]{});
  }

  boolean ignore(String fullClassName) {
    for (String packagePrefix : packagePrefixes) {
      if (fullClassName.startsWith(packagePrefix)) {
        return false;
      }
    }
    return true;
  }
}
