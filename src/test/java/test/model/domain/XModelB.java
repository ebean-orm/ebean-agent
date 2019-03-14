package test.model.domain;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class XModelB extends Model {

  @Id
  long id;

  String name;

  public XModelB(){
    super("other");
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
