package test.enhancement;

import com.avaje.ebean.bean.EntityBean;
import org.junit.Assert;
import org.junit.Test;
import test.model.Contact;
import test.model.WithInitialisedCollections;

import java.util.List;

public class WithInitialisedCollectionsTests extends BaseTest {


  @Test
  public void test() {

    WithInitialisedCollections bean = new WithInitialisedCollections();
    Assert.assertNotNull(bean);

    Assert.assertTrue(bean instanceof EntityBean);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();

    Assert.assertEquals(9, props.length);
    Assert.assertEquals("contacts", props[5]);
    Assert.assertEquals("myset", props[6]);
    Assert.assertEquals("myLinkedSet", props[7]);
    Assert.assertEquals("strings", props[8]);

    Object val5 = eb._ebean_getField(5);
    Object val6 = eb._ebean_getField(6);
    Object val7 = eb._ebean_getField(7);
    Object val8 = eb._ebean_getField(8);
    Assert.assertNotNull(val8);
    Assert.assertNull(val5);
    Assert.assertNull(val6);
    Assert.assertNull(val7);

    List<Contact> contacts = bean.getContacts();
    Assert.assertNotNull(contacts);

    Assert.assertNotNull(bean.getMyset());
    Assert.assertNotNull(bean.getMyLinkedSet());

  }
}
