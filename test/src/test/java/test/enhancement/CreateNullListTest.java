package test.enhancement;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.common.BeanList;
import org.junit.jupiter.api.Test;
import test.model.Contact;
import test.model.Customer;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CreateNullListTest extends BaseTest {

  @Test
  public void test() {

    Customer customer = new Customer();
    EntityBean customerEntity = (EntityBean)customer;
    EntityBeanIntercept intercept = customerEntity._ebean_getIntercept();

    assertTrue(intercept.isNew());

    assertNotNull(customer.nonNullArrayOne());
    assertNotNull(customer.nonNullArrayTwo());
    assertNotNull(customer.nonNullArrayThree());
    assertThat(customer.nonNullArrayOne()).isInstanceOf(ArrayList.class);
    assertThat(customer.nonNullArrayTwo()).isInstanceOf(LinkedHashSet.class);
    assertThat(customer.nonNullArrayThree()).isInstanceOf(ArrayList.class);

    assertNull(customer.getCodes());

    assertNull(customer.getCodesList());

    Set<String> codes2 = customer.getCodes2();
    assertNotNull(codes2);
    assertThat(codes2).isInstanceOf(HashSet.class);

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
    assertThat(contacts).isNotEmpty();
    assertTrue(contacts instanceof BeanList<?>);
  }

}
