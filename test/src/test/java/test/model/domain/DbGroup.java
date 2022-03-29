package test.model.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "fh_group")
public class DbGroup {

  @Id
  private UUID id;

  private String name;

  private boolean adminGroup;

  @OneToMany(mappedBy = "group")
  private Set<DbPerson> peopleInGroup;

  public DbGroup() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAdminGroup() {
    return adminGroup;
  }

  public void setAdminGroup(boolean adminGroup) {
    this.adminGroup = adminGroup;
  }

  public Set<DbPerson> getPeopleInGroup() {
    return peopleInGroup;
  }

  public void setPeopleInGroup(Set<DbPerson> peopleInGroup) {
    this.peopleInGroup = peopleInGroup;
  }

  private DbGroup(Builder builder) {
    setAdminGroup(builder.adminGroup);
    setName(builder.name);
    setPeopleInGroup(builder.peopleInGroup);
  }

  public static final class Builder {

    private boolean adminGroup;
    private String name;
    private Set<DbPerson> peopleInGroup;

    public Builder() {
    }

    public Builder adminGroup(boolean val) {
      adminGroup = val;
      return this;
    }

    public Builder name(String val) {
      name = val;
      return this;
    }

    public Builder peopleInGroup(Set<DbPerson> val) {
      peopleInGroup = val;
      return this;
    }

    public DbGroup build() {
      return new DbGroup(this);
    }
  }
}
