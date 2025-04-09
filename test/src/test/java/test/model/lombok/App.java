package test.model.lombok;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class App {

  @Id
  long id;

  @Column
  String name;
}
