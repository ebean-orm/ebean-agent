package test.model;

import io.ebean.Ebean;
import io.ebean.annotation.Transactional;
import io.ebeaninternal.api.SpiTransaction;

@Transactional(getGeneratedKeys = false, batchSize = 50)
public class SomeTransactionalServiceCls {

  Boolean getGeneratedKeys;

  public void someMethod(String param) {

    System.out.println("--- in someMethod");

    SpiTransaction tdTransaction = (SpiTransaction)Ebean.currentTransaction();
    getGeneratedKeys = tdTransaction.getBatchGetGeneratedKeys();
  }

}
