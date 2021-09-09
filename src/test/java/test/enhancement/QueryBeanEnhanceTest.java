package test.enhancement;


import io.ebean.typequery.PLong;
import org.junit.jupiter.api.Test;
import test.model.domain.query.QAddress;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class QueryBeanEnhanceTest extends BaseTest {

  @Test
  public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    QAddress qAddress = new QAddress();
    PLong<QAddress> version1 = qAddress.version;
    assertNull(version1);

    Class<?>[] cls= {};
    Method method = qAddress.getClass().getMethod("_version", cls);
    Object result = method.invoke(qAddress, (Object[])null);
    assertNotNull(result);

    PLong<QAddress> version2 = qAddress.version;
    assertNotNull(version2);
    assertSame(result, version2);

  }
}
