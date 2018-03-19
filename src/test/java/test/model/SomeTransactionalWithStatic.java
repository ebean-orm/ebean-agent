package test.model;

import io.ebean.Ebean;
import io.ebean.Transaction;
import io.ebean.annotation.Transactional;
import io.ebean.annotation.TxType;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Transactional(type = TxType.REQUIRED)
public class SomeTransactionalWithStatic {

  static {
    Boolean anything = true;

    Transaction tdTransaction = Ebean.currentTransaction();
    System.out.println("--- in <clinit> " + tdTransaction);
    assertNull(tdTransaction);
  }

  @Transactional(label = "something")
  public void someMethod(String param) {

    Transaction tdTransaction = Ebean.currentTransaction();
    System.out.println("--- in someMethod " + tdTransaction);
    assertNotNull(tdTransaction);
  }
}
