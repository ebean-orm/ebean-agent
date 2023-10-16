package io.ebean.enhance.common;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackageFilterTest {

  @Test
  void ignore() {
    PackageFilter filter = new PackageFilter("org.foo,com.bar");

    assertFalse(filter.ignore("org/foo/A"));
    assertFalse(filter.ignore("org/foo/B"));
    assertFalse(filter.ignore("org/foo/some/A"));
    assertFalse(filter.ignore("com/bar/A"));
    assertFalse(filter.ignore("com/bar/some/A"));

    assertTrue(filter.ignore("org/baz/Some"));
    assertTrue(filter.ignore("org/foo"));
  }
}
