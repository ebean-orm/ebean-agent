package test.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Mapped superClass with equals/hashCode also needs to be enhanced.
 */
@MappedSuperclass
public abstract class BaseWithEquals {

  public transient int equalsCount;
  
  public String toString() {
    return ""+equalsCount;
  }

  public boolean equals(Object obj) {
    equalsCount++;
    return (obj != null);
  }

}
