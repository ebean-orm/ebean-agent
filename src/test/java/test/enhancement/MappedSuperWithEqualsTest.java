package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.AMappedSuperChild;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappedSuperWithEqualsTest extends BaseTest {

  @Test
  public void test() {

    // make sure we can construct it and set values
    AMappedSuperChild bean = new AMappedSuperChild();
    bean.setName("name");
    bean.setId(42L);

    assertEquals("name", bean.getName());
  }
}
