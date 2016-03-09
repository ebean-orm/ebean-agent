package test.enhancement;

import org.junit.Test;
import test.model.AMappedSuperChild;

import static org.junit.Assert.assertEquals;

public class MappedSuperWithEqualsTests extends BaseTest {

  @Test
  public void test() {

    // make sure we can construct it and set values
    AMappedSuperChild bean = new AMappedSuperChild();
    bean.setName("name");
    bean.setId(42L);

    assertEquals("name", bean.getName());
  }
}
