package test.model.domain.finder;

import test.model.domain.Country;

import java.util.List;

public class CountryFinder extends io.ebean.Finder<String,Country> {

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
}
