package io.ebean.example;

public interface EbiInterface {

  void preGetId();

  void preSetter(boolean b, int i, Long id, Long newValue);

  boolean isEmbeddedNewOrDirty(Object foo);
}
