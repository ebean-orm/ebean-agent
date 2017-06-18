package test.model.domain.query.assoc;

import io.ebean.typequery.PJodaDateTime;
import io.ebean.typequery.PLong;
import io.ebean.typequery.PMonth;
import io.ebean.typequery.PString;
import io.ebean.typequery.PTimestamp;
import io.ebean.typequery.TQAssocBean;
import io.ebean.typequery.TQPath;
import io.ebean.typequery.TypeQueryBean;
import test.model.domain.Product;

@TypeQueryBean
public class QAssocProduct<R> extends TQAssocBean<Product,R> {

  public PLong<R> id;
  public PLong<R> version;
  public PTimestamp<R> whenCreated;
  public PTimestamp<R> whenUpdated;
  public PString<R> sku;
  public PString<R> name;
  public PJodaDateTime<R> jdDateTime;
  public PMonth<R> month;

  public QAssocProduct(String name, R root, int depth) {
    this(name, root, null, depth);
  }

  public QAssocProduct(String name, R root, String prefix, int depth) {
    super(name, root, prefix);
    String path = TQPath.add(prefix, name);
    this.id = new PLong<R>("id", root, path);
    this.version = new PLong<R>("version", root, path);
    this.whenCreated = new PTimestamp<R>("whenCreated", root, path);
    this.whenUpdated = new PTimestamp<R>("whenUpdated", root, path);
    this.sku = new PString<R>("sku", root, path);
    this.name = new PString<R>("name", root, path);
    this.jdDateTime = new PJodaDateTime<R>("jdDateTime", root, path);
    this.month = new PMonth<R>("month", root, path);
  }
}
