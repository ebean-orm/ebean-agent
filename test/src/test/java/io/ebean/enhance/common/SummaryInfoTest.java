package io.ebean.enhance.common;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class SummaryInfoTest {

  @Test
  public void prepare() {

    SummaryInfo summaryInfo = new SummaryInfo(new ArrayList<>());
    summaryInfo.addQueryBean("org/foo/model/query/QCustomer");
    summaryInfo.addQueryBeanCaller("org/foo/model/query/QCustomer");
    summaryInfo.addQueryBean("org/foo/model/query/QOrder");
    summaryInfo.addQueryBeanCaller("org/foo/model/query/QOrder");

    summaryInfo.prepare();

    assertFalse(summaryInfo.hasEntities());
    assertFalse(summaryInfo.hasTransactional());

    assertTrue(summaryInfo.hasQueryBeans());
    assertFalse(summaryInfo.hasQueryCallers());
    assertFalse(summaryInfo.hasFieldAccess());
    assertEquals("Query Callers (0)  pkgs[] beans[]", summaryInfo.queryCallers());
    assertEquals(" Field Access (0)  pkgs[] beans[]", summaryInfo.fieldAccess());
  }

  @Test
  public void prepare_companion() {

    SummaryInfo summaryInfo = new SummaryInfo(new ArrayList<>());
    summaryInfo.addQueryBean("org/foo/model/query/QCustomer");
    summaryInfo.addQueryBeanCaller("org/foo/model/query/QCustomer");
    summaryInfo.addQueryBeanCaller("org/foo/model/query/QCustomer$Companion");

    summaryInfo.prepare();
    assertFalse(summaryInfo.hasQueryCallers());
  }

  @Test
  public void summaryInfo() {

    SummaryInfo summaryInfo = new SummaryInfo(new ArrayList<>());
    summaryInfo.addEntity("org/model/Customer");
    summaryInfo.addQueryBean("org/model/query/QCustomer");
    summaryInfo.addQueryBeanCaller("org/dao/MyDao");
    summaryInfo.addTransactional("org/dao/OtherDao");
    summaryInfo.addFieldAccessUser("org/model/CustomerTest");

    summaryInfo.prepare();
    assertFalse(summaryInfo.isEmpty());
    assertTrue(summaryInfo.hasEntities());
    assertTrue(summaryInfo.hasQueryBeans());
    assertTrue(summaryInfo.hasFieldAccess());
    assertTrue(summaryInfo.hasQueryCallers());
    assertTrue(summaryInfo.hasTransactional());

    assertEquals("     Entities (1)  pkgs[org/model] beans[Customer]", summaryInfo.entities());
    assertEquals("   QueryBeans (1)  pkgs[org/model/query] beans[QCustomer]", summaryInfo.queryBeans());
    assertEquals("Transactional (1)  pkgs[org/dao] beans[OtherDao]", summaryInfo.transactional());
    assertEquals("Query Callers (1)  pkgs[org/dao] beans[MyDao]", summaryInfo.queryCallers());
    assertEquals(" Field Access (1)  pkgs[org/model] beans[CustomerTest]", summaryInfo.fieldAccess());
  }

  @Test
  public void summaryInfo_empty() {

    SummaryInfo summaryInfo = new SummaryInfo(new ArrayList<>());
    summaryInfo.prepare();
    assertTrue(summaryInfo.isEmpty());
    assertFalse(summaryInfo.hasEntities());
    assertFalse(summaryInfo.hasQueryBeans());
    assertFalse(summaryInfo.hasQueryCallers());
    assertFalse(summaryInfo.hasFieldAccess());
    assertFalse(summaryInfo.hasTransactional());

    assertEquals("     Entities (0)  pkgs[] beans[]", summaryInfo.entities());
    assertEquals("   QueryBeans (0)  pkgs[] beans[]", summaryInfo.queryBeans());
    assertEquals("Transactional (0)  pkgs[] beans[]", summaryInfo.transactional());
    assertEquals("Query Callers (0)  pkgs[] beans[]", summaryInfo.queryCallers());
    assertEquals(" Field Access (0)  pkgs[] beans[]", summaryInfo.fieldAccess());
  }

  @Test
  public void test_SumOut() {

    Set<String> beans = new HashSet<>(asList("org/foo/model/BeanA", "org/foo/model/BeanZ", "org/foo/model/BeanB"));
    SummaryInfo.SumOut sumOut = new SummaryInfo.SumOut(beans);

    assertEquals("org/foo/model", sumOut.commonPackage());
    assertEquals("[BeanA, BeanB, BeanZ]", sumOut.beans());

    assertEquals("Entities (3)  pkgs[org/foo/model] beans[BeanA, BeanB, BeanZ]", sumOut.summary("Entities"));
  }

  @Test
  public void test_SumOut_when_subpackage() {

    Set<String> beans = new HashSet<>(asList("org/foo/model/BeanA", "org/foo/model/subpack/BeanZ", "org/foo/model/subpack/BeanB"));
    SummaryInfo.SumOut sumOut = new SummaryInfo.SumOut(beans);

    assertEquals("org/foo/model", sumOut.commonPackage());
    assertEquals("[BeanA, subpack/BeanB, subpack/BeanZ]", sumOut.beans());
    assertEquals("Entities (3)  pkgs[org/foo/model] beans[BeanA, subpack/BeanB, subpack/BeanZ]", sumOut.summary("Entities"));
  }

  @Test
  public void test_SumOut_diff() {

    Set<String> beans = new HashSet<>(asList("org/foo/model/BeanA", "org/foo/modelb/BeanB"));
    SummaryInfo.SumOut sumOut = new SummaryInfo.SumOut(beans);

    assertEquals("org/foo", sumOut.commonPackage());
    assertEquals("[model/BeanA, modelb/BeanB]", sumOut.beans());
  }

  @Test
  public void test_SumOut_diff2() {

    Set<String> beans = new HashSet<>(asList("org/foo/model/BeanX", "org/foo/model/BeanT", "org/foo/modelx/BeanA", "org/foo/modelx/BeanC", "org/foo/modeld/BeanD"));
    SummaryInfo.SumOut sumOut = new SummaryInfo.SumOut(beans);

    assertEquals("org/foo", sumOut.commonPackage());
    assertEquals("[model/BeanT, model/BeanX, modeld/BeanD, modelx/BeanA, modelx/BeanC]", sumOut.beans());
  }

  @Test
  public void test_SumOut_nothingSimilar() {

    Set<String> beans = new HashSet<>(asList("org/foo/model/BeanX", "baz/foo/model/BeanT"));
    SummaryInfo.SumOut sumOut = new SummaryInfo.SumOut(beans);

    assertEquals("", sumOut.commonPackage());
    assertEquals("[baz/foo/model/BeanT, org/foo/model/BeanX]", sumOut.beans());
  }
}
