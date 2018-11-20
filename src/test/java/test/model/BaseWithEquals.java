package test.model;

import javax.persistence.MappedSuperclass;

/**
 * Mapped superClass with equals/hashCode also needs to be enhanced.
 */
@MappedSuperclass
public abstract class BaseWithEquals {

  public transient int equalsCount;

  @Override
  public String toString() {
    return ""+equalsCount;
  }

  @Override
  public boolean equals(Object obj) {
    equalsCount++;
    return (obj != null);
  }

}
