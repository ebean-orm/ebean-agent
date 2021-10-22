package test.model.normalize;

import javax.persistence.MappedSuperclass;

import io.ebean.annotation.Normalize;
import test.normalize.Trimmer;

@Normalize(Trimmer.class)
@MappedSuperclass
public class BaseNormalizeModel {

}
