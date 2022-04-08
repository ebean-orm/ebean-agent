package test.model;

import io.ebean.example.MyEntityBean3;
import org.junit.jupiter.api.Test;
import test.enhancement.BaseTest;

import java.util.ArrayList;
import java.util.List;

public class SomeBeanWithEmbeddableTest extends BaseTest {

  @Test
  void to_string() {
    MyEmbeddedBean foo = new MyEmbeddedBean();
    foo.setName("f00");
    foo.setDesc("desc");

    MyEmbeddedBean bar = new MyEmbeddedBean();
    bar.setName("b00");
    bar.setDesc(null);

    MyEntityBean3 b = new MyEntityBean3();
    b.setId(54);
    b.setName("asdasd");
    b.setFoo(foo);
    b.setBar(bar);
    b.setChildren(childasd());

    String s = b.toString();
    System.out.println(s);
  }

  private List<MyEntityBean3> childasd() {
    List<MyEntityBean3> list = new ArrayList<>();
    for (int i = 0; i <10; i++) {
      MyEntityBean3 b = new MyEntityBean3();
      b.setId(i+10);
      b.setName("child"+i);
      list.add(b);
    }
    return list;
  }

  @Test
  public void create() {

    SomeBeanWithEmbeddable bean = new SomeBeanWithEmbeddable();
    bean.setOne(new MyEmbeddedBean());
    bean.setTwo(new MyEmbeddedBean());
    bean.getOne().setName("foo");

  }
}
