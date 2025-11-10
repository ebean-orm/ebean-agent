package io.ebean.enhance.common;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ArgParserTest {

  @Test
  void parse() {
    Map<String, String> map = ArgParser.parse("packages=com/google/foo");
    assertThat(map).containsKey("packages");
    assertThat(map.get("packages")).isEqualTo("com/google/foo");
  }
}
