package io.ebean.enhance.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper to check for entity annotations on a class.
 */
final class EntityCheck {

  /**
   * A class with one of these annotations is enhanced as an "entity".
   */
  private static final String[] entityAnnotations = {
    EnhanceConstants.Javax.Entity, EnhanceConstants.Jakarta.Entity,
    EnhanceConstants.Javax.Embeddable, EnhanceConstants.Jakarta.Embeddable,
    EnhanceConstants.Javax.MappedSuperclass, EnhanceConstants.Jakarta.MappedSuperclass,
    EnhanceConstants.DOCSTORE_ANNOTATION
  };

  private static final Set<String> allEntityTypes = new HashSet<>(Arrays.asList(entityAnnotations));

  /**
   * Return true if the annotation is for an Entity, Embeddable, MappedSuperclass or DocStore.
   */
  static boolean isEntityAnnotation(String desc) {
    return allEntityTypes.contains(desc);
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
