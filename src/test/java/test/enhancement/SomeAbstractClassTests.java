package test.enhancement;

import com.avaje.ebean.bean.EntityBean;
import org.junit.Assert;
import org.junit.Test;
import test.model.SomeAbstractClass;
import test.model.SomeBeanWithInterface;
import test.model.SomeExtendsAbstract;
import test.model.SomeInterface;

public class SomeAbstractClassTests extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    SomeExtendsAbstract bean = new SomeExtendsAbstract();
    Assert.assertNotNull(bean);

    Assert.assertTrue(bean instanceof EntityBean);
    Assert.assertTrue(bean instanceof SomeInterface);
    Assert.assertTrue(bean instanceof SomeAbstractClass);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    Assert.assertEquals(2, props.length);
    Assert.assertEquals("id", props[0]);
    Assert.assertEquals("name", props[1]);
  }
}
