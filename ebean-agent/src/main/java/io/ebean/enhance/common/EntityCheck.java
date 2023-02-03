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
      EnhanceConstants.JX_ENTITY_ANNOTATION,
      EnhanceConstants.JX_EMBEDDABLE_ANNOTATION,
      EnhanceConstants.JX_MAPPEDSUPERCLASS_ANNOTATION,
      EnhanceConstants.JK_ENTITY_ANNOTATION,
      EnhanceConstants.JK_EMBEDDABLE_ANNOTATION,
      EnhanceConstants.JK_MAPPEDSUPERCLASS_ANNOTATION,
      EnhanceConstants.DOCSTORE_ANNOTATION
  };

  /**
  * Return true if the annotation is for an Entity, Embeddable, MappedSuperclass or DocStore.
  */
  static boolean isEntityAnnotation(String desc) {
    if (!desc.startsWith(EnhanceConstants.JX_JAVAX_PERSISTENCE) && !desc.startsWith(EnhanceConstants.JK_JAVAX_PERSISTENCE)) {
      return desc.equals(EnhanceConstants.DOCSTORE_ANNOTATION);
    }
    return desc.equals(EnhanceConstants.JX_ENTITY_ANNOTATION)
      || desc.equals(EnhanceConstants.JX_EMBEDDABLE_ANNOTATION)
      || desc.equals(EnhanceConstants.JX_MAPPEDSUPERCLASS_ANNOTATION)
      || desc.equals(EnhanceConstants.JK_ENTITY_ANNOTATION)
      || desc.equals(EnhanceConstants.JK_EMBEDDABLE_ANNOTATION)
      || desc.equals(EnhanceConstants.JK_MAPPEDSUPERCLASS_ANNOTATION);
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
