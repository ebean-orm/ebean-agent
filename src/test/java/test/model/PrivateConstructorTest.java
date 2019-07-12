package test.model;

import io.ebean.DB;
import org.testng.annotations.Test;
import test.enhancement.BaseTest;
import test.model.domain.DbGroup;
import test.model.domain.DbPerson;

import static org.assertj.core.api.Assertions.assertThat;

public class PrivateConstructorTest extends BaseTest {

  @Test
  public void test() {

    DbPerson person = new DbPerson().name("Richard");
    DB.save(person);

    final DbGroup group = new DbGroup.Builder()
      .adminGroup(true)
      .name("foo")
      .build();

    DB.save(group);

    assertThat(group.getId()).isNotNull();
    assertThat(person.getId()).isNotNull();
  }

}
