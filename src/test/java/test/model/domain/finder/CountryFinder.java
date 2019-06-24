package test.model.domain.finder;

import io.ebean.ProfileLocation;
import test.model.domain.Address;
import test.model.domain.Country;
import test.model.domain.query.QAddress;

import java.util.List;

public class CountryFinder extends io.ebean.Finder<String,Country> {

  private static ProfileLocation _profLoc0;

  static void _$initProfileLocations() {
    _profLoc0 = ProfileLocation.create();
  }

  public CountryFinder() {
    super(Country.class);
  }

  public Country byCode(String code) {
    return query().where()
        .eq("code", code)
        .findOne();
  }

  public List<String> byName() {
    return nativeSql("select name from o_country")
        .findSingleAttributeList();
  }

  public List<Country> byCodeLike(String code) {
    return query("where code startsWith :code")
        .setParameter("code", code)
        .findList();
  }

  public List<Address> example() {

    return new QAddress()
      .setProfileLocation(_profLoc0)
      .city.eq("Auckland")
      .findList();
  }
}
