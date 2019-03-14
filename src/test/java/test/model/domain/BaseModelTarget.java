package test.model.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.sql.Timestamp;

/**
 * Base domain object with Id, version, whenCreated and whenUpdated.
 *
 * <p>
 * Extending Model to enable the 'active record' style.
 *
 * <p>
 * whenCreated and whenUpdated are generally useful for maintaining external search services (like
 * elasticsearch) and audit.
 */
@MappedSuperclass
public abstract class BaseModelTarget extends Model {

  @Id
  protected Long id;

  @Version
  protected Long version;

  @WhenCreated
  protected Timestamp whenCreated;

  @WhenModified
  protected Timestamp whenModified;

  public BaseModelTarget() {
    super("central");
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public Timestamp getWhenCreated() {
    return whenCreated;
  }

  public void setWhenCreated(Timestamp whenCreated) {
    this.whenCreated = whenCreated;
  }

  public Timestamp getWhenModified() {
    return whenModified;
  }

  public void setWhenModified(Timestamp whenModified) {
    this.whenModified = whenModified;
  }

}
