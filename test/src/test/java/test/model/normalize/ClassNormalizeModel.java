package test.model.normalize;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.annotation.Normalize;
import test.normalize.LeadingZeroRemover;
import test.normalize.Trimmer;

@Entity
@Normalize(Trimmer.class)
public class ClassNormalizeModel {

  @Id
  int id;

  String name;

  @Normalize(value = {})
  String noNormalize;

  @Normalize(LeadingZeroRemover.class)
  String zerosNormalize;

  @Normalize({ Trimmer.class, LeadingZeroRemover.class })
  String bothNormalize;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNoNormalize() {
    return noNormalize;
  }

  public void setNoNormalize(String noNormalize) {
    this.noNormalize = noNormalize;
  }

  public String getZerosNormalize() {
    return zerosNormalize;
  }

  public void setZerosNormalize(String zerosNormalize) {
    this.zerosNormalize = zerosNormalize;
  }

  public String getBothNormalize() {
    return bothNormalize;
  }

  public void setBothNormalize(String bothNormalize) {
    this.bothNormalize = bothNormalize;
  }

}
