package test.model;

import io.ebean.Ebean;
import io.ebean.Transaction;
import io.ebean.annotation.Transactional;

import static org.testng.Assert.assertNotNull;

@Transactional
public class SomeTransactionalWithStatic {

  static {
    Boolean anything = true;
  }

  public void someMethod(String param) {

    Transaction tdTransaction = Ebean.currentTransaction();
    System.out.println("--- in someMethod " + tdTransaction);
    assertNotNull(tdTransaction);
  }
}
