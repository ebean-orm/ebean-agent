package test.model;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bean with Transient collections and multiple constructors.
 */
@Entity
public class BeanWithTransientInit {

  @Id
  private UUID id;
  private String name;
  @Transient
  private final Collection<String> coll1 = new HashSet<>();
  @Transient
  private final Map<String, String> coll2;
  {
    coll2 = new HashMap<>();
  }
  @Transient
  private final Set<String> coll3;

  /**
   * Check transient initialisation of non persistence collection type.
   */
  @Transient
  private final Set<String> coll4 = new TreeSet<>();

  /**
   * Check transient initialisation of non collection type.
   */
  @Transient
  private final Lock lock = new ReentrantLock();

  public BeanWithTransientInit(UUID id) {
    this.coll3 = new HashSet<>();
    this.id = id;
  }

  public BeanWithTransientInit(String name) {
    this.coll3 = new TreeSet<>();
    this.name = name;
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

  public Collection<String> transientColl1() {
    return coll1;
  }

  public Map<String, String> transientColl2() {
    return coll2;
  }

  public Set<String> transientColl3() {
    return coll3;
  }

  public Set<String> transientColl4() {
    return coll4;
  }

  public Lock transientLock() {
    return lock;
  }
}
