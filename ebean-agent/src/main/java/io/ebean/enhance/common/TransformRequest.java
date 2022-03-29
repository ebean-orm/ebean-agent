package io.ebean.enhance.common;

public class TransformRequest {

  private final String className;
  private boolean enhancedEntity;
  private boolean enhancedTransactional;
  private boolean enhancedQueryBean;

  private byte[] bytes;

  public TransformRequest(String className, byte[] bytes) {
    this.className = className;
    this.bytes = bytes;
  }

  public String getClassName() {
    return className;
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
