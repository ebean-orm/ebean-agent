package test.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NoEnhanceMappedSuperEntity extends NoEnhanceMappedSuper {

  @Id
  long id;

  String name;
}
