package test.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseWithEqualsEntity {

  @Id
  Long id;

  @Version
  Long version;

  private transient int equalsCount;

  @Override
  public String toString() {
    return ""+equalsCount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object obj) {
    equalsCount++;
    return (obj != null);
  }


}
