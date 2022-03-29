package test.model.domain.finder;

import test.model.domain.query.QAddress;
import test.model.domain.query.QOrder;

public class Finder {


  public QAddress simpleFind() {

    QAddress query = new QAddress();

    query.version.lessThan(3);
    query.city.like("abc");
    query.country.code.equalTo("NZ");

    //query._version().lessThan(3);

    return query;
  }

  public QOrder orderQuery() {

    QOrder qOrder = new QOrder();
    qOrder.id.greaterThan(1);
    qOrder.details.product.id.equalTo(12);

    return qOrder;
  }
}
