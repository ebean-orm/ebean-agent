package io.ebean.enhance.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.ebean.enhance.common.EnhanceConstants.C_MODEL;
import static io.ebean.enhance.common.EnhanceConstants.C_OBJECT;

/**
 * Cache of ClassMeta.
 *
 * The fallbackCache part is designed to help with incremental compilation by holding
 * a copy of meta data for mapped superclass beans.
 */
public class ClassMetaCache {

  private final Map<String, ClassMeta> cache = new HashMap<>();

  private final Map<String, ClassMeta> fallbackCache = new HashMap<>();

  private int fallbackHits;

  public ClassMetaCache() {
    // add meta for Model (so we never load it)
    cache.put(C_MODEL, new ModelMeta());
  }

  public ClassMeta get(String name) {
    return cache.get(name);
  }

  public void put(String name, ClassMeta meta) {
    cache.put(name, meta);
  }

  public ClassMeta getFallback(String className) {
    ClassMeta meta = fallbackCache.get(className);
    if (meta != null) {
      fallbackHits++;
    }
    return meta;
  }

  public Set<String> fallbackKeys() {
    return fallbackCache.keySet();
  }

  /**
  * Put MappedSuperclass classMeta into the fallback cache.
  */
  public void setFallback() {
    Collection<ClassMeta> values = cache.values();
    for (ClassMeta value : values) {
      if (value.isMappedSuper()) {
        fallbackCache.put(value.getClassName(), value);
      }
    }
    cache.clear();
  }

  public Map<String, ClassMeta> getCache() {
    return cache;
  }

  public Map<String, ClassMeta> getFallbackCache() {
    return fallbackCache;
  }

  public int getFallbackHits() {
    return fallbackHits;
  }

  /**
   * Class meta data for io.ebean.Model.
   */
  static class ModelMeta extends ClassMeta {

    ModelMeta() {
      super(null, 0, null);
      setClassName(C_MODEL, C_OBJECT);
    }

    @Override
    public boolean isCheckSuperClassForEntity() {
      return false;
    }

    @Override
    public boolean isSuperClassEntity() {
      return false;
    }
  }
}
