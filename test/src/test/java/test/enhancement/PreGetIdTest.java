package test.enhancement;

import io.ebean.bean.EntityBean;
import org.junit.jupiter.api.Test;
import test.model.AMappedSuperChild;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PreGetIdTest extends BaseTest {

  @Test
  public void test() {

    AMappedSuperChild bean = new AMappedSuperChild();

    EntityBean eb = (EntityBean)bean;
    assertNotNull(eb);
    //assertFalse(eb._ebean_getIntercept().isCalledGetId());

    bean.getId();
    //assertTrue(eb._ebean_getIntercept().isCalledGetId());
  }
}
