package io.ebean.enhance.common;

/**
 * Constant values used in byte code generation.
 */
public interface EnhanceConstants {

  String AVAJE_TRANSACTIONAL_ANNOTATION = "Lio/ebean/annotation/Transactional;";

  String JAVAX_PERSISTENCE = "Ljavax/persistence/";

  String ENTITY_ANNOTATION = "Ljavax/persistence/Entity;";

  String DOCSTORE_ANNOTATION = "Lio/ebean/annotation/DocStore;";

  String EMBEDDABLE_ANNOTATION = "Ljavax/persistence/Embeddable;";

  String MAPPEDSUPERCLASS_ANNOTATION = "Ljavax/persistence/MappedSuperclass;";

  String IDENTITY_FIELD = "_ebean_identity";

  String INTERCEPT_FIELD = "_ebean_intercept";

  String C_ENHANCEDTRANSACTIONAL = "io/ebean/bean/EnhancedTransactional";

  String C_ENTITYBEAN = "io/ebean/bean/EntityBean";

  String C_SCALAOBJECT = "scala/ScalaObject";

  String C_GROOVYOBJECT = "groovy/lang/GroovyObject";

  String C_INTERCEPT = "io/ebean/bean/EntityBeanIntercept";

  String C_BEANCOLLECTION = "io/ebean/bean/BeanCollection";

  String L_INTERCEPT = "Lio/ebean/bean/EntityBeanIntercept;";

  String L_SCOPETRANS = "Lio/ebeaninternal/api/ScopeTrans;";

  String L_HELPSCOPETRANS = "Lio/ebeaninternal/api/HelpScopeTrans;";

  String L_DRAFT = "Lio/ebean/annotation/Draft;";

  String C_TXSCOPE = "io/ebean/TxScope";

  String C_TXTYPE = "io/ebean/annotation/TxType";

  String C_TXISOLATION = "io/ebean/annotation/TxIsolation";

  String C_PERSISTBATCH = "io/ebean/annotation/PersistBatch";

  String BEANLIST = "io/ebean/common/BeanList";

  String BEANSET = "io/ebean/common/BeanSet";

  String BEANMAP = "io/ebean/common/BeanMap";

  String L_JETBRAINS_NOTNULL = "Lorg/jetbrains/annotations/NotNull;";

  String L_EBEAN_NOTNULL = "Lio/ebean/annotation/NotNull;";
}
