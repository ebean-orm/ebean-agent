package test.model.domain;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class XModelC extends Model {

  @Id
  long id;

  final String name;

  public XModelC(String name) {
    super("other");
    this.name = name;
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

}
