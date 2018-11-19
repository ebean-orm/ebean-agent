package test.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NoEnhanceMappedSuper {

  static String oneStatic;

  transient String oneInstance;

}
