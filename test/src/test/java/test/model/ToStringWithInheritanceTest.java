package test.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToStringWithInheritanceTest {

  @Test
  void toString_notExpected_when_inherited() {
    StringCustomProperty prop = new StringCustomProperty("test");
    assertThat(prop.toString()).isEqualTo("test");
  }
}
