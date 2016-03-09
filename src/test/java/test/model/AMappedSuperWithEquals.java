package test.model;

import javax.persistence.MappedSuperclass;

/**
 * Only has hashCode() and equals() methods with no persistent fields.
 */
@MappedSuperclass
public abstract class AMappedSuperWithEquals {

  @Override
  public int hashCode() {
    return 42;
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }
}
