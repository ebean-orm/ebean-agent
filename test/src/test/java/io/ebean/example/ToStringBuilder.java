package io.ebean.example;

import java.util.Collection;
import java.util.IdentityHashMap;

public final class ToStringBuilder {

  private static final int MAX = 100;

  private final IdentityHashMap<Object, Integer> id = new IdentityHashMap<>();
  private final StringBuilder sb = new StringBuilder(50);
  private boolean first = true;
  private int counter;

  @Override
  public String toString() {
    return sb.toString();
  }

  public void start(Object bean) {
    if (counter <= MAX) {
      sb.append(bean.getClass().getSimpleName())
        .append("@").append(counter)
        .append("(");
    }
  }

  public void add(String name, Object value) {
    if (value != null && counter <= MAX) {
      key(name);
      value(value);
    }
  }

  public void end() {
    if (counter <= MAX) {
      sb.append(")");
    }
  }

  private void key(String name) {
    if (counter > MAX) {
      return;
    }
    if (first) {
      first = false;
    } else {
      sb.append(", ");
    }
    sb.append(name).append(": ");
  }

  private void value(Object value) {
    if (counter > MAX) {
      return;
    }
    if (value instanceof EbString) {
      if (push(value)) {
        ((EbString)value).toString(this);
      }
    } else if (value instanceof Collection){
      Collection<?> c = (Collection<?>)value;
      int collectionPos = 0;
      sb.append("[");
      for (Object o : c) {
        if (collectionPos++ > 0) {
          sb.append(", ");
        }
        value(o);
        if (counter > MAX) {
          return;
        }
      }
      sb.append("]");
    } else {
      sb.append(value);
    }
  }

  private boolean push(Object bean) {
    if (counter > MAX) {
      return false;
    }
    if (counter == MAX) {
      sb.append(" ...");
      counter++;
      return false;
    }
    Integer idx = id.putIfAbsent(bean, counter++);
    if (idx != null) {
      --counter;
      sb.append(bean.getClass().getSimpleName()).append("@").append(idx);
      return false;
    }
    first = true;
    return true;
  }

}
