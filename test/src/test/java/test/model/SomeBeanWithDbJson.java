package test.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.annotation.DbJson;
import test.normalize.PostJsonGetter;

/**
 * An entity for PostJsonGetter feature.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
@Entity
public class SomeBeanWithDbJson {

  @Id
  Long id;

  String name;

  MyDbJson one;

  List<MyDbJson> oneList = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MyDbJson getOne() {
    return one;
  }

  public void setOne(MyDbJson one) {
    this.one = one;
  }

  public List<MyDbJson> getOneList() {
    return oneList;
  }

  protected /* synthetic */ MyDbJson blahblah() {
    System.out.println();
    PostJsonGetter.postJsonGet(this, this.one, "one");
    return this.one;
 }

  @Override
  public String toString() {
    return "SomeBeanWithDbJson";
  }
}
