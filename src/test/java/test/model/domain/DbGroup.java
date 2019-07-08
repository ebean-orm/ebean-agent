package test.model.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

  @ManyToOne(optional = false)
  private DbPerson whoCreated;

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

  public DbPerson getWhoCreated() {
    return whoCreated;
  }

  public void setWhoCreated(DbPerson whoCreated) {
    this.whoCreated = whoCreated;
  }

  public Set<DbPerson> getPeopleInGroup() {
    return peopleInGroup;
  }

  public void setPeopleInGroup(Set<DbPerson> peopleInGroup) {
    this.peopleInGroup = peopleInGroup;
  }

  private DbGroup(Builder builder) {
    setWhoCreated(builder.whoCreated);
    setAdminGroup(builder.adminGroup);
//    setOwningOrganization(builder.owningOrganization);
    setName(builder.name);
//    setGroupRolesAcl(builder.groupRolesAcl);
    setPeopleInGroup(builder.peopleInGroup);
  }

  public static final class Builder {

//    private DbPortfolio owningPortfolio;
//    private DbOrganization owningOrganization;
//    private Set<DbAcl> groupRolesAcl;

    private DbPerson whoCreated;
    private boolean adminGroup;
    private String name;
    private Set<DbPerson> peopleInGroup;

    public Builder() {
    }

    public Builder whoCreated(DbPerson val) {
      whoCreated = val;
      return this;
    }
//
//    public Builder owningPortfolio(DbPortfolio val) {
//      owningPortfolio = val;
//      return this;
//    }

    public Builder adminGroup(boolean val) {
      adminGroup = val;
      return this;
    }

//    public Builder owningOrganization(DbOrganization val) {
//      owningOrganization = val;
//      return this;
//    }

    public Builder name(String val) {
      name = val;
      return this;
    }

    //    public Builder groupRolesAcl(Set<DbAcl> val) {
//      groupRolesAcl = val;
//      return this;
//    }
//
    public Builder peopleInGroup(Set<DbPerson> val) {
      peopleInGroup = val;
      return this;
    }

    public DbGroup build() {
      return new DbGroup(this);
    }
  }
}
