package test.model.domain.query;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryServiceTest {

  @Test
  public void test() {

    QueryService service = new QueryService();
    final String name = service.name;
    final Object other = service.findOne();

    assertThat(name).isNull();
    assertThat(other).isNull();
  }
}
