package test.enhancement;

import org.testng.annotations.Test;
import test.model.SomeTransactionalWithStatic;

public class SomeTransactionalWithStaticTest extends BaseTest {

  @Test
  public void testSomeMethod() {

    SomeTransactionalWithStatic some = new SomeTransactionalWithStatic();
    some.someMethod("junk");
  }

}
