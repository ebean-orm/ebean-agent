package test.enhancement;

import io.ebean.enhance.ant.MainTransform;

public abstract class BaseTest {

  static String[] transformArgs = {"target/test-classes", "test, de/foconis", "debug=1"};

  static {
    MainTransform.main(transformArgs);
  }

}
