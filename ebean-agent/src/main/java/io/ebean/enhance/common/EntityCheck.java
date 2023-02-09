package io.ebean.enhance.common;

import java.util.Set;

/**
 * Helper to check for entity annotations on a class.
 */
class EntityCheck {

  /**
  * A class with one of these annotations is enhanced as an "entity".
  */
  private static final String[] entityAnnotations = {
      EnhanceConstants.Javax.Entity,
      EnhanceConstants.Javax.Embeddable,
      EnhanceConstants.Javax.MappedSuperclass,
      EnhanceConstants.DOCSTORE_ANNOTATION
  };

  /**
  * Return true if the annotation is for an Entity, Embeddable, MappedSuperclass or DocStore.
  */
  static boolean isEntityAnnotation(String desc) {
    if (!desc.startsWith(EnhanceConstants.Javax.PERSISTENCE)) {
      return desc.equals(EnhanceConstants.DOCSTORE_ANNOTATION);
    }
    return desc.equals(EnhanceConstants.Javax.Entity)
      || desc.equals(EnhanceConstants.Javax.Embeddable)
      || desc.equals(EnhanceConstants.Javax.MappedSuperclass);
  }

  /**
  * Return true if the class annotations contains one of the entity annotations.
  */
  public static boolean hasEntityAnnotation(Set<String> classAnnotations) {
    for (String entityAnnotation : entityAnnotations) {
      if (classAnnotations.contains(entityAnnotation)) {
        return true;
      }
    }
    return false;
  }
}
