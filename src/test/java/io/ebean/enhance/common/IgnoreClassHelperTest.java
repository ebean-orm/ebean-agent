package io.ebean.enhance.common;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class IgnoreClassHelperTest {

  @Test
  public void test() {

    IgnoreClassHelper ignoreClassHelper = new IgnoreClassHelper();

    assertTrue(ignoreClassHelper.isIgnoreClass("io/ebean/Model"));
    assertTrue(ignoreClassHelper.isIgnoreClass("io.ebean.Model"));

    assertTrue(ignoreClassHelper.isIgnoreClass("java/lang/Boolean"));
    assertTrue(ignoreClassHelper.isIgnoreClass("java/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/joda/LocalDate"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/apache/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("junit/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("javax/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("play/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("sbt/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("scala/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("sun/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("sunw/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("oracle/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("groovy/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("kotlin/Something"));

    assertTrue(ignoreClassHelper.isIgnoreClass("org/junit/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/apache/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/eclipse/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/jetbrains/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/joda/Something"));

    assertTrue(ignoreClassHelper.isIgnoreClass("io/ebeaninternal/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("io/ebean/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/avaje/Something"));


    assertFalse(ignoreClassHelper.isIgnoreClass("org/koda/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("foo/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("bar/pixie/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("bar/poo/Foo"));
  }

}
