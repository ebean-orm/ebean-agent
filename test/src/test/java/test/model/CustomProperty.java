package test.model;

import io.ebean.annotation.DocStore;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Inheritance;

@DocStore
@Inheritance
@DiscriminatorColumn(name = "kind")
public abstract class CustomProperty<T> {

  public abstract Object getValue();

  @Override
  public final String toString() {
    return String.valueOf(getValue());
  }

}
