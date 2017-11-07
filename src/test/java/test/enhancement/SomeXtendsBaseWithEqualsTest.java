package test.enhancement;

import org.testng.annotations.Test;
import test.model.SomeXtendsBaseWithEquals;

import static org.testng.Assert.assertEquals;

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
