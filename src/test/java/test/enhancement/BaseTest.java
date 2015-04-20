package test.enhancement;

import com.avaje.ebean.enhance.ant.MainTransform;

import java.io.IOException;

public abstract class BaseTest {

  static String[] transformArgs = { "target/test-classes", "test/model/**", "debug=9;transactional=true" };

  static {
//    try {
      MainTransform.main(transformArgs);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
  }

}
