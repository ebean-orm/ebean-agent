package io.ebean.enhance.ant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AntEnhanceTaskTest {

  @Test
  void combine() {
    String result = AntEnhanceTask.combine("com/one/**, com/two/**", "debug=1");
    assertThat(result).isEqualTo("packages=com/one/, com/two/,debug=1");
  }

  @Test
  void combine_when_NoTransformArgs() {
    String result = AntEnhanceTask.combine("com/one/**", "");
    assertThat(result).isEqualTo("packages=com/one/");
  }

  @Test
  void combine_when_NullTransformArgs() {
    String result = AntEnhanceTask.combine("com/one/**", null);
    assertThat(result).isEqualTo("packages=com/one/");
  }

}
