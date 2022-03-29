package test.model;

import test.model.domain.Address;
import test.model.domain.query.QAddress;

import java.util.Optional;

public interface RepoInterfaceWithDefaultMethod {

  default Optional<Address> findOneById(int id) {
    return new QAddress().id.eq(id).findOneOrEmpty();
  }
}
