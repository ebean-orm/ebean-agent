package test.enhancement;

import io.ebean.common.BeanList;
import io.ebean.common.BeanMap;
import io.ebean.common.BeanSet;
import org.junit.jupiter.api.Test;
import test.model.Contact;
import test.model.WithInitialisedCollections2;

import static org.assertj.core.api.Assertions.assertThat;

class WithInitialisedCollections2Test extends BaseTest {

  @Test
  void oneToMany_initialisationCode_expect_removed() {
    WithInitialisedCollections2 bean = new WithInitialisedCollections2();

    assertThat(bean.listOf()).isInstanceOf(BeanList.class);
    assertThat(bean.listCollEmpty()).isInstanceOf(BeanList.class);
    assertThat(bean.setOf()).isInstanceOf(BeanSet.class);
    assertThat(bean.setCollEmpty()).isInstanceOf(BeanSet.class);
    assertThat(bean.mapOf()).isInstanceOf(BeanMap.class);
    assertThat(bean.mapCollEmpty()).isInstanceOf(BeanMap.class);


    assertThat(bean.transientList()).isNotInstanceOf(BeanList.class);
    assertThat(bean.transientList2()).isNotInstanceOf(BeanList.class);
    assertThat(bean.transientSet()).isNotInstanceOf(BeanSet.class);
    assertThat(bean.transientSet2()).isNotInstanceOf(BeanSet.class);
    assertThat(bean.transientMap()).isNotInstanceOf(BeanMap.class);
    assertThat(bean.transientMap2()).isNotInstanceOf(BeanMap.class);


    // these methods work because the underlying collection is a BeanCollection
    bean.listOf().add(new Contact("junk"));
    bean.setOf().add(new Contact("junk"));
    bean.listCollEmpty().add(new Contact("junk"));
    bean.setCollEmpty().add(new Contact("junk"));
  }
}
