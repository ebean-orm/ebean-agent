package test.enhancement;


import io.ebean.bean.EntityBean;
import org.testng.annotations.Test;
import test.model.domain.XModelA;
import test.model.domain.XModelB;
import test.model.domain.XModelC;
import test.model.domain.XModelC2;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanExtendsModel extends BaseTest {

  @Test
  public void test_simpleExtends() {

    XModelA xModelA = new XModelA();
    XModelB xModelB = new XModelB();

    assertThat(propertyNames(xModelA)).containsExactly("id", "name");
    assertThat(propertyNames(xModelB)).containsExactly("id", "name");

    XModelC xModelC = new XModelC("foo");
    XModelC2 xModelC2 = new XModelC2("foo");

    assertThat(propertyNames(xModelC)).containsExactly("id", "name");

    assertThat(propertyNames(xModelC2)).containsExactly("id", "version", "whenCreated", "whenModified", "name");

  }

  private String[] propertyNames(Object bean) {
    return ((EntityBean) bean)._ebean_getPropertyNames();
  }

}
