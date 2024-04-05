package io.ebean.enhance.querybean;

public final class TypeQueryUtil {

  private static final String TQ_ROOT_BEAN = "io/ebean/typequery/TQRootBean";
  private static final String TQ_ROOT_BEAN2 = "io/ebean/typequery/QueryBean";

  public static boolean isQueryBean(String superName) {
    return TQ_ROOT_BEAN.equals(superName) || TQ_ROOT_BEAN2.equals(superName);
  }
}
