package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.PersistentFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
