package test.model.domain;

import javax.persistence.Entity;

@Entity
public class XModelC2 extends BaseModelTarget {

  final String name;

  public XModelC2(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
