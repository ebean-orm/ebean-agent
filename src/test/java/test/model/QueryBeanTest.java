package test.model;

import org.testng.annotations.Test;
import test.enhancement.BaseTest;
import test.model.domain.Address;
import test.model.domain.query.QAddress;

import java.util.List;

public class QueryBeanTest extends BaseTest {

  @Test(enabled = false)
  public void runQuery() {

    final List<Address> list = new QAddress()
      .version.ge(1L)
      .findList();

    System.out.println("done " + list);

  }
}
