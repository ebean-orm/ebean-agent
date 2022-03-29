package test.enhancement;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import test.model.PersistentFile;

public class MockEntityBeanTest extends BaseTest {

  @Test
  public void mockitoMock() {

    final PersistentFile mock = Mockito.mock(PersistentFile.class);
    mock.setName("asd");

    final PersistentFile spy = Mockito.spy(PersistentFile.class);
    spy.setName("asd");

    Mockito.verify(spy).setName("asd");
  }
}
