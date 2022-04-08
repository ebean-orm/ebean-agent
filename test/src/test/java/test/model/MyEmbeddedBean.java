package test.model;

import io.ebean.example.EbString;
import io.ebean.example.ToStringBuilder;

import javax.persistence.Embeddable;

@Embeddable
public class MyEmbeddedBean implements EbString {

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

  public void toString(ToStringBuilder sb) {
    sb.start(this);
    sb.add("name", name);
    sb.add("desc", desc);
    sb.end();
  }
}
