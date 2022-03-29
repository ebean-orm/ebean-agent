package test.model;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class WithInitialisedCollectionAndAtTransient extends BaseEntity {

  String name;

  Date whenStart;

  @OneToMany(cascade = CascadeType.PERSIST)
  List<Contact> contacts = new ArrayList<>();

  @Transient
  StringBuilder buffer = new StringBuilder();

  @Override
  public String toString() {
    return "id:"+id+" name:"+name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getWhenStart() {
    return whenStart;
  }

  public void setWhenStart(Date whenStart) {
    this.whenStart = whenStart;
  }

  public List<Contact> getContacts() {
    return contacts;
  }

  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }

  public StringBuilder getBuffer() {
    return buffer;
  }

  public void setBuffer(StringBuilder buffer) {
    this.buffer = buffer;
  }
}
