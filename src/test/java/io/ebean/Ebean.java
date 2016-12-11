package io.ebean;

import io.ebean.EbeanServer;

public class Ebean {

  static TDEbeanServer server = new TDEbeanServer();

  public static EbeanServer getDefaultServer() {
    return server;
  }

  public static EbeanServer getServer(String name) {
    return server;
  }

  /**
   * For testing return the scoped transaction.
   */
  public static TDTransaction testScopedTransaction() {
    return server.scopedTransaction;
  }
}
