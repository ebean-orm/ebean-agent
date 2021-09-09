package test.enhancement;

import io.ebean.bean.EntityBean;
import org.junit.jupiter.api.Test;
import test.model.SomeBeanWithInterface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 */
public class SomeInterfaceTest extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    SomeBeanWithInterface bean = new SomeBeanWithInterface();
    assertNotNull(bean);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    assertEquals(2, props.length);
    assertEquals("id", props[0]);
    assertEquals("code", props[1]);
  }
}
