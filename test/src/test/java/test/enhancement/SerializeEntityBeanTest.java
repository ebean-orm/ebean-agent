package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.Customer;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
