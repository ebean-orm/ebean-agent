package com.avaje.ebean.enhance.agent;

import java.io.PrintStream;

/**
 * MessageOutput typically used with System.out.
 */
public class SysoutMessageOutput implements MessageOutput {

  private final PrintStream writer;

  public SysoutMessageOutput(PrintStream writer) {
    this.writer = writer;
  }

  @Override
  public void println(String message) {
    writer.println(message);
  }
}
