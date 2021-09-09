package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.SomeTransactionalWithStatic;

public class SomeTransactionalWithStaticTest extends BaseTest {

  @Test
  public void testSomeMethod() {

    SomeTransactionalWithStatic some = new SomeTransactionalWithStatic();
    some.someMethod("junk");
  }

}
