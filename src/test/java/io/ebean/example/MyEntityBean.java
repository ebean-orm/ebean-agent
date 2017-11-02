package io.ebean.example;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import test.model.MyEmbeddedBean;

import javax.persistence.Id;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Prototype bean for ASM bytecode generation.
 */
public class MyEntityBean implements EntityBean {

  EntityBeanIntercept intercept;

  @Id
  Long id;

  MyEmbeddedBean foo;
  MyEmbeddedBean bar;

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
  public String _ebean_getMarker() {
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
