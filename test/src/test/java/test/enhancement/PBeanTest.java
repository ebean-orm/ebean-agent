package test.enhancement;

import io.ebean.bean.EntityBean;
import org.testng.annotations.Test;
import test.model.PBean;

import static org.testng.Assert.*;

/**
 */
public class PBeanTest extends BaseTest {

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

  @Test
  public void testEquals_whenNotSet() {

    PBean b0 = new PBean();
    PBean b1 = new PBean();

    assertFalse(b0.equals(b1));

    b0.setId(42L);
    b1.setId(42L);
    // still false, hashCode/equals consistent
    assertFalse(b0.equals(b1));
  }

  @Test
  public void testEquals_whenSet() {

    PBean b0 = new PBean();
    PBean b1 = new PBean();
    b0.setId(42L);
    b1.setId(42L);

    assertTrue(b0.equals(b1));
  }

}
