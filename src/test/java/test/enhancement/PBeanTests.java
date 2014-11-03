package test.enhancement;

import com.avaje.ebean.bean.EntityBean;
import org.junit.Assert;
import org.junit.Test;
import test.model.PBean;
import test.model.SomeXtendsBaseWithEquals;

/**
 */
public class PBeanTests extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    PBean bean = new PBean();
    Assert.assertNotNull(bean);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    Assert.assertEquals(2, props.length);
    Assert.assertEquals("id", props[0]);
    Assert.assertEquals("name", props[1]);

  }
}
