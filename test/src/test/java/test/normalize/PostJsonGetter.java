package test.normalize;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to test postJsonGetter feature.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class PostJsonGetter {
  public static List<String> callbacks = new ArrayList<>();

  public static void postJsonGet(Object bean, Object obj, String fieldName) {
    StringBuilder sb = new StringBuilder();
    sb.append("postJsonGet(").append(bean).append(", ").append(obj).append(", ").append(fieldName).append(')');
    callbacks.add(sb.toString());
  }
}
