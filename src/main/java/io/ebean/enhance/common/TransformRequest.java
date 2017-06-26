package io.ebean.enhance.common;

public class TransformRequest {

  boolean enhancedEntity;
  boolean enhancedTransactional;
  boolean enhancedQueryBean;

  byte[] bytes;

  public TransformRequest(byte[] bytes) {
    this.bytes = bytes;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void enhancedEntity(byte[] bytes) {
    this.enhancedEntity = true;
    this.bytes = bytes;
  }

  public void enhancedTransactional(byte[] bytes) {
    this.enhancedTransactional = true;
    this.bytes = bytes;
  }

  public boolean isEnhanced() {
    return enhancedTransactional || enhancedQueryBean || enhancedEntity;
  }

  public void enhancedQueryBean(byte[] bytes) {
    this.enhancedQueryBean = true;
    this.bytes = bytes;
  }
}
