package test.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by rob on 4/11/14.
 */
@Entity
public class SomeXtendsBaseWithEquals extends BaseWithEquals {

  @Id
  Long id;

  String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
