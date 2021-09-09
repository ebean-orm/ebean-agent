package test.model.domain;

import io.ebean.bean.EntityBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParentTest {

  @Test
  public void testBuild() {

    final ParentWith parentWith = new ParentWith("hello");
    assertNotNull(parentWith);

    final ParentWith2 parentWith2 = new ParentWith2();
    assertNotNull(parentWith2);

    checkEntity(parentWith);
    checkEntity(parentWith2);
  }

  private void checkEntity(Object bean) {
    assertTrue(bean instanceof EntityBean);

    EntityBean eb = (EntityBean) bean;
    String[] props = eb._ebean_getPropertyNames();
    assertEquals(1, props.length);
    assertEquals("name", props[0]);
  }
}
