package test.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.UUID;

@Entity
public class ReferencingBean {

  @Id @GeneratedValue
  UUID id;

  @OneToMany
  private List<PBean> rootBeans;

  public ReferencingBean(List<PBean> rootBeans) {
    this.rootBeans = rootBeans;
  }

  public List<PBean> getRootBeans() {
    return rootBeans;
  }

}
