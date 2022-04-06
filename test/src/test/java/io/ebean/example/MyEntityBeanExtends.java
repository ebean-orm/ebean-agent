package io.ebean.example;

import io.ebean.bean.EntityBean;

public class MyEntityBeanExtends extends MyEntityBean2 {

  public Object _ebean_newInstanceReadOnly() {
    return new MyEntityBeanExtends(null);
  }

  protected MyEntityBeanExtends(EntityBean ignore) {
    super(ignore);
  }
}
