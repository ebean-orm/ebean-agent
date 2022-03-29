package test.model;

import java.io.IOException;

import io.ebean.Ebean;
import io.ebean.annotation.Transactional;
import io.ebeaninternal.api.SpiTransaction;

@Transactional(rollbackFor = { IOException.class, IllegalStateException.class }, batchSize = 42)
public class SomeTransactionalWithOverride {

  public void someMethod1() {
    // must inherit class level
  }

  @Transactional(rollbackFor = ArrayIndexOutOfBoundsException.class)
  public void someMethod2() {
    // merge result: rollbackFor = ArrayIndexOutOfBoundsException.class, batchSize = 42
  }

  @Transactional(rollbackFor = {})
  public void someMethod3() {
    // merge result: rollbackFor = {}, batchSize = 42
  }

  @Transactional(batchSize = 23)
  public void someMethod4() {
    // merge result: rollbackFor = { IOException.class, IllegalStateException.class }, batchSize = 23
  }

  @Transactional
  public void someMethod5() {
    // no merge - take class level annotation
  }

  @Transactional(rollbackFor = {}, batchSize = 0)
  public void someMethod6() {
    // complete merge - take method level annotation
  }
}
