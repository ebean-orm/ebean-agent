package test.enhancement;

import com.avaje.ebean.bean.EntityBean;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.model.SomeBeanWithInterface;
import test.model.SomeInterface;

/**
 */
public class SomeInterfaceTests extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    SomeBeanWithInterface bean = new SomeBeanWithInterface();
    Assert.assertNotNull(bean);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    Assert.assertEquals(2, props.length);
    Assert.assertEquals("id", props[0]);
    Assert.assertEquals("code", props[1]);

    Assert.assertTrue(bean instanceof SomeInterface);

  }
}
