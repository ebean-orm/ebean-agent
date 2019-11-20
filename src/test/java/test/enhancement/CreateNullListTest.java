package test.enhancement;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.common.BeanList;
import org.testng.annotations.Test;
import test.model.Contact;
import test.model.Customer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
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

    Set<String> codes = customer.getCodes();
    assertNotNull(codes);
    assertThat(codes).isInstanceOf(LinkedHashSet.class);

    List<String> codesList = customer.getCodesList();
    assertNotNull(codesList);
    assertThat(codesList).isInstanceOf(ArrayList.class);

    Set<String> codes2 = customer.getCodes2();
    assertNotNull(codes);
    assertThat(codes2).isInstanceOf(LinkedHashSet.class);

    List<String> codesList2 = customer.getCodesList2();
    assertNotNull(codesList2);
    assertThat(codesList2).isInstanceOf(ArrayList.class);

    Set<String> codesTree = customer.getCodesTree();
    assertNotNull(codesTree);
    assertThat(codesTree).isInstanceOf(TreeSet.class);

    // contacts created automatically when "checkNullManyFields" is set
    List<Contact> contacts = customer.getContacts();
    assertNotNull(contacts);

    // Not invoking lazy loading btw
    assertTrue(contacts.isEmpty());

    contacts.add(new Contact("1"));
    assertFalse(contacts.isEmpty());

    assertTrue(contacts instanceof BeanList<?>);

  }

}
