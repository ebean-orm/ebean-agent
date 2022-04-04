package io.ebean.example;

public final class EbiReadOnly implements EbiInterface {

  public EbiReadOnly(Object owner) {

  }

  @Override
  public void preGetId() {

  }

  @Override
  public void preSetter(boolean b, int i, Long id, Long newValue) {

  }

  @Override
  public boolean isEmbeddedNewOrDirty(Object foo) {
    return false;
  }
}
