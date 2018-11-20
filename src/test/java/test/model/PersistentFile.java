package test.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PersistentFile {

  @Id
  private Long id;

  private String name;

  @OneToOne(mappedBy="persistentFile", cascade=CascadeType.ALL)
  private PersistentFileContent persistentFileContent;

  public PersistentFile() {
  }

  public PersistentFile(String name, PersistentFileContent persistentFileContent) {
    super();
    this.name = name;
    this.persistentFileContent = persistentFileContent;
    this.persistentFileContent.setPersistentFile(this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PersistentFileContent getPersistentFileContent() {
    return persistentFileContent;
  }

  public void setPersistentFileContent(PersistentFileContent persistentFileContent) {
    this.persistentFileContent = persistentFileContent;
  }

}
