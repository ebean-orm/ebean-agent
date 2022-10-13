package test.model;

import io.ebean.DB;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

class BeanWithInvalidTransientInitTest {

  @Test
  void testWithInitializer() {
    UUID id1 = UUID.randomUUID();

    BeanWithInvalidTransientInit bean1 = new BeanWithInvalidTransientInit(id1);
    bean1.setName("Foo");

    assertThat(bean1.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientLock()).isInstanceOf(ReentrantLock.class);
    assertThat(bean1.invalidTransientInitViaMethod()).isInstanceOf(HashSet.class);
    assertThat(bean1.invalidTransientInitWithoutDefaultConstructor()).isInstanceOf(HashMap.class);

    DB.save(bean1);

    bean1 = DB.find(BeanWithInvalidTransientInit.class, id1);

    assertThat(bean1.getName()).isEqualTo("Foo");
    assertThat(bean1.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientLock()).isInstanceOf(ReentrantLock.class);

    // these are null as when ebean adds the default constructor it is
    // not adding code to initialise these transient fields
    assertThat(bean1.invalidTransientInitViaMethod()).isNull();
    assertThat(bean1.invalidTransientInitWithoutDefaultConstructor()).isNull();
  }
}
