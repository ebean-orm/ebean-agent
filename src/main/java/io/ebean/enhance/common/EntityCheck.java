package io.ebean.enhance.common;

import java.util.Set;

/**
 * Helper to check for entity annotations on a class.
 */
public class EntityCheck {

  /**
  * A class with one of these annotations is enhanced as an "entity".
  */
  private static String[] entityAnnotations = {
      EnhanceConstants.ENTITY_ANNOTATION,
      EnhanceConstants.EMBEDDABLE_ANNOTATION,
      EnhanceConstants.MAPPEDSUPERCLASS_ANNOTATION,
      EnhanceConstants.DOCSTORE_ANNOTATION
  };

  /**
  * Return true if the annotation is for an Entity, Embeddable, MappedSuperclass or DocStore.
  */
  public static boolean isEntityAnnotation(String desc) {

    if (!desc.startsWith(EnhanceConstants.JAVAX_PERSISTENCE)) {
      return desc.equals(EnhanceConstants.DOCSTORE_ANNOTATION);
    }
    if (desc.equals(EnhanceConstants.ENTITY_ANNOTATION)) {
      return true;
    } else if (desc.equals(EnhanceConstants.EMBEDDABLE_ANNOTATION)) {
      return true;
    } else if (desc.equals(EnhanceConstants.MAPPEDSUPERCLASS_ANNOTATION)) {
      return true;
    }
    return false;
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
