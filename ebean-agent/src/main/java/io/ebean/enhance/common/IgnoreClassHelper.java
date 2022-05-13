package io.ebean.enhance.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper object used to ignore known classes. We only want to enhance the
 * entity beans and perhaps some other user classes (not JDK classes etc).
 * <p>
 * In looking for classes to enhance we can skip some such as JDK, JDBC and
 * other known libraries.
 * </p>
 */
class IgnoreClassHelper {

  private static final Set<String> ignoreOneLevel = new HashSet<>();
  private static final Set<String> ignoreTwoLevel = new HashSet<>();
  private static final Set<String> ignoreThreeLevel = new HashSet<>();

  static {
    ignoreOneLevel.add("jdk");
    ignoreOneLevel.add("java");
    ignoreOneLevel.add("javax");
    ignoreOneLevel.add("jakarta");
    ignoreOneLevel.add("sbt");
    ignoreOneLevel.add("scala");
    ignoreOneLevel.add("sun");
    ignoreOneLevel.add("sunw");
    ignoreOneLevel.add("oracle");
    ignoreOneLevel.add("groovy");
    ignoreOneLevel.add("kotlin");
    ignoreOneLevel.add("junit");
    ignoreOneLevel.add("microsoft");

    ignoreTwoLevel.add("org/aopalliance");
    ignoreTwoLevel.add("org/jcp");
    ignoreTwoLevel.add("org/omg");
    ignoreTwoLevel.add("org/wc3");
    ignoreTwoLevel.add("org/xml");
    ignoreTwoLevel.add("org/junit");
    ignoreTwoLevel.add("org/apache");
    ignoreTwoLevel.add("org/eclipse");
    ignoreTwoLevel.add("org/joda");
    ignoreTwoLevel.add("org/graalvm");
    ignoreTwoLevel.add("org/postgresql");
    ignoreTwoLevel.add("org/h2");
    ignoreTwoLevel.add("org/hsqldb");
    ignoreTwoLevel.add("org/ibex");
    ignoreTwoLevel.add("org/sqlite");
    ignoreTwoLevel.add("ch/qos");
    ignoreTwoLevel.add("org/slf4j");
    ignoreTwoLevel.add("org/codehaus");
    ignoreTwoLevel.add("org/assertj");
    ignoreTwoLevel.add("org/hamcrest");
    ignoreTwoLevel.add("org/mockito");
    ignoreTwoLevel.add("org/objenesis");
    ignoreTwoLevel.add("org/objectweb");
    ignoreTwoLevel.add("org/jboss");
    ignoreTwoLevel.add("org/testng");
    ignoreTwoLevel.add("org/springframework");
    ignoreTwoLevel.add("com/sun");
    ignoreTwoLevel.add("com/mysql");
    ignoreTwoLevel.add("com/h2database");
    ignoreTwoLevel.add("com/fasterxml");
    ignoreTwoLevel.add("com/intellij");
    ignoreTwoLevel.add("com/jprofiler");
    ignoreTwoLevel.add("com/google");
    ignoreTwoLevel.add("com/squareup");
    ignoreTwoLevel.add("com/microsoft");
    ignoreTwoLevel.add("com/oracle");
    ignoreTwoLevel.add("io/ebean");
    ignoreTwoLevel.add("io/ebeaninternal");
    ignoreTwoLevel.add("io/ebeanservice");
    ignoreTwoLevel.add("io/opentelemetry");

    ignoreThreeLevel.add("io/avaje/config");
    ignoreThreeLevel.add("io/avaje/classpath");
    ignoreThreeLevel.add("io/avaje/http");
    ignoreThreeLevel.add("io/avaje/inject");
    ignoreThreeLevel.add("io/avaje/jex");
  }

  IgnoreClassHelper() {
  }

  /**
   * Try to exclude JDK classes and known JDBC Drivers and Libraries.
   * <p>
   * We want to do this for performance reasons - that is skip checking for
   * enhancement on classes that we know are not part of the application code
   * and should not be enhanced.
   * </p>
   *
   * @param className the className of the class being defined.
   * @return true if this class should not be processed.
   */
  boolean isIgnoreClass(String className) {
    if (className == null || "bsh/Interpreter".equals(className)) {
      return true;
    }
    className = className.replace('.', '/');

    // we will ignore packages that we know we don't want to
    // process (they won't contain entity beans etc).
    if (className.startsWith("$")) {
      // ignore $Proxy classes
      return true;
    }
    int firstSlash = className.indexOf('/');
    if (firstSlash == -1) {
      return true;
    }
    String firstPackage = className.substring(0, firstSlash);
    if (ignoreOneLevel.contains(firstPackage)) {
      return true;
    }
    int secondSlash = className.indexOf('/', firstSlash + 1);
    if (secondSlash > -1) {
      String secondPackage = className.substring(0, secondSlash);
      if (ignoreTwoLevel.contains(secondPackage)) {
        return true;
      }
      int thirdSlash = className.indexOf('/', secondSlash + 1);
      if (thirdSlash > -1) {
        String thirdPackage = className.substring(0, thirdSlash);
        if (ignoreThreeLevel.contains(thirdPackage)) {
          return true;
        }
      }
    }
    return className.contains("$ByteBuddy$");
  }
}
