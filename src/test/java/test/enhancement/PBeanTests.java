package test.enhancement;

import com.avaje.ebean.bean.EntityBean;
import org.testng.annotations.Test;
import test.model.PBean;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 */
public class PBeanTests extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    PBean bean = new PBean();
    assertNotNull(bean);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    assertEquals(2, props.length);
    assertEquals("id", props[0]);
    assertEquals("name", props[1]);

  }
}
