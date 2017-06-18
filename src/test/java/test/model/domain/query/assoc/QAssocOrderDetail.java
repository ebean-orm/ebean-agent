package test.model.domain.query.assoc;

import io.ebean.typequery.PDouble;
import io.ebean.typequery.PInteger;
import io.ebean.typequery.PLong;
import io.ebean.typequery.PTimestamp;
import io.ebean.typequery.TQAssocBean;
import io.ebean.typequery.TypeQueryBean;
import test.model.domain.OrderDetail;

@TypeQueryBean
public class QAssocOrderDetail<R> extends TQAssocBean<OrderDetail,R> {

  public PLong<R> id;
  public PLong<R> version;
  public PTimestamp<R> whenCreated;
  public PTimestamp<R> whenUpdated;
  public PInteger<R> orderQty;
  public PInteger<R> shipQty;
  public PDouble<R> unitPrice;
  public QAssocProduct<R> product;

  public QAssocOrderDetail(String name, R root) {
    super(name, root);
  }

////  private String _path;
////  private R _root;
//  public QAssocOrderDetail(String name, R root, int depth) {
//    this(name, root, null, depth);
//  }
//  public QAssocOrderDetail(String name, R root, String prefix, int depth) {
//    String path = TQPath.add(prefix, name);
//    //_root = root;
//    this.id = new PLong<R>("id", root, path);
//    this.version = new PLong<R>("version", root, path);
//    this.whenCreated = new PTimestamp<R>("whenCreated", root, path);
//    this.whenUpdated = new PTimestamp<R>("whenUpdated", root, path);
//    this.orderQty = new PInteger<R>("orderQty", root, path);
//    this.shipQty = new PInteger<R>("shipQty", root, path);
//    this.unitPrice = new PDouble<R>("unitPrice", root, path);
//    if (--depth > 0) {
//      //this.product = new QAssocProduct<R>("product", root, path, depth);
//    }
//  }

//  public PDouble<R> _unitPrice() {
//    if (unitPrice == null) {
//      unitPrice = new PDouble<R>("unitPrice", _root, _path);
//    }
//    return unitPrice;
//  }
//
  public QAssocProduct<R> _otherProduct() {
    if (product == null) {
      product = new QAssocProduct<R>("product", _root, _name, 1);
    }
    return product;
  }
}
