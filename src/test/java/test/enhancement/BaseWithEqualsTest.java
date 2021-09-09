package test.enhancement;

import org.junit.jupiter.api.Test;
import test.model.AExtends;
import test.model.BExtends;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseWithEqualsTest {

  @Test
  public void test() {

    AExtends aExtends = new AExtends();
    boolean isEqual = aExtends.equals(new Object());

    assertTrue(isEqual);
    assertEquals("1", aExtends.toString());

  }


  @Test
  public void testWhereBaseHasEqualsAndSubtypeHasId() {

    BExtends bExtends = new BExtends();
    boolean isEqual = bExtends.equals(new Object());

    assertTrue(isEqual);
    assertEquals("1", bExtends.toString());

  }
}
