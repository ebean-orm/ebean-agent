package io.ebean.enhance.common;

/**
 * Constant values used in byte code generation.
 */
public interface EnhanceConstants {

  interface Javax {
    String PERSISTENCE = "Ljavax/persistence/";
    String Entity = "Ljavax/persistence/Entity;";
    String Embeddable = "Ljavax/persistence/Embeddable;";
    String MappedSuperclass = "Ljavax/persistence/MappedSuperclass;";
    String Column = "Ljavax/persistence/Column;";
    String Transient = "Ljavax/persistence/Transient;";
    String Id = "Ljavax/persistence/Id;";
    String EmbeddedId = "Ljavax/persistence/EmbeddedId;";
    String OneToOne = "Ljavax/persistence/OneToOne;";
    String ManyToOne = "Ljavax/persistence/ManyToOne;";
    String OneToMany = "Ljavax/persistence/OneToMany;";
    String ManyToMany = "Ljavax/persistence/ManyToMany;";
    String Version = "Ljavax/persistence/Version;";
    String Embedded = "Ljavax/persistence/Embedded;";
    String OrderColumn = "Ljavax/persistence/OrderColumn;";
  }
  interface Jakarta {
    String Entity = "Ljakarta/persistence/Entity;";
    String Embeddable = "Ljakarta/persistence/Embeddable;";
    String MappedSuperclass = "Ljakarta/persistence/MappedSuperclass;";
    String Column = "Ljakarta/persistence/Column;";
    String Transient = "Ljakarta/persistence/Transient;";
    String Id = "Ljakarta/persistence/Id;";
    String EmbeddedId = "Ljakarta/persistence/EmbeddedId;";
    String OneToOne = "Ljakarta/persistence/OneToOne;";
    String ManyToOne = "Ljakarta/persistence/ManyToOne;";
    String OneToMany = "Ljakarta/persistence/OneToMany;";
    String ManyToMany = "Ljakarta/persistence/ManyToMany;";
    String Version = "Ljakarta/persistence/Version;";
    String Embedded = "Ljakarta/persistence/Embedded;";
    String OrderColumn = "Ljakarta/persistence/OrderColumn;";
  }
  String INIT = "<init>";
  String CLINIT = "<clinit>";
  String NOARG_VOID = "()V";
  String MOCKITO_MOCK = "$MockitoMock$";

  String TRANSACTIONAL_ANNOTATION = "Lio/ebean/annotation/Transactional;";
  String TYPEQUERYBEAN_ANNOTATION = "Lio/ebean/typequery/TypeQueryBean;";
  String DOCSTORE_ANNOTATION = "Lio/ebean/annotation/DocStore;";

  String IDENTITY_FIELD = "_ebean_identity";
  String INTERCEPT_FIELD = "_ebean_intercept";
  String C_OBJECT = "java/lang/Object";
  String C_MODEL = "io/ebean/Model";
  String C_ENHANCEDTRANSACTIONAL = "io/ebean/bean/EnhancedTransactional";
  String C_ENTITYBEAN = "io/ebean/bean/EntityBean";
  String C_SCALAOBJECT = "scala/ScalaObject";
  String C_GROOVYOBJECT = "groovy/lang/GroovyObject";
  String C_RECORDTYPE = "java/lang/Record";
  String C_INTERCEPT_I = "io/ebean/bean/EntityBeanIntercept";
  String C_INTERCEPT_RW = "io/ebean/bean/InterceptReadWrite";
  String C_INTERCEPT_RO = "io/ebean/bean/InterceptReadOnly";
  String C_BEANCOLLECTION = "io/ebean/bean/BeanCollection";
  String C_TOSTRINGBUILDER = "io/ebean/bean/ToStringBuilder";

  String L_STRING = "Ljava/lang/String;";
  String L_OBJECT = "Ljava/lang/Object;";
  String L_INTERCEPT = "Lio/ebean/bean/EntityBeanIntercept;";
  String L_HELPSCOPETRANS = "Lio/ebeaninternal/api/HelpScopeTrans;";
  String L_DRAFT = "Lio/ebean/annotation/Draft;";
  String C_TXSCOPE = "io/ebean/TxScope";
  String C_TXTYPE = "io/ebean/annotation/TxType";
  String C_TXISOLATION = "io/ebean/annotation/TxIsolation";
  String C_PERSISTBATCH = "io/ebean/annotation/PersistBatch";
  String C_TXOPTION = "io/ebean/annotation/TxOption";
  String BEANLIST = "io/ebean/common/BeanList";
  String BEANSET = "io/ebean/common/BeanSet";
  String BEANMAP = "io/ebean/common/BeanMap";
  String ARRAYLIST = "java/util/ArrayList";
  String LINKEDHASHSET = "java/util/LinkedHashSet";
  String LINKEDHASHMAP = "java/util/LinkedHashMap";
  String L_JETBRAINS_NOTNULL = "Lorg/jetbrains/annotations/NotNull;";
  String L_EBEAN_NOTNULL = "Lio/ebean/annotation/NotNull;";
  String L_DBARRAY = "Lio/ebean/annotation/DbArray;";
}
