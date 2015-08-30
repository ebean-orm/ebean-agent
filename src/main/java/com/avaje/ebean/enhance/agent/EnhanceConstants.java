package com.avaje.ebean.enhance.agent;

/**
 * Constant values used in byte code generation.
 */
public interface EnhanceConstants {

  String AVAJE_TRANSACTIONAL_ANNOTATION = "Lcom/avaje/ebean/annotation/Transactional;";

  String ENTITY_ANNOTATION = "Ljavax/persistence/Entity;";

  String EMBEDDABLE_ANNOTATION = "Ljavax/persistence/Embeddable;";

  String MAPPEDSUPERCLASS_ANNOTATION = "Ljavax/persistence/MappedSuperclass;";

  String IDENTITY_FIELD = "_ebean_identity";

  String INTERCEPT_FIELD = "_ebean_intercept";

  String C_ENHANCEDTRANSACTIONAL = "com/avaje/ebean/bean/EnhancedTransactional";

  String C_ENTITYBEAN = "com/avaje/ebean/bean/EntityBean";

  String C_SCALAOBJECT = "scala/ScalaObject";

  String C_GROOVYOBJECT = "groovy/lang/GroovyObject";

  String C_INTERCEPT = "com/avaje/ebean/bean/EntityBeanIntercept";

  String C_BEANCOLLECTION = "com/avaje/ebean/bean/BeanCollection";

  String L_INTERCEPT = "Lcom/avaje/ebean/bean/EntityBeanIntercept;";

  String L_EMBEDDEDCOLUMNS = "Lcom/avaje/ebean/annotation/EmbeddedColumns;";

  String L_SCOPETRANS = "Lcom/avaje/ebeaninternal/api/ScopeTrans;";

  String L_HELPSCOPETRANS = "Lcom/avaje/ebeaninternal/api/HelpScopeTrans;";

  String C_TXTYPE = "com/avaje/ebean/TxType";

  String C_TXSCOPE = "com/avaje/ebean/TxScope";

  String C_TXISOLATION = "com/avaje/ebean/TxIsolation";

  String C_PERSISTBATCH = "com/avaje/ebean/config/PersistBatch";

  String EBEAN_MODEL = "com/avaje/ebean/Model";

  String EBEAN_PREFIX = "com/avaje/ebean";

}
