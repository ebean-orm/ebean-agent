package io.ebean.enhance.agent;

public class TransformRequest {

  boolean enhancedEntity;
  boolean enhancedTransactional;
  boolean enhancedQueryBean;

  byte[] bytes;

  TransformRequest(byte[] bytes) {
    this.bytes = bytes;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void enhancedEntity(byte[] bytes) {
    this.enhancedEntity = true;
    this.bytes = bytes;
  }

  public boolean isEnhancedEntity() {
    return enhancedEntity;
  }

  public void enhancedTransactional(byte[] bytes) {
    this.enhancedTransactional = true;
    this.bytes = bytes;
  }

  public boolean isEnhanced() {
    return enhancedTransactional || enhancedQueryBean;
  }

  public void enhancedQueryBean(byte[] bytes) {
    this.enhancedQueryBean = true;
    this.bytes = bytes;
  }
}
