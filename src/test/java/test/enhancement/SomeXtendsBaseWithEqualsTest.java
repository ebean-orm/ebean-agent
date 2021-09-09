package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.SomeXtendsBaseWithEquals;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 */
public class SomeXtendsBaseWithEqualsTest extends BaseTest {


  @Test
  public void testBasic() {

    SomeXtendsBaseWithEquals bean = new SomeXtendsBaseWithEquals();
    bean.equals(new SomeXtendsBaseWithEquals());

    assertEquals(1, bean.equalsCount);
  }
}
