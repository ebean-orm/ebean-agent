package io.ebean.example;

import io.ebean.Model;
import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import test.model.MyEmbeddedBean;

import javax.persistence.Id;
import java.util.List;

public class MyEntityBean3 extends Model implements EntityBean, EbString {

  EbiInterface intercept;

  @Id
  long id;
  String name;

  MyEmbeddedBean foo;
  MyEmbeddedBean bar;
  List<MyEntityBean3> children;

  public Object _ebean_newInstanceReadOnly() {
    return new MyEntityBean3(null);
  }

  protected MyEntityBean3(EntityBean ignore) {
    intercept = new EbiReadOnly(this);
  }

  public MyEntityBean3() {
    intercept = new EbiReadWrite(this);
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MyEmbeddedBean getFoo() {
    return foo;
  }

  public void setFoo(MyEmbeddedBean foo) {
    this.foo = foo;
  }

  public MyEmbeddedBean getBar() {
    return bar;
  }

  public void setBar(MyEmbeddedBean bar) {
    this.bar = bar;
  }

  public List<MyEntityBean3> getChildren() {
    return children;
  }

  public void setChildren(List<MyEntityBean3> children) {
    this.children = children;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder();
    toString(builder);
    return builder.toString();
  }

  public void toString(ToStringBuilder sb) {
    sb.start(this);
    sb.add("id", id);
    sb.add("name", name);
    sb.add("foo", foo);
    sb.add("bar1", bar);
    sb.add("bar2", bar);
    sb.add("children", children);
    sb.end();
  }

  Long getId() {
    return id;
  }

  public Long _ebean_get_id() {
    intercept.preGetId();
    return id;
  }

  public void _ebean_set_id(Long newValue) {
    intercept.preSetter(true, 1, id, newValue);
    this.id = newValue;
  }

  @Override
  public String[] _ebean_getPropertyNames() {
    return new String[0];
  }

  @Override
  public String _ebean_getPropertyName(int pos) {
    return null;
  }

  @Override
  public Object _ebean_newInstance() {
    return null;
  }

  @Override
  public void _ebean_setEmbeddedLoaded() {

  }

  @Override
  public boolean _ebean_isEmbeddedNewOrDirty() {
    // for each embedded bean field...
    if (intercept.isEmbeddedNewOrDirty(foo)) return true;
    if (intercept.isEmbeddedNewOrDirty(bar)) return true;

    return false;
 }

  @Override
  public EntityBeanIntercept _ebean_getIntercept() {
    return null;
  }

  @Override
  public EntityBeanIntercept _ebean_intercept() {
    return null;
  }

  @Override
  public void _ebean_setField(int fieldIndex, Object value) {

    switch (fieldIndex) {
      case 0:
        _ebean_set_id((Long)value);
        break;
      case 1:
        _ebean_set_id((Long)value);
      default:
        throw new RuntimeException("asd");
    }
  }

  Object _ebean_identity;

  public Object _ebean_getIdentity() {
    synchronized (this) {
      if (_ebean_identity != null) {
        return _ebean_identity;
      }

      Object id = getId();
      if (id != null) {
        _ebean_identity = id;
      } else {
        _ebean_identity = new Object();
      }

      return _ebean_identity;
    }
  }

  long intId;

  public long getIntId() {
    return intId;
  }

  public Object _ebean_getIdentity_primative() {
    synchronized (this) {
      if (_ebean_identity != null) {
        return _ebean_identity;
      }

      if (getIntId() != 0) {
        _ebean_identity = Long.valueOf(getIntId());
      } else {
        _ebean_identity = new Object();
      }

      return _ebean_identity;
    }
  }

  @Override
  public void _ebean_setFieldIntercept(int fieldIndex, Object value) {

  }

  @Override
  public Object _ebean_getField(int fieldIndex) {
    return null;
  }

  @Override
  public Object _ebean_getFieldIntercept(int fieldIndex) {
    return null;
  }
}
