package test.enhancement;

import io.ebean.bean.BeanLoader;
import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import org.junit.jupiter.api.Test;
import test.model.Contact;
import test.model.Customer;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class LazyLoadingInvokeTest extends BaseTest {

  @Test
  public void testNoLazyLoadOnToString() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();
    MyBeanLoader myBeanLoader = new MyBeanLoader();

    setupCustomerState(customer, intercept, myBeanLoader);

    // toString does not invoke lazy loading
    String toStringValue = customer.toString();

    assertNull(myBeanLoader.wasLoad);
    assertEquals(0, myBeanLoader.count);
    assertEquals("id:27 name:null", toStringValue);
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
    assertTrue(myBeanLoader.wasLoad == intercept);
    assertEquals(1, myBeanLoader.count);

    // The index on the lazy loaded property was set
    assertEquals(3, intercept.getLazyLoadPropertyIndex());
    assertEquals("name", intercept.getProperty(3));
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
    assertTrue(myBeanLoader.wasLoad == intercept);
    assertEquals(1, myBeanLoader.count);

    // The index on the lazy loaded property was set
    assertEquals(2, intercept.getLazyLoadPropertyIndex());
    assertEquals("one", intercept.getProperty(2));
  }

  private void setupCustomerState(Customer customer, EntityBeanIntercept intercept, MyBeanLoader myBeanLoader) {

    customer.setId(27l);
    intercept.setBeanLoader(myBeanLoader);
    intercept.setLoaded();
    assertNull(myBeanLoader.wasLoad);
    assertEquals(0, myBeanLoader.count);
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
    assertTrue(myBeanLoader.wasLoad == intercept);
    assertEquals(1, myBeanLoader.count);
    assertEquals("blah", customer.getName());
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
    assertTrue(myBeanLoader.wasLoad == intercept);
    assertEquals(1, myBeanLoader.count);
    assertNotNull(contacts);
  }

  /**
  * For asserting the lazy loading occured.
  */
  private static class MyBeanLoader implements BeanLoader {

    int count;

    EntityBeanIntercept wasLoad;

    final Lock lock = new ReentrantLock();

    @Override
    public String getName() {
      return null;
    }

    @Override
    public void loadBean(EntityBeanIntercept ebi) {
      this.count++;
      this.wasLoad = ebi;
    }

    @Override
    public Lock lock() {
      this.lock.lock();
      return this.lock;
    }

  }
}
