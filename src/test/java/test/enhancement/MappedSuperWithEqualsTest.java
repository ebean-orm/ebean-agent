package test.enhancement;

import org.testng.annotations.Test;
import test.model.AMappedSuperChild;

import static org.testng.Assert.assertEquals;

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
