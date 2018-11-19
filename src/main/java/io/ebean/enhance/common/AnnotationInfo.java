package io.ebean.enhance.common;

import java.util.ArrayList;
import java.util.HashMap;

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


  public AnnotationInfo getParent() {
    return parent;
  }

  public void setParent(AnnotationInfo parent) {
    this.parent = parent;
  }

  /**
  * Add a annotation value.
  */
  @SuppressWarnings("unchecked")
  public void add(String prefix, String name, Object value){
    if (name == null){
      // this is an array value...
      ArrayList<Object> list = (ArrayList<Object>)valueMap.get(prefix);
      if (list == null){
        list = new ArrayList<>();
        valueMap.put(prefix, list);
      }
      //System.out.println("addArrayValue "+prefix+" value:"+value);
      list.add(value);

    } else {
      String key = getKey(prefix, name);
      //System.out.println("addValue "+key+" value:"+value);
      valueMap.put(key, value);
    }
  }

  /**
  * Add a enum annotation value.
  */
  public void addEnum(String prefix, String name, String desc, String value){

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
