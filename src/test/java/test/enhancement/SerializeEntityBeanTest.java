package test.enhancement;

import org.testng.annotations.Test;
import test.model.Customer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.testng.Assert.assertEquals;

public class SerializeEntityBeanTest extends BaseTest {

  @Test
  public void test() throws IOException, ClassNotFoundException {

    Customer customer = new Customer();
    customer.setName("hello");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(customer);
    oos.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    Customer bean = (Customer)ois.readObject();

    final String name = bean.getName();
    assertEquals("hello", name);
  }
}
