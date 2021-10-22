package test.enhancement;

import test.model.MyDbJson;
import test.model.SomeBeanWithDbJson;
import test.normalize.PostJsonGetter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PostJsonGetterTest extends BaseTest {


  @Test
  public void testPostJsonGetter() {
    SomeBeanWithDbJson b = new SomeBeanWithDbJson();
    MyDbJson j = new MyDbJson();
    b.setOne(j);
    b.getOneList().add(j);
    b.getOneList().add(j);
    PostJsonGetter.callbacks.clear();

    b.getOne();
    assertEquals(PostJsonGetter.callbacks.size(), 1);
    assertEquals(PostJsonGetter.callbacks.get(0), "postJsonGet(SomeBeanWithDbJson, MyDbJson, one)");
    b.getOne();
    assertEquals(PostJsonGetter.callbacks.size(), 2);
    assertEquals(PostJsonGetter.callbacks.get(1), "postJsonGet(SomeBeanWithDbJson, MyDbJson, one)");

    b.getOneList();
    assertEquals(PostJsonGetter.callbacks.size(), 3);
    assertEquals(PostJsonGetter.callbacks.get(2), "postJsonGet(SomeBeanWithDbJson, [MyDbJson, MyDbJson], oneList)");

    b.getName();
    assertEquals(PostJsonGetter.callbacks.size(), 3);
  }


}
