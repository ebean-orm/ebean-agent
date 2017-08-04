package test.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class NoEnhanceMappedSuper {

  static String oneStatic;
  
  transient String oneInstance;
  
}
