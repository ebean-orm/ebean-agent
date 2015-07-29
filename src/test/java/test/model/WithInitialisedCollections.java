package test.model;


import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
public class WithInitialisedCollections extends BaseEntity {

  String name;
  
  Date whenStart;
  
  @OneToMany
  List<Contact> contacts = new ArrayList<Contact>();

  @OneToMany
  Set<Contact> myset = new HashSet<Contact>();

  @OneToMany
  Set<Contact> myLinkedSet = new LinkedHashSet<Contact>();

  @Transient
  Set<String> strings = new HashSet<String>();

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

  public Set<Contact> getMyset() {
    return myset;
  }

  public void setMyset(Set<Contact> myset) {
    this.myset = myset;
  }

  public Set<Contact> getMyLinkedSet() {
    return myLinkedSet;
  }

  public void setMyLinkedSet(Set<Contact> myLinkedSet) {
    this.myLinkedSet = myLinkedSet;
  }

  public Set<String> getStrings() {
    return strings;
  }

  public void setStrings(Set<String> strings) {
    this.strings = strings;
  }
}
