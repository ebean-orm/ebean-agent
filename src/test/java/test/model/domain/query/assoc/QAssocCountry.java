package test.model.domain.query.assoc;

import io.ebean.typequery.PString;
import io.ebean.typequery.TQAssocBean;
import io.ebean.typequery.TypeQueryBean;
import test.model.domain.Country;

@TypeQueryBean
public class QAssocCountry<R> extends TQAssocBean<Country,R> {

  public PString<R> code;
  public PString<R> name;

//  public QAssocCountry(String name, R root) {
//    this(name, root, null);
//  }
//  public QAssocCountry(String name, R root, String prefix) {
//    String path = TQPath.add(prefix, name);
//    this.code = new PString<R>("code", root, path);
//    this.name = new PString<R>("name", root, path);
//  }
//
  public QAssocCountry(String name, R root, int depth) {
    //this(name, root, null, depth);
    super(name, root, null);
  }

  public QAssocCountry(String name, R root, String prefix, int depth) {
    super(name, root, prefix);
    //String path = TQPath.add(prefix, name);
    //this.code = new PString<R>("code", _root, _name);
    //this.name = new PString<R>("name", _root, _name);
  }
}
