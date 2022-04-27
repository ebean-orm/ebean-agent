package test.model;

import io.ebean.annotation.DocStore;

@DocStore
public class StringCustomProperty extends CustomProperty<String> {

  private String value;

  public StringCustomProperty(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
