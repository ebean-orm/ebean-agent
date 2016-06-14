package com.avaje.ebean.enhance.agent;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class DistillPackagesTest {

  @Test
  public void addNulls_expect_empty() throws Exception {

    List<String> distilled = new DistillPackages()
        .add(null).addRaw(null)
        .distill();

    assertEquals(distilled.toString(),"[]");
  }


  @Test
  public void addRawOne_expect_empty() throws Exception {

    List<String> distilled = new DistillPackages()
        .add(null).addRaw("com.foo")
        .distill();

    assertEquals(distilled.toString(),"[com.foo]");
  }

  @Test
  public void addRawTwo_expect_empty() throws Exception {

    List<String> distilled = new DistillPackages()
        .add(null).addRaw("com.foo,com.moo")
        .distill();

    assertEquals(distilled.toString(),"[com.foo, com.moo]");
  }

  @Test
  public void addRawMixed_expect_empty() throws Exception {

    List<String> distilled = new DistillPackages()
        .add(Arrays.asList("com.bar", "com.foo.sub"))
        .addRaw("com.foo,com.moo,com.moo.sub")
        .distill();

    assertEquals(distilled.toString(),"[com.bar, com.foo, com.moo]");
  }

  @Test
  public void addWithSubPackage() throws Exception {

    List<String> distilled = new DistillPackages()
        .add(Arrays.asList("com.foo", "com.bar", "com.foo.sub"))
        .distill();

    assertEquals(distilled.toString(),"[com.bar, com.foo]");
  }

  @Test
  public void addWithDuplicates() throws Exception {

    List<String> distilled = new DistillPackages()
        .add(Arrays.asList("com.foo", "com.bar", "com.foo.sub"))
        .add(Arrays.asList("com.alpha", "com.foo.othersub"))
        .distill();

    assertEquals(distilled.toString(),"[com.alpha, com.bar, com.foo]");
  }

}