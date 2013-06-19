package test.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.avaje.ebean.bean.EntityBeanIntercept;

@MappedSuperclass
public class BaseEntity {

  @Id
  Long id;

  @Version
  Long version;

  private static String[] _props = { "id", "version", "junk", "asdad" };

  // EntityBeanIntercept ebi = null;

  // Object one;
  // Object two;

  public void setfield(int pos, Object bean, Object value) {
    BaseEntity b = ((BaseEntity) bean);
    switch (pos) {
    case 0:
      b.setOne(value);
    case 1:
      b.setTwo(value);
    default:
      throw new IllegalArgumentException("Invalid index " + pos);
    }
  }

  public Object getfield2(int pos) {
    switch (pos) {
    case 0: return one;
    case 1: return getTwo();
    default:
    throw new IllegalArgumentException("Invalid index " + pos);
    }
  }
  
  Object one;
  public Long getTwo() {
    return null;
  }
  
//  public void setfield2(int pos, Object value) {
//    switch (pos) {
//    case 0:
//      // setOne(value);
//      this.one = value;
//    case 1:
//      setTwo(value);
//    default:
//      throw new IllegalArgumentException("Invalid index " + pos);
//    }
//  }

  public void setOne(Object o) {

  }

  public void setTwo(Object o) {

  }

  public Long getId() {
    // ebi.preGetter(3);
    return id;
  }

  public void setId(Long id) {
    // ebi.setLoadedProperty(4);
    // ebi.preSetter(true, 4, getId(), id);
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  //
  // public String[] _get_props() {
  // return _props;
  // }
  // public String _get_prop(int pos) {
  // return _props[pos];
  // }

}
