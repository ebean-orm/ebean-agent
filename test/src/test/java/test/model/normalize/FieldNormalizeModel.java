package test.model.normalize;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.annotation.Normalize;
import test.normalize.EvenNumber;
import test.normalize.Trimmer;

@Entity
public class FieldNormalizeModel {

  @Id
  int id;

  @Normalize(EvenNumber.class)
  int evenNumberOnly;

  @Normalize(Trimmer.class)
  String name;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getEvenNumberOnly() {
    return evenNumberOnly;
  }

  public void setEvenNumberOnly(int evenNumberOnly) {
    this.evenNumberOnly = evenNumberOnly;
  }
}
