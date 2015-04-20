package test.model;

import com.avaje.ebean.annotation.SomePath;
import com.avaje.ebean.annotation.Transactional;

public class SomeTransactionalService {

  @SomePath(name = "HelloWorld")
  @Transactional
  public void someMethod(String param) {

    System.out.println("--- in someMethod");
  }

}
