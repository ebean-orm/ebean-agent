package test.enhancement;

import com.avaje.ebean.annotation.SomePath;
import org.junit.Test;
import test.model.SomeTransactionalService;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class SomeTransactionalTest extends BaseTest {

  @Test
  public void test() throws NoSuchMethodException {

    SomeTransactionalService someService = new SomeTransactionalService();

    System.out.println("--- calling someMethod() ... ");
    someService.someMethod("Jimmy");

    Class<? extends SomeTransactionalService> aClass = someService.getClass();
    Method method = aClass.getMethod("someMethod", String.class);

    SomePath pathAnnotation = method.getAnnotation(SomePath.class);

    assertEquals("HelloWorld", pathAnnotation.name());
  }
}
