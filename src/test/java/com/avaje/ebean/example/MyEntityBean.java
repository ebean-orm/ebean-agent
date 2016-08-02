package com.avaje.ebean.example;

import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebean.bean.EntityBeanIntercept;

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

  public Long _ebean_get_id() {
    intercept.preGetId();
    return id;
  }

  public void _ebean_set_id(Long newValue) {
    PropertyChangeEvent evt = intercept.preSetter(true, 1, id, newValue);
    this.id = newValue;
    intercept.postSetter(evt);
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
  public void addPropertyChangeListener(PropertyChangeListener listener) {

  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {

  }

  @Override
  public void _ebean_setEmbeddedLoaded() {

  }

  @Override
  public boolean _ebean_isEmbeddedNewOrDirty() {
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
