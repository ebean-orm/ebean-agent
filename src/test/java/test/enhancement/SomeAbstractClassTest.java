package test.enhancement;

import io.ebean.bean.EntityBean;
import org.junit.jupiter.api.Test;
import test.model.SomeAbstractClass;
import test.model.SomeExtendsAbstract;
import test.model.SomeInterface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SomeAbstractClassTest extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    SomeExtendsAbstract bean = new SomeExtendsAbstract();
    assertNotNull(bean);

    assertTrue(bean instanceof EntityBean);
    assertTrue(bean instanceof SomeInterface);
    assertTrue(bean instanceof SomeAbstractClass);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    assertEquals(2, props.length);
    assertEquals("id", props[0]);
    assertEquals("name", props[1]);
  }
}
