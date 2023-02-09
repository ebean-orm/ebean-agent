package test.model;


import io.ebean.annotation.DbArray;
import io.ebean.annotation.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.*;

@Entity
public class JakartaCustomer extends JakartaBaseEntity {

  String name;

  Date whenStart;

  @OneToMany(mappedBy="customer")
  List<Contact> contacts;

  @DbArray
  Set<String> codes;

  @DbArray
  List<String> codesList;

  @DbArray
  Set<String> codes2 = new HashSet<>();

  @DbArray(nullable = false)
  List<String> codesList2 = new ArrayList<>();

  @DbArray
  List<String> codesList3 = new ArrayList<>();

  @DbArray
  Set<String> codesTree = new TreeSet<>();

  @DbArray(nullable = false)
  List<String> nonNullArrayOne;

  @NotNull
  @DbArray
  Set<String> nonNullArrayTwo;

  @Column(nullable = false)
  @DbArray
  List<String> nonNullArrayThree;

  public JakartaCustomer() {
    codesList3.add("foo");
  }

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

  public Set<String> getCodes() {
    return codes;
  }

  public void setCodes(Set<String> codes) {
    this.codes = codes;
  }

  public List<String> getCodesList() {
    return codesList;
  }

  public Set<String> getCodes2() {
    return codes2;
  }

  public List<String> getCodesList2() {
    return codesList2;
  }

  public Set<String> getCodesTree() {
    return codesTree;
  }

  public List<String> nonNullArrayOne() {
    return nonNullArrayOne;
  }

  public Set<String> nonNullArrayTwo() {
    return nonNullArrayTwo;
  }

  public List<String> nonNullArrayThree() {
    return nonNullArrayThree;
  }
}
