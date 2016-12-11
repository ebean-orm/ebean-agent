package test.enhancement;

import io.ebean.bean.EntityBean;
import org.testng.annotations.Test;
import test.model.AMappedSuperChild;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PreGetIdTest extends BaseTest {

  @Test
  public void test() {

    AMappedSuperChild bean = new AMappedSuperChild();

    EntityBean eb = (EntityBean)bean;
    assertFalse(eb._ebean_getIntercept().isCalledGetId());

    bean.getId();
    assertTrue(eb._ebean_getIntercept().isCalledGetId());
  }
}
