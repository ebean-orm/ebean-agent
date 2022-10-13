package test.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bean with Transient collections and multiple constructors.
 */
@Entity
public class BeanWithInvalidTransientInit {

  @Id
  private UUID id;
  private String name;
  @Transient
  private final Collection<String> transientSimpleCollection = new HashSet<>();
  @Transient
  private final Lock lock = new ReentrantLock();

  /**
   * Unsupported, as NOT using default constructor for HashMap.
   */
  @Transient
  private final Map<String, String> invalidTransientInitWithoutDefaultConstructor = new HashMap<>(16);

  /**
   * Unsupported, as using method to initialise.
   */
  @Transient
  private Set<String> invalidTransientInitViaMethod;

  public BeanWithInvalidTransientInit(UUID id) {
    this.id = id;
    this.invalidTransientInitViaMethod = initialiseViaMethod();
  }

  private Set<String> initialiseViaMethod() {
    return new HashSet<>();
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
    return transientSimpleCollection;
  }

  public Map<String, String> invalidTransientInitWithoutDefaultConstructor() {
    return invalidTransientInitWithoutDefaultConstructor;
  }

  public Set<String> invalidTransientInitViaMethod() {
    return invalidTransientInitViaMethod;
  }

  public Lock transientLock() {
    return lock;
  }
}
