package test.model.domain;

import io.ebean.Model;

import javax.persistence.MappedSuperclass;

/**
 * MappedSuperclass with no properties.
 */
@MappedSuperclass
public abstract class ParentWithDbParam extends Model {

  public ParentWithDbParam(String dbName) {
    super(dbName);
    //((EntityBean) this)._ebean_intercept();
  }

}
