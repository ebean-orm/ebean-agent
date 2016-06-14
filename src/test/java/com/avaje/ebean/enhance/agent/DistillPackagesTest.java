package com.avaje.ebean.enhance.agent;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class DistillPackagesTest {

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