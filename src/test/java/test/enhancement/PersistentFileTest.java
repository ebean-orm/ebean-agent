package test.enhancement;

import org.testng.annotations.Test;
import test.model.PersistentFile;

import static org.testng.Assert.assertNotNull;

/**
 */
public class PersistentFileTest extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    PersistentFile bean = new PersistentFile();
    assertNotNull(bean);
  }
}
