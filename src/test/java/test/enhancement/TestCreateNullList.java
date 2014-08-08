package test.enhancement;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.model.Contact;
import test.model.Customer;

import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebean.bean.EntityBeanIntercept;
import com.avaje.ebean.common.BeanList;

public class TestCreateNullList extends BaseTest {

  @Test
  public void test() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();

    Assert.assertTrue(intercept.isNew());

    // contacts created automatically when "checkNullManyFields" is set
    List<Contact> contacts = customer.getContacts();
    Assert.assertNotNull(contacts);

    // Not invoking lazy loading btw
    Assert.assertTrue(contacts.isEmpty());

    contacts.add(new Contact());
    Assert.assertFalse(contacts.isEmpty());

    Assert.assertTrue(contacts instanceof BeanList<?>);

  }
  
}
