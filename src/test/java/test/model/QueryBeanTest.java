package test.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import test.enhancement.BaseTest;
import test.model.domain.Address;
import test.model.domain.query.QAddress;

import java.util.List;

public class QueryBeanTest extends BaseTest {

  @Disabled
  @Test
  public void runQuery() {

    final List<Address> list = new QAddress()
      .version.ge(1L)
      .findList();

    System.out.println("done " + list);

  }
}
