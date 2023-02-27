package io.ebean.enhance.querybean;

import io.ebean.enhance.common.EnhanceConstants;

/**
 * Set of most interesting constants used by the entity.
 */
interface Constants {

  /**
   * Annotation used to mark beans that are already enhanced.
   */
  String ANNOTATION_ALREADY_ENHANCED_MARKER = "Lio/ebean/typequery/AlreadyEnhancedMarker;";

  /**
   * The TypeQueryBean annotation.
   */
  String ANNOTATION_TYPE_QUERY_BEAN = EnhanceConstants.TYPEQUERYBEAN_ANNOTATION;

  /**
   * The TQRootBean object class name.
   */
  String TQ_ROOT_BEAN = "io/ebean/typequery/TQRootBean";

  String ASSOC_BEAN_BASIC_CONSTRUCTOR_DESC = "(Ljava/lang/String;Ljava/lang/Object;I)V";

  String ASSOC_BEAN_MAIN_CONSTRUCTOR_DESC = "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;I)V";

  String ASSOC_BEAN_BASIC_SIG = "(Ljava/lang/String;TR;I)V";

  String ASSOC_BEAN_MAIN_SIG = "(Ljava/lang/String;TR;Ljava/lang/String;I)V";

  /**
   * The name field inherited that effectively holds the path for other properties to create from.
   */
  String FIELD_PATH = "_name";

  /**
   * The root object field inherited.
   */
  String FIELD_ROOT = "_root";

  /**
   * EbeanServer as constructor argument.
   */
  String WITH_EBEANSERVER_ARGUMENT = "(Lio/ebean/EbeanServer;)V";
  String WITH_DATABASE_ARGUMENT = "(Lio/ebean/Database;)V";

  String SET_LABEL = "setLabel";
}
