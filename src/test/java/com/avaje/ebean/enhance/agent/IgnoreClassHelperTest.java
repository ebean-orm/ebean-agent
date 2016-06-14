package com.avaje.ebean.enhance.agent;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class IgnoreClassHelperTest {

  @Test
  public void test() {

    IgnoreClassHelper ignoreClassHelper = new IgnoreClassHelper(new HashSet<String>());

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

    assertTrue(ignoreClassHelper.isIgnoreClass("com/avaje/ebeaninternal/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("com/avaje/ebean/Something"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/avaje/ebean/Something"));


    assertFalse(ignoreClassHelper.isIgnoreClass("org/koda/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("foo/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("bar/pixie/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("bar/poo/Foo"));
  }

  @Test
  public void testWithPackages() {

    Set<String> packages = new HashSet<String>();
    packages.add("foo");
    packages.add("bar/pixie");

    IgnoreClassHelper ignoreClassHelper = new IgnoreClassHelper(packages);

    assertTrue(ignoreClassHelper.isIgnoreClass("java/lang/Boolean"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/joda/LocalDate"));
    assertTrue(ignoreClassHelper.isIgnoreClass("org/koda/Foo"));
    assertTrue(ignoreClassHelper.isIgnoreClass("bar/poo/Foo"));

    assertFalse(ignoreClassHelper.isIgnoreClass("foo/Foo"));
    assertFalse(ignoreClassHelper.isIgnoreClass("bar/pixie/Foo"));
  }
}