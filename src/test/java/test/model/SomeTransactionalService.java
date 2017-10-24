package test.model;

import io.ebean.Ebean;
import io.ebean.annotation.SomePath;
import io.ebean.annotation.Transactional;
import io.ebeaninternal.api.SpiTransaction;

public class SomeTransactionalService {

  public Boolean getGeneratedKeys;

  @SomePath(name = "HelloWorld")
  @Transactional(getGeneratedKeys = false, batchSize = 100, profileId = 100)
  public void someMethod(String param) {

    System.out.println("--- in someMethod");

    SpiTransaction tdTransaction = (SpiTransaction)Ebean.currentTransaction();
    getGeneratedKeys = tdTransaction.getBatchGetGeneratedKeys();
  }

  @Transactional
  public int someInt() {
    return 42;
  }

  @Transactional
  public double someDouble() {
    return 42.0;
  }

  @Transactional
  public Object someObject() {
    return "Foo" + "Bar";
  }
}
