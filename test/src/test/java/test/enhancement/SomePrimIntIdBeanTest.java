package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.SomePrimIntIdBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SomePrimIntIdBeanTest extends BaseTest {

  @Test
  public void test_equals_whenIdSetAfter() {

    SomePrimIntIdBean b0 = new SomePrimIntIdBean();
    SomePrimIntIdBean b1 = new SomePrimIntIdBean();

    assertFalse(b0.equals(b1));

    b1.setId(42);
    b0.setId(42);

    // still not equals
    assertFalse(b0.equals(b1));
  }

  @Test
  public void test_equals_whenIdSetFirst() {

    SomePrimIntIdBean b0 = new SomePrimIntIdBean();
    SomePrimIntIdBean b1 = new SomePrimIntIdBean();
    b1.setId(42);
    b0.setId(42);

    assertTrue(b0.equals(b1));
  }
}
