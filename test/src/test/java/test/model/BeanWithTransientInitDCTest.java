package test.model;

import io.ebean.DB;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

class BeanWithTransientInitDCTest {

  @Test
  void testWithInitializer() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    BeanWithTransientInitDC bean1 = new BeanWithTransientInitDC(id1);
    bean1.setName("Foo");
    BeanWithTransientInitDC bean2 = new BeanWithTransientInitDC("Bar");
    bean2.setId(id2);

    assertThat(bean1.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientLock()).isInstanceOf(ReentrantLock.class);
    assertThat(bean1.transientInitViaMethod()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientInitWithoutDefaultConstructor()).isInstanceOf(ArrayList.class);

    assertThat(bean2.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean2.transientLock()).isInstanceOf(ReentrantLock.class);
    assertThat(bean2.transientInitViaMethod()).isInstanceOf(HashSet.class);
    assertThat(bean2.transientInitWithoutDefaultConstructor()).isInstanceOf(HashSet.class);

    DB.save(bean1);
    DB.save(bean2);

    {
      bean1 = DB.find(BeanWithTransientInitDC.class, id1);

      assertThat(bean1.getName()).isEqualTo("Foo");
      assertThat(bean1.transientColl1()).isInstanceOf(HashSet.class);
      assertThat(bean1.transientLock()).isInstanceOf(ReentrantLock.class);

      // Developer added the default constructor so these are still all as expected
      assertThat(bean1.transientInitViaMethod()).isInstanceOf(HashSet.class);
      assertThat(bean1.transientInitWithoutDefaultConstructor()).isInstanceOf(LinkedHashSet.class);
    }
    {
      bean2 = DB.find(BeanWithTransientInitDC.class, id2);

      assertThat(bean2.getName()).isEqualTo("Bar");
      assertThat(bean2.transientColl1()).isInstanceOf(HashSet.class);
      assertThat(bean2.transientLock()).isInstanceOf(ReentrantLock.class);

      // Developer added the default constructor so these are still all as expected
      assertThat(bean2.transientInitViaMethod()).isInstanceOf(HashSet.class);
      assertThat(bean2.transientInitWithoutDefaultConstructor()).isInstanceOf(LinkedHashSet.class);
    }
  }
}
