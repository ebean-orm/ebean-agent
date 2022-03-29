package test.model;

import io.ebean.bean.EntityBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NoEnhanceMappedSuperEntityTest {

  @Test
  public void construct() {

    NoEnhanceMappedSuperEntity bean = new NoEnhanceMappedSuperEntity();

    assertNotNull(bean);

    assertTrue(bean instanceof EntityBean);
    assertTrue(bean instanceof NoEnhanceMappedSuper);
    assertTrue(bean instanceof NoEnhanceMappedSuperEntity);

    EntityBean eb = (EntityBean)bean;
    String[] props = eb._ebean_getPropertyNames();
    assertEquals(2, props.length);
    assertEquals("id", props[0]);
    assertEquals("name", props[1]);
  }
}
