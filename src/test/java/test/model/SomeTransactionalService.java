package test.model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TDTransaction;
import com.avaje.ebean.annotation.SomePath;
import com.avaje.ebean.annotation.Transactional;

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
