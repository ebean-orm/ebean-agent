package io.ebean.enhance.querybean;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * For combined transformation from two ClassFileTransformers.
 * <p>
 * In practice this is used to perform both the entity bean/transactional enhancement and query bean enhancement.
 * This is really only expected to be used in IDE enhancers so IntelliJ IDEA plugin and Eclipse IDE plugin.
 * </p>
 */
public class CombinedTransform {

  private final ClassFileTransformer first;

  private final ClassFileTransformer second;

  /**
   * Construct with 2 class transformers to combine.
   */
  public CombinedTransform(ClassFileTransformer first, ClassFileTransformer second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Perform the combined enhancement.
   */
  public Response transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] origBytes) throws IllegalClassFormatException {

    byte[] transformed = first.transform(loader, className, classBeingRedefined, protectionDomain, origBytes);

    boolean firstEnhanced = (transformed != null);
    byte[] nextBytes = (transformed != null) ? transformed : origBytes;
    byte[] finalTransformed = second.transform(loader, className, classBeingRedefined, protectionDomain, nextBytes);

    if (finalTransformed != null) {
      return new Response(finalTransformed, firstEnhanced, true);

    } else if (transformed != null) {
      return new Response(transformed, firstEnhanced, false);
    }
    return Response.NOT_TRANSFORMED;
  }

  /**
   * The combined enhancement response.
   */
  public static class Response {

    private static final Response NOT_TRANSFORMED = new Response();

    private final byte[] classBytes;
    private final boolean first;
    private final boolean second;

    private Response() {
      this.classBytes = null;
      this.first = false;
      this.second = false;
    }

    private Response(byte[] classBytes, boolean first, boolean second) {
      this.classBytes = classBytes;
      this.first = first;
      this.second = second;
    }

    /**
     * Return true if enhancement occurred.
     */
    public boolean isEnhanced() {
      return classBytes != null;
    }

    /**
     * Return the enhanced class bytes.
     */
    public byte[] getClassBytes() {
      return classBytes;
    }

    /**
     * Return true if the first enhancement was applied.
     */
    public boolean isFirst() {
      return first;
    }

    /**
     * Return true if the second enhancement was applied.
     */
    public boolean isSecond() {
      return second;
    }
  }
}
