package test.model;

import io.ebean.annotation.DocStore;

import javax.persistence.DiscriminatorValue;

@DocStore
@DiscriminatorValue("string")
public class StringCustomProperty extends CustomProperty<String> {

  private String value;

  public StringCustomProperty(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
