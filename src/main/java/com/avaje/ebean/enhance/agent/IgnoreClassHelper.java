package com.avaje.ebean.enhance.agent;

import java.util.HashMap;

/**
 * Helper object used to ignore known classes. We only want to enhance the
 * entity beans and perhaps some other user classes (not JDK classes etc).
 * <p>
 * In looking for classes to enhance we can skip some such as JDK, JDBC and
 * other known libraries.
 * </p>
 * <p>
 * The agentArgs can optionally include a "packages" parameter with a comma
 * delimited list of packages that SHOULD be processed. All other packages would
 * then be ignored.
 * </p>
 */
public class IgnoreClassHelper {

  private final String[] processPackages;

  public IgnoreClassHelper(String agentArgs) {

    HashMap<String, String> args = ArgParser.parse(agentArgs);
    String packages = args.get("packages");
    if (packages != null) {
      String[] pkgs = packages.split(",");
      processPackages = new String[pkgs.length];
      for (int i = 0; i < pkgs.length; i++) {
        processPackages[i] = convertPackage(pkgs[i]);
      }
    } else {
      processPackages = new String[0];
    }
  }

  /**
   * Convert dots/periods to slashes in the package name.
   */
  private String convertPackage(String pkg) {

    pkg = pkg.trim().replace('.', '/');

    if (pkg.endsWith("**")) {
      // wild card, remove the ** 
      return pkg.substring(0, pkg.length() - 2);

    } else if (pkg.endsWith("*")) {
      // wild card, remove the *
      return pkg.substring(0, pkg.length() - 1);

    } else if (pkg.endsWith("/")) {
      // already ends in "/"
      return pkg;

    } else {
      // add "/" so we don't pick up another
      // package with a similar starting name
      return pkg + "/";
    }
  }

  /**
   * Use specific positive matching to determine if the class needs to be
   * processed.
   * <p>
   * Any class at any depth under the package can be processed and all others
   * ignored.
   * </p>
   * 
   * @return true if the class can be ignored
   */
  private boolean specificMatching(String className) {

    for (int i = 0; i < processPackages.length; i++) {
      if (className.startsWith(processPackages[i])) {
        // a positive match
        return false;
      }
    }
    // we can ignore this class
    return true;
  }

  /**
   * Try to exclude JDK classes and known JDBC Drivers and Libraries.
   * <p>
   * We want to do this for performance reasons - that is skip checking for
   * enhancement on classes that we know are not part of the application code
   * and should not be enhanced.
   * </p>
   * 
   * @param className
   *          the className of the class being defined.
   * @return true if this class should not be processed.
   */
  public boolean isIgnoreClass(String className) {

    if (className == null) {
      return true;
    }
    
    className = className.replace('.', '/');

    // the special entity beans supplied by Ebean SHOULD be processed
    if (className.equals(EnhanceConstants.EBEAN_MODEL)) {
      return false;
    }

    if (processPackages.length > 0) {
      // use specific positive matching
      return specificMatching(className);
    }

    // we don't have specific packages to process so instead
    // we will ignore packages that we know we don't want to
    // process (they won't contain entity beans etc).

    // the rest of Ebean should be ignored
    if (className.startsWith(EnhanceConstants.EBEAN_PREFIX)) {
      return true;
    }
    // ignore the JDK classes ...
    if (className.startsWith("java/") || className.startsWith("javax/")) {
      return true;
    }
    if (className.startsWith("sun/") || className.startsWith("sunw/") || className.startsWith("com/sun/")) {
      return true;
    }
    if (className.startsWith("org/wc3/") || className.startsWith("org/xml/")) {
      return true;
    }
    if (className.startsWith("org/junit/") || className.startsWith("junit/")) {
      return true;
    }
    // ignore apache libraries
    if (className.startsWith("org/apache/") || className.startsWith("org/eclipse/")) {
      return true;
    }
    if (className.startsWith("com/fasterxml/jackson") || className.startsWith("org/joda/")) {
      return true;
    }
    // ignore various jdbc drivers
    if (className.startsWith("org/postgresql/") || className.startsWith("com/mysql/jdbc")
            || className.startsWith("org/h2/") || className.startsWith("oracle/")) {
      return true;
    }
    // ignore base groovy classes
    if (className.startsWith("groovy/")) {
      return true;
    }
    // ignore $Proxy classes
    if (className.startsWith("$")) {
      return true;
    }
    return false;
  }
}
