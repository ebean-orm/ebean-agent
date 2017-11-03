package test.enhancement;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.common.BeanList;
import org.testng.annotations.Test;
import test.model.Contact;
import test.model.Customer;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CreateNullListTest extends BaseTest {

  @Test
  public void test() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();

     assertTrue(intercept.isNew());

    // contacts created automatically when "checkNullManyFields" is set
    List<Contact> contacts = customer.getContacts();
    assertNotNull(contacts);

    // Not invoking lazy loading btw
    assertTrue(contacts.isEmpty());

    contacts.add(new Contact());
    assertFalse(contacts.isEmpty());

    assertTrue(contacts instanceof BeanList<?>);

  }
  
}
