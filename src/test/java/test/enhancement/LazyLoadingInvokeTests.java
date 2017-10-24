package test.enhancement;

import io.ebean.bean.BeanLoader;
import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.model.Contact;
import test.model.Customer;

import java.util.List;

public class LazyLoadingInvokeTests extends BaseTest {

  @Test
  public void testNoLazyLoadOnToString() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();
    MyBeanLoader myBeanLoader = new MyBeanLoader();

    setupCustomerState(customer, intercept, myBeanLoader);

    // toString does not invoke lazy loading
    String toStringValue = customer.toString();

    Assert.assertNull(myBeanLoader.wasLoad);
    Assert.assertEquals(0, myBeanLoader.count);
    Assert.assertEquals("id:27 name:null", toStringValue);
  }

  @Test
  public void testLazyLoadOnGet() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();
    MyBeanLoader myBeanLoader = new MyBeanLoader();

    setupCustomerState(customer, intercept, myBeanLoader);

    // Invoke lazy loading
    customer.getName();

    // It happened
    Assert.assertTrue(myBeanLoader.wasLoad == intercept);
    Assert.assertEquals(1, myBeanLoader.count);

    // The index on the lazy loaded property was set
    Assert.assertEquals(3, intercept.getLazyLoadPropertyIndex());
    Assert.assertEquals("name", intercept.getProperty(3));
  }

  @Test
  public void testLazyLoadOnGetSuperField() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();
    MyBeanLoader myBeanLoader = new MyBeanLoader();

    setupCustomerState(customer, intercept, myBeanLoader);

    // Invoke lazy loading
    customer.getOne();

    // It happened
    Assert.assertTrue(myBeanLoader.wasLoad == intercept);
    Assert.assertEquals(1, myBeanLoader.count);

    // The index on the lazy loaded property was set
    Assert.assertEquals(2, intercept.getLazyLoadPropertyIndex());
    Assert.assertEquals("one", intercept.getProperty(2));
  }

  private void setupCustomerState(Customer customer, EntityBeanIntercept intercept, MyBeanLoader myBeanLoader) {

    customer.setId(27l);
    intercept.setBeanLoader(myBeanLoader);
    intercept.setLoaded();
    Assert.assertNull(myBeanLoader.wasLoad);
    Assert.assertEquals(0, myBeanLoader.count);
  }

  @Test
  public void testLazyLoadOnSet() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();
    MyBeanLoader myBeanLoader = new MyBeanLoader();

    setupCustomerState(customer, intercept, myBeanLoader);

    // Invoke lazy loading
    customer.setName("blah");

    // It happened as expected
    Assert.assertTrue(myBeanLoader.wasLoad == intercept);
    Assert.assertEquals(1, myBeanLoader.count);
    Assert.assertEquals("blah", customer.getName());
  }


  @Test
  public void testLazyLoadOnGetMany() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();
    MyBeanLoader myBeanLoader = new MyBeanLoader();

    setupCustomerState(customer, intercept, myBeanLoader);

    // Invoke lazy loading
    List<Contact> contacts = customer.getContacts();

    // It happened as expected
    Assert.assertTrue(myBeanLoader.wasLoad == intercept);
    Assert.assertEquals(1, myBeanLoader.count);
    Assert.assertNotNull(contacts);
  }

  /**
   * For asserting the lazy loading occured.
   */
  private static class MyBeanLoader implements BeanLoader {

    int count;

    EntityBeanIntercept wasLoad;

    @Override
    public String getName() {
      return null;
    }

    @Override
    public void loadBean(EntityBeanIntercept ebi) {
      this.count++;
      this.wasLoad = ebi;
    }

  }
}
