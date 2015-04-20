package com.avaje.ebean;

public class Ebean {

  public static EbeanServer getServer(String name) {

    return new TDEbeanServer();
  }
}
