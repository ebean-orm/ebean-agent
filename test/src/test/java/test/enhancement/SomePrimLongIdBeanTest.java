package test.enhancement;

import org.testng.annotations.Test;
import test.model.SomePrimLongIdBean;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SomePrimLongIdBeanTest extends BaseTest {

  @Test
  public void test_equals_whenIdSetAfter() {

    SomePrimLongIdBean b0 = new SomePrimLongIdBean();
    SomePrimLongIdBean b1 = new SomePrimLongIdBean();

    assertFalse(b0.equals(b1));

    b1.setId(42);
    b0.setId(42);

    // still not equals
    assertFalse(b0.equals(b1));
  }

  @Test
  public void test_equals_whenIdSetFirst() {

    SomePrimLongIdBean b0 = new SomePrimLongIdBean();
    SomePrimLongIdBean b1 = new SomePrimLongIdBean();
    b1.setId(42);
    b0.setId(42);

    assertTrue(b0.equals(b1));
  }
}
