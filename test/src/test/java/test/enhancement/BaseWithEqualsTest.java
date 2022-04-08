package test.enhancement;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import test.model.AExtends;
import test.model.BExtends;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseWithEqualsTest {

  @Test
  void test() {
    AExtends aExtends = new AExtends();
    boolean isEqual = aExtends.equals(new Object());

    assertTrue(isEqual);
    //assertEquals("AExtends@0()", aExtends.toString());
  }

  @Test
  void testWhereBaseHasEqualsAndSubtypeHasId() {
    BExtends bExtends = new BExtends();
    boolean isEqual = bExtends.equals(new Object());

    assertTrue(isEqual);
    //assertEquals("BExtends@0()", bExtends.toString());
  }

  @Disabled
  @Test
  void test_toString() {
    AExtends aExtends = new AExtends();
    aExtends.setName("foo");

    assertEquals("AExtends@0(name:foo)", aExtends.toString());
  }

  @Disabled
  @Test
  void test_toString2() {
    AExtends aExtends = new AExtends();
    aExtends.setName("foo");
    aExtends.setId(45L);
    aExtends.setVersion(3L);

    assertEquals("AExtends@0(id:45, name:foo, version:3)", aExtends.toString());
  }
}
