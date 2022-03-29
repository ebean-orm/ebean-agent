package io.ebean;

/**
 * Simulating the new Model with _$targetDatabase field.
 */
public abstract class Model {

  protected final String _$dbName;

  protected Model() {
    this(null);
  }

  protected Model(String dbName) {
    _$dbName = dbName;
  }

  public Database db() {
    return DB.byName(_$dbName);
  }
}
