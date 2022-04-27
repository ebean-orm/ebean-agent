package test.model;

import io.ebean.annotation.DocStore;

@DocStore
public abstract class CustomProperty<T> {

  public abstract Object getValue();

  @Override
  public String toString() {
    return String.valueOf(getValue());
  }

}
