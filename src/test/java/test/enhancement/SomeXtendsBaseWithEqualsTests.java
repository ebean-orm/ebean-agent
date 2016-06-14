package test.enhancement;

import org.testng.Assert;
import org.testng.annotations.Test;
import test.enhancement.BaseTest;
import test.model.SomeXtendsBaseWithEquals;

/**
 */
public class SomeXtendsBaseWithEqualsTests extends BaseTest {


  @Test
  public void testBasic() {

    SomeXtendsBaseWithEquals bean = new SomeXtendsBaseWithEquals();
    bean.equals(new SomeXtendsBaseWithEquals());

    Assert.assertEquals(1, bean.equalsCount);
  }
}
