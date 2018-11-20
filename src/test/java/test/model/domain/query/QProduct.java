package test.model.domain.query;

import io.ebean.EbeanServer;
import io.ebean.typequery.PLong;
import io.ebean.typequery.PString;
import io.ebean.typequery.PTimestamp;
import io.ebean.typequery.TQRootBean;
import io.ebean.typequery.TypeQueryBean;
import test.model.domain.Product;

@TypeQueryBean
public class QProduct extends TQRootBean<Product,QProduct> {

  public PLong<QProduct> id;
  public PLong<QProduct> version;
  public PTimestamp<QProduct> whenCreated;
  public PTimestamp<QProduct> whenUpdated;
  public PString<QProduct> sku;
  public PString<QProduct> name;

  // minimal code gen
  public QProduct() {
    super(Product.class);
    setRoot(this);
  }

  /**
  * Construct with a given EbeanServer.
  */
  public QProduct(EbeanServer server) {
    super(Product.class, server);
    setRoot(this);
  }

  /**
  * Construct for alias.
  */
  private QProduct(boolean alias) {
    super(alias);
  }

  private static final QProduct _alias = new QProduct(true);

  public static QProduct alias() {
    return _alias;
  }
}
