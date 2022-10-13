package test.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bean with default constructor - all transient initialisation is under developer control.
 */
@Entity
public class BeanWithTransientInitDC {

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
  private final Collection<String> transientInitWithoutDefaultConstructor;

  /**
   * Unsupported, as using method to initialise.
   */
  @Transient
  private Set<String> transientInitViaMethod;

  /**
   * Developer specified Default constructor - so ALL transient initialisation is under developer control.
   */
  public BeanWithTransientInitDC() {
    this.transientInitViaMethod = initialiseViaMethod();
    this.transientInitWithoutDefaultConstructor = new LinkedHashSet<>(40);
  }

  public BeanWithTransientInitDC(UUID id) {
    this.id = id;
    this.transientInitViaMethod = initialiseViaMethod();
    this.transientInitWithoutDefaultConstructor = new ArrayList<>(40);
  }

  public BeanWithTransientInitDC(String name) {
    this.name = name;
    this.transientInitViaMethod = initialiseViaMethod();
    this.transientInitWithoutDefaultConstructor = new HashSet<>(16);
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

  public Collection<String> transientInitWithoutDefaultConstructor() {
    return transientInitWithoutDefaultConstructor;
  }

  public Set<String> transientInitViaMethod() {
    return transientInitViaMethod;
  }

  public Lock transientLock() {
    return lock;
  }
}
