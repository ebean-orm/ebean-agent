package test.model;

import javax.persistence.Embeddable;

@Embeddable
public class MyEmbeddedBean {

  String name;

  String desc;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
