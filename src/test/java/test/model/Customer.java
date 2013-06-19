package test.model;

import java.sql.Date;

import javax.persistence.Entity;

@Entity
public class Customer extends BaseEntity {

  String name;
  
  Date whenStart;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getWhenStart() {
    return whenStart;
  }

  public void setWhenStart(Date whenStart) {
    this.whenStart = whenStart;
  }
  
}
