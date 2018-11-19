package test.enhancement;

import org.testng.annotations.Test;
import test.model.domain.Country;

import java.util.List;

/**
 * Execute finder methods.
 *
 * Internally in Ebean there can be ProfileLocation set for the query execution metrics.
 */
public class CountryTest extends BaseTest {

  @Test
  public void finderTest() {

    Country got = Country.find.byCode("NZ");
    System.out.println("done" + got);
  }

  @Test
  public void finderOrmQuery() {

    List<Country> countries = Country.find.byCodeLike("N");
    System.out.println("done" + countries);
  }

  @Test
  public void finderNativeSql() {

    List<String> countryNames = Country.find.byName();
    System.out.println("done" + countryNames);
  }

}
