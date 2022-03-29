package test.model;

import javax.persistence.*;

/**
 * Entity bean that implements an interface.
 */
@Entity
@Table(name = "ab_test_types")
public class SomeBeanWithInterface implements SomeInterface {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @Column(name = "ab_test_type_id")
  private Short id;

  @Basic(optional = false)
  @Column(name = "ab_test_type_code")
  private String code;


  public Short getId() {
    return id;
  }

  public void setId(Short id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
