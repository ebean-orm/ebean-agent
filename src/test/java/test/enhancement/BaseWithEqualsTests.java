package test.enhancement;

import org.testng.Assert;
import org.testng.annotations.Test;

import test.model.AExtends;
import test.model.BExtends;

public class BaseWithEqualsTests {

  @Test
  public void test() {
    
    AExtends aExtends = new AExtends();
    boolean isEqual = aExtends.equals(new Object());
    
    Assert.assertTrue(isEqual);
    Assert.assertEquals("1", aExtends.toString());
    
  }
  
  
  @Test
  public void testWhereBaseHasEqualsAndSubtypeHasId() {
    
    BExtends bExtends = new BExtends();
    boolean isEqual = bExtends.equals(new Object());
    
    Assert.assertTrue(isEqual);
    Assert.assertEquals("1", bExtends.toString());
    
  }
}
