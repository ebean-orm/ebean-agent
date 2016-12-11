package test.model;

import io.ebean.Ebean;
import io.ebean.TDTransaction;
import io.ebean.annotation.SomePath;
import io.ebean.annotation.Transactional;

public class SomeTransactionalService {

  public Boolean getGeneratedKeys;

  @SomePath(name = "HelloWorld")
  @Transactional(getGeneratedKeys = false, batchSize = 100)
  public void someMethod(String param) {

    System.out.println("--- in someMethod");

    TDTransaction tdTransaction = Ebean.testScopedTransaction();
    getGeneratedKeys = tdTransaction.getBatchGetGeneratedKeys();
  }

}
