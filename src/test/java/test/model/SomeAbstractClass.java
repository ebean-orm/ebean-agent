package test.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SomeAbstractClass {

  public abstract String sayHello();
}
