package io.ebean.enhance.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Collects the annotation information.
 */
public class AnnotationInfo {

  private final HashMap<String,Object> valueMap = new HashMap<>();

  private AnnotationInfo parent;

  /**
  * The parent is typically the class level annotation information
  * which could be considered to hold default values.
  */
  public AnnotationInfo(AnnotationInfo parent){
    this.parent = parent;
  }

  @Override
  public String toString() {
    return valueMap.toString();
  }

  public void setParent(AnnotationInfo parent) {
    this.parent = parent;
  }

  /**
   * Gets or creates a list for the given prefix, it will hold the array values.
   */
  @SuppressWarnings("unchecked")
  public List<Object> getArrayEntry(String prefix) {
    return (List<Object>) valueMap.computeIfAbsent(prefix, k -> new ArrayList<>());
  }

  /**
  * Add a annotation value.
  */
  public void add(String prefix, String name, Object value){
    if (name == null){
      // this is an array value...
      getArrayEntry(prefix).add(value);
    } else {
      String key = getKey(prefix, name);
      valueMap.put(key, value);
    }
  }

  /**
  * Add a enum annotation value.
  */
  void addEnum(String prefix, String name, String value){
    add(prefix, name, value);
  }

  private String getKey(String prefix, String name){
    if (prefix == null){
      return name;
    } else {
      return prefix+"."+name;
    }
  }

  /**
  * Return a value out of the map.
  */
  public Object getValue(String key){
    Object o = valueMap.get(key);
    if (o == null && parent != null){
      // try getting value from parent
      o = parent.getValue(key);
    }
    return o;
  }
}
