package test.enhancement;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.model.BaseEntity;
import test.model.Customer;

import java.lang.reflect.Field;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CustomerEntityTest extends BaseTest {

  private static final int PROPERTY_COUNT = 6;


  @Test
  public void testBasic() {

    Field field;
    try {
      field = BaseEntity.class.getField("_ebean_props");
      String[] names = (String[]) field.get(null);

      assertNotNull(names);
      assertEquals(3, names.length);
      assertEquals("id", names[0]);
      assertEquals("version", names[1]);
      assertEquals("one", names[2]);

    } catch (Exception e) {
      Assert.fail();
    }


    Customer customer = new Customer();
    Assert.assertTrue(customer instanceof EntityBean);

    EntityBean custEb = (EntityBean)customer;

    String[] custFieldNames = custEb._ebean_getPropertyNames();

    assertEquals(PROPERTY_COUNT, custFieldNames.length);

    assertEquals("id", custFieldNames[0]);
    assertEquals("version", custFieldNames[1]);
    assertEquals("one", custFieldNames[2]);
    assertEquals("name", custFieldNames[3]);
    assertEquals("whenStart", custFieldNames[4]);
    assertEquals("contacts", custFieldNames[5]);

    assertEquals("id", custEb._ebean_getPropertyName(0));
    assertEquals("version", custEb._ebean_getPropertyName(1));
    assertEquals("one", custEb._ebean_getPropertyName(2));
    assertEquals("name", custEb._ebean_getPropertyName(3));
    assertEquals("whenStart", custEb._ebean_getPropertyName(4));
    assertEquals("contacts", custEb._ebean_getPropertyName(5));

    EntityBeanIntercept customerIntercept = custEb._ebean_getIntercept();

    customer.setId(23l);

    // this is null as the bean has not been marked as loaded yet
    Assert.assertNull(customerIntercept.getChanged());
    boolean[] loaded = customerIntercept.getLoaded();
    assertNotNull(loaded);
    assertEquals(PROPERTY_COUNT, loaded.length);
    assertEquals(true, loaded[0]);
    assertEquals(false, loaded[1]);
    assertLoaded(customerIntercept, 0);
    assertNotLoaded(customerIntercept, 1,2,3,4);


    customer.setVersion(1l);
    assertLoaded(customerIntercept, 0, 1);
    assertNotLoaded(customerIntercept, 2,3,4);

    customer.setName("hello");
    assertLoaded(customerIntercept, 0, 1, 3);
    assertNotLoaded(customerIntercept, 2, 4);

    Assert.assertNull(customerIntercept.getChanged());
    Assert.assertTrue(customer.hashCode() > 0);

    Assert.assertTrue(customerIntercept.isNew());
    Assert.assertFalse(customerIntercept.isDirty());
    customerIntercept.setLoaded();

    // Will not change (set dirty flag)
    String otherName = "h"+"ello";
    customer.setName(otherName);
    Assert.assertNull(customerIntercept.getChanged());

    // This will set it to be modified
    customer.setName("nameModified");
    Assert.assertTrue(customerIntercept.isDirty());
    Assert.assertTrue(customerIntercept.isNewOrDirty());
    assertNotNull(customerIntercept.getChanged());
    assertChanged(customerIntercept, 3);
    assertNotChanged(customerIntercept, 0,1,2,4);

  }

  @Test
  public void testViaFieldSet() {

    Customer c2 = new Customer();
    EntityBean c2Entity = (EntityBean)c2;
    EntityBeanIntercept c2Intercept = c2Entity._ebean_getIntercept();

    Date now = new Date();

    c2Entity._ebean_setField(0, 12L);
    c2Entity._ebean_setField(1, 34L);
    c2.setName("c2name");
    c2.setWhenStart(now);
    assertEquals(Long.valueOf(12), c2.getId());
    assertEquals(Long.valueOf(12), c2Entity._ebean_getField(0));
    assertEquals(Long.valueOf(34), c2.getVersion());
    assertEquals(Long.valueOf(34), c2Entity._ebean_getField(1));
    assertEquals("c2name", c2.getName());
    assertEquals("c2name", c2Entity._ebean_getField(3));
    assertEquals(now, c2.getWhenStart());
    assertEquals(now, c2Entity._ebean_getField(4));

    // AFTER LOADED ... then changes are tracked
    c2Intercept.setLoaded();
    Assert.assertNull(c2Intercept.getChanged());


    c2.setName("c2NameChanged");
    assertNotNull(c2Intercept.getChanged());
    assertEquals(PROPERTY_COUNT, c2Intercept.getChanged().length);
    assertChanged(c2Intercept, 3);
    assertNotChanged(c2Intercept, 0,1,2,4);

    // Call setters but no actual change in value
    c2.setWhenStart(now);
    c2.setName("c2"+"Name"+"Changed");
    assertChanged(c2Intercept, 3);
    assertNotChanged(c2Intercept, 0,1,2,4);
  }

  private void assertLoaded(EntityBeanIntercept intercept, int... idx) {
    assertThatLoaded(intercept, true, idx);
  }

  private void assertNotLoaded(EntityBeanIntercept intercept, int... idx) {
    assertThatLoaded(intercept, false, idx);
  }

  private void assertChanged(EntityBeanIntercept intercept, int... idx) {
    assertThatChanged(intercept, true, idx);
  }

  private void assertNotChanged(EntityBeanIntercept intercept, int... idx) {
    assertThatChanged(intercept, false, idx);
  }

  private void assertThatLoaded(EntityBeanIntercept intercept, boolean propertyLoaded, int... idx) {
    boolean[] loaded = intercept.getLoaded();
    assertNotNull(loaded);
    for (int pos : idx) {
      assertEquals(propertyLoaded, loaded[pos]);
    }
  }

  private void assertThatChanged(EntityBeanIntercept intercept, boolean propertyLoaded, int... idx) {
    boolean[] changedFlags = intercept.getChanged();
    assertNotNull(changedFlags);
    for (int pos : idx) {
      assertEquals(propertyLoaded, changedFlags[pos]);
    }
  }

}
