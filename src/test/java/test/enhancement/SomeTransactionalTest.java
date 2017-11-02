package test.enhancement;

import io.ebean.annotation.SomePath;
import org.testng.annotations.Test;
import test.model.SomeTransactionalService;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class SomeTransactionalTest extends BaseTest {


  @Test(enabled = false)
  public void test() throws NoSuchMethodException {

    SomeTransactionalService someService = new SomeTransactionalService();

    System.out.println("--- calling someMethod() ... ");
    someService.someMethod("Jimmy");

    assertNotNull(someService.getGeneratedKeys);
    assertFalse(someService.getGeneratedKeys);

    Class<? extends SomeTransactionalService> aClass = someService.getClass();
    Method method = aClass.getMethod("someMethod", String.class);

    SomePath pathAnnotation = method.getAnnotation(SomePath.class);

    assertEquals("HelloWorld", pathAnnotation.name());

    assertEquals(42, someService.someInt());
    assertEquals(42.0, someService.someDouble());
    assertEquals("FooBar", someService.someObject());

  }
}
