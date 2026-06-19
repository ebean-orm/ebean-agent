package io.ebean.enhance.querybean;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DetectQueryBeanTest {

  @Test
  void isQueryBean_withPackages_matches() {
    DetectQueryBean detect = new DetectQueryBean();
    detect.addAll(Set.of("de.worldinsight.wision"));

    assertThat(detect.isQueryBean("de/worldinsight/wision/auth/roles/query/QRoleEntity")).isTrue();
    assertThat(detect.isQueryBean("de/worldinsight/wision/domain/query/QCustomer")).isTrue();
    assertThat(detect.isQueryBean("de/worldinsight/wision/auth/roles/query/RoleEntity")).isFalse(); // no Q prefix
    assertThat(detect.isQueryBean("com/other/query/QFoo")).isFalse(); // wrong package
  }

  @Test
  void isQueryBean_withPackages_noMatch_returnsFalse() {
    DetectQueryBean detect = new DetectQueryBean();
    detect.addAll(Set.of("de.worldinsight.wision"));

    assertThat(detect.isQueryBean("com/other/query/QFoo")).isFalse();
    assertThat(detect.isQueryBean("de/worldinsight/wision/RoleEntity")).isFalse(); // not in query sub-package
  }

  @Test
  void isQueryBean_emptyPackages_namingConventionFallback() {
    // When no packages loaded (ebean.mf not found — Gradle Kotlin KAPT scenario),
    // trust naming convention: .../query/Q... classes are treated as query beans.
    DetectQueryBean detect = new DetectQueryBean();
    assertThat(detect.isEmpty()).isTrue();

    assertThat(detect.isQueryBean("de/worldinsight/wision/auth/roles/query/QRoleEntity")).isTrue();
    assertThat(detect.isQueryBean("com/example/domain/query/QCustomer")).isTrue();
    assertThat(detect.isQueryBean("com/example/domain/query/assoc/QAssocAddress")).isTrue();
  }

  @Test
  void isQueryBean_emptyPackages_nonQueryClass_returnsFalse() {
    DetectQueryBean detect = new DetectQueryBean();
    assertThat(detect.isEmpty()).isTrue();

    assertThat(detect.isQueryBean("de/worldinsight/wision/auth/roles/RoleEntity")).isFalse();
    assertThat(detect.isQueryBean("de/worldinsight/wision/auth/roles/query/RoleRepository")).isFalse(); // no Q prefix
    assertThat(detect.isQueryBean("com/example/SomeClass")).isFalse();
  }
}

