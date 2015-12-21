package test.enhancement;

import org.junit.Assert;
import org.junit.Test;
import test.model.PersistentFile;

/**
 */
public class PersistentFileTests extends BaseTest {


  @Test
  public void testBasic() {

    // make sure we can construct it
    PersistentFile bean = new PersistentFile();
    Assert.assertNotNull(bean);
  }
}
