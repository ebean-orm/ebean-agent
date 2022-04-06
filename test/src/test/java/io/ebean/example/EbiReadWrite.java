package io.ebean.example;

public final class EbiReadWrite implements EbiInterface {

  public EbiReadWrite(Object owner) {

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
