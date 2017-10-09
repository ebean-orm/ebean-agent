package test.model;

import io.ebean.Ebean;
import io.ebean.TDTransaction;
import io.ebean.annotation.Transactional;

import static org.testng.Assert.assertNotNull;

@Transactional
public class SomeTransactionalWithStatic {

  static {
    Boolean anything = true;
  }

  public void someMethod(String param) {

    TDTransaction tdTransaction = Ebean.testScopedTransaction();
    System.out.println("--- in someMethod " + tdTransaction);
    assertNotNull(tdTransaction);
  }
}
