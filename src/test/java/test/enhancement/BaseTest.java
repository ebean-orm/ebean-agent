package test.enhancement;

import io.ebean.enhance.ant.MainTransform;

public abstract class BaseTest {

  static String[] transformArgs = {"target/test-classes", "test/model", "debug=2"};

  static {
    MainTransform.main(transformArgs);
  }

}
