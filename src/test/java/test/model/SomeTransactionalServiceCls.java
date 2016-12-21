package test.model;

import io.ebean.Ebean;
import io.ebean.TDTransaction;
import io.ebean.annotation.Transactional;

@Transactional(getGeneratedKeys = false, batchSize = 50)
public class SomeTransactionalServiceCls {

  Boolean getGeneratedKeys;

  public void someMethod(String param) {

    System.out.println("--- in someMethod");

    TDTransaction tdTransaction = Ebean.testScopedTransaction();
    getGeneratedKeys = tdTransaction.getBatchGetGeneratedKeys();
  }

}
