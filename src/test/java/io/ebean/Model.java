package io.ebean;

/**
 * Simulating the new Model with _$targetDatabase field.
 */
public abstract class Model {

  protected final String _$targetDatabase;

  protected Model() {
    this(null);
  }

  protected Model(String dbName) {
    _$targetDatabase = dbName;
  }

  public Database db() {
    return DB.byName(_$targetDatabase);
  }
}
