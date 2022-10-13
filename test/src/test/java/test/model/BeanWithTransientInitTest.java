package test.model;

import io.ebean.DB;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

class BeanWithTransientInitTest {

  @Test
  void testWithInitializer() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    BeanWithTransientInit bean1 = new BeanWithTransientInit(id1);
    bean1.setName("Roland");

    BeanWithTransientInit bean2 = new BeanWithTransientInit("Rob");
    bean2.setId(id2);

    assertThat(bean1.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientColl2()).isInstanceOf(HashMap.class);
    assertThat(bean1.transientColl3()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientColl4()).isInstanceOf(TreeSet.class);
    assertThat(bean1.transientLock()).isInstanceOf(ReentrantLock.class);

    assertThat(bean2.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean2.transientColl2()).isInstanceOf(HashMap.class);
    assertThat(bean2.transientColl3()).isInstanceOf(TreeSet.class);
    assertThat(bean2.transientColl4()).isInstanceOf(TreeSet.class);
    assertThat(bean2.transientLock()).isInstanceOf(ReentrantLock.class);

    DB.save(bean1);
    DB.save(bean2);

    bean1 = DB.find(BeanWithTransientInit.class, id1);
    bean2 = DB.find(BeanWithTransientInit.class, id2);


    assertThat(bean1.getName()).isEqualTo("Roland");
    assertThat(bean1.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientColl2()).isInstanceOf(HashMap.class);
    assertThat(bean1.transientColl3()).isInstanceOf(HashSet.class);
    assertThat(bean1.transientColl4()).isInstanceOf(TreeSet.class);
    assertThat(bean1.transientLock()).isInstanceOf(ReentrantLock.class);

    assertThat(bean2.getName()).isEqualTo("Rob");
    assertThat(bean2.transientColl1()).isInstanceOf(HashSet.class);
    assertThat(bean2.transientColl2()).isInstanceOf(HashMap.class);
    assertThat(bean2.transientColl3()).isInstanceOf(HashSet.class);// HashSet from first constructor wins, not TreeSet.class
    assertThat(bean2.transientColl4()).isInstanceOf(TreeSet.class);
    assertThat(bean2.transientLock()).isInstanceOf(ReentrantLock.class);
  }
}
