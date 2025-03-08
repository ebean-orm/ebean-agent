package test.model;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.*;

@Entity
public class WithInitialisedCollections2 extends BaseEntity {

  String name;

  @OneToMany(cascade = CascadeType.PERSIST)
  final List<Contact> listOf = List.of();
  @OneToMany(cascade = CascadeType.PERSIST)
  final Set<Contact> setOf = Set.of();
  @OneToMany(cascade = CascadeType.PERSIST)
  Map<Long,Contact> mapOf = Map.of();

  @OneToMany(cascade = CascadeType.PERSIST)
  final List<Contact> listCollEmpty = Collections.emptyList();
  @OneToMany(cascade = CascadeType.PERSIST)
  final Set<Contact> setCollEmpty = Collections.emptySet();
  @OneToMany(cascade = CascadeType.PERSIST)
  Map<Long,Contact> mapCollEmpty = Collections.emptyMap();

  // @OneToMany final List<Contact> listCollNotValidInitialisation0 = new io.ebean.common.BeanList<>();
  // @OneToMany final List<Contact> listCollNotValidInitialisation1 = Collections.EMPTY_LIST;
  // @OneToMany final List<Contact> listCollNotValidInitialisation2 = List.of(new Contact("junk"));
  // @OneToMany final List<Contact> listCollNotValidInitialisation3 = Collections.unmodifiableList(Collections.emptyList());

  @Transient
  List<String> transientList = List.of();
  @Transient
  Set<String> transientSet = Set.of();
  @Transient
  Map<String,Contact> transientMap = Map.of();
  @Transient
  List<String> transientList2 = Collections.emptyList();
  @Transient
  Set<String> transientSet2 = Collections.emptySet();
  @Transient
  Map<String,Contact> transientMap2 = Collections.emptyMap();

  public String name() {
    return name;
  }

  public WithInitialisedCollections2 setName(String name) {
    this.name = name;
    return this;
  }

  public List<Contact> listOf() {
    return listOf;
  }

  public Set<Contact> setOf() {
    return setOf;
  }

  public Map<Long, Contact> mapOf() {
    return mapOf;
  }

  public List<Contact> listCollEmpty() {
    return listCollEmpty;
  }

  public Set<Contact> setCollEmpty() {
    return setCollEmpty;
  }

  public Map<Long, Contact> mapCollEmpty() {
    return mapCollEmpty;
  }

  public List<String> transientList() {
    return transientList;
  }

  public Set<String> transientSet() {
    return transientSet;
  }

  public List<String> transientList2() {
    return transientList2;
  }

  public Set<String> transientSet2() {
    return transientSet2;
  }

  public Map<String, Contact> transientMap() {
    return transientMap;
  }

  public Map<String, Contact> transientMap2() {
    return transientMap2;
  }
}
