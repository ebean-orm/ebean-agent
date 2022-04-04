package io.ebean.enhance.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentManifestTest {

  @Test
  public void testRead() {

    AgentManifest manifest = new AgentManifest(this.getClass().getClassLoader());

    assertThat(manifest.getTransactionalPackages()).contains("test");
    assertThat(manifest.getEntityPackages()).contains("test.model.domain");

    FilterEntityTransactional filterEntityTransactional = new FilterEntityTransactional(manifest);
    assertTrue(filterEntityTransactional.detectEnhancement("test/model/domain/Foo"));
    assertTrue(filterEntityTransactional.detectEnhancement("test/Anything"));
    assertFalse(filterEntityTransactional.detectEnhancement("foo/Any"));


    FilterQueryBean filterQueryBean = new FilterQueryBean(manifest);
    assertTrue(filterQueryBean.detectEnhancement("test/model/domain/Any"));
    assertTrue(filterQueryBean.detectEnhancement("foo/Any"));
  }

  @Test
  public void readInternalVersion() throws IOException {

    AgentManifest manifest = new AgentManifest();
    manifest.readEbeanVersion(this.getClass().getClassLoader(), "META-INF/test_ebean-version.mf");

    assertThat(manifest.getEbeanInternalVersion()).isEqualTo(129);
  }

  @Test
  public void testRead_basic() throws Exception {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_basic.mf");

    assertThat(manifest.getEntityPackages()).contains("aone.domain", "btwo.domain");
    assertThat(manifest.getTransactionalPackages()).contains("aone","btwo.services","cthree.other");

    FilterEntityTransactional filterEntityTransactional = new FilterEntityTransactional(manifest);
    assertTrue(filterEntityTransactional.detectEnhancement("aone/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/domain/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/services/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("btwo/other/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("btwo/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("cthree/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("cthree/bar/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("cthree/other/Any"));


    FilterQueryBean filterQueryBean = new FilterQueryBean(manifest);
    assertTrue(filterQueryBean.detectEnhancement("test/Any"));
    assertTrue(filterQueryBean.detectEnhancement("foo/Any"));
  }

  @Test
  public void testRead_none() throws Exception {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_none.mf");

    assertThat(manifest.getEntityPackages()).contains("aone.domain", "btwo.domain", "cthree.other");
    assertThat(manifest.isTransactionalNone()).isTrue();
    assertThat(manifest.isQueryBeanNone()).isTrue();

    assertThat(manifest.getTransactionalPackages()).containsExactly("none");
    assertThat(manifest.getQuerybeanPackages()).containsExactly("none");

    FilterEntityTransactional filterEntityTransactional = new FilterEntityTransactional(manifest);

    assertTrue(filterEntityTransactional.detectEnhancement("aone/domain/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/domain/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("cthree/other/Any"));

    assertFalse(filterEntityTransactional.detectEnhancement("aone/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("btwo/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("btwo/other/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("btwo/services/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("cthree/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("cthree/bar/Any"));

    FilterQueryBean filterQueryBean = new FilterQueryBean(manifest);
    assertFalse(filterQueryBean.detectEnhancement("test/Any"));
    assertFalse(filterQueryBean.detectEnhancement("foo/Any"));
    assertFalse(filterQueryBean.detectEnhancement("aone/domain/Any"));
    assertFalse(filterQueryBean.detectEnhancement("btwo/domain/Any"));
    assertFalse(filterQueryBean.detectEnhancement("cthree/other/Any"));
  }

  @Test
  public void testRead_old() throws Exception {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_old.mf");

    assertThat(manifest.getEntityPackages()).contains("aone.domain", "btwo.domain", "cthree.other");
    assertThat(manifest.isTransactionalNone()).isFalse();
    assertThat(manifest.isQueryBeanNone()).isFalse();

    assertThat(manifest.isTransientInternalFields()).isFalse();
    assertThat(manifest.isCheckNullManyFields()).isTrue();

    assertThat(manifest.getTransactionalPackages()).isEmpty();
    assertThat(manifest.getQuerybeanPackages()).isEmpty();


    FilterEntityTransactional filterEntityTransactional = new FilterEntityTransactional(manifest);

    assertTrue(filterEntityTransactional.detectEnhancement("aone/domain/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/domain/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("cthree/other/Any"));

    assertTrue(filterEntityTransactional.detectEnhancement("aone/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/other/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("btwo/services/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("cthree/Any"));
    assertTrue(filterEntityTransactional.detectEnhancement("cthree/bar/Any"));

    FilterQueryBean filterQueryBean = new FilterQueryBean(manifest);
    assertTrue(filterQueryBean.detectEnhancement("test/Any"));
    assertTrue(filterQueryBean.detectEnhancement("foo/Any"));
    assertTrue(filterQueryBean.detectEnhancement("aone/domain/Any"));
    assertTrue(filterQueryBean.detectEnhancement("btwo/domain/Any"));
    assertTrue(filterQueryBean.detectEnhancement("cthree/other/Any"));
  }


  @Test
  public void testRead_expected() throws Exception {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_expected.mf");

    assertThat(manifest.getEntityPackages()).contains("org.foo.domain", "org.foo.some.domain");
    assertThat(manifest.isTransactionalNone()).isFalse();
    assertThat(manifest.isQueryBeanNone()).isFalse();
    assertThat(manifest.isTransientInternalFields()).isTrue();
    assertThat(manifest.isCheckNullManyFields()).isFalse();

    assertThat(manifest.getTransactionalPackages()).containsExactly("org.foo");
    assertThat(manifest.getQuerybeanPackages()).containsExactly("org.foo");


    FilterEntityTransactional filterEntityTransactional = new FilterEntityTransactional(manifest);

    assertTrue(filterEntityTransactional.detectEnhancement("org/foo/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("com/Any"));
    assertFalse(filterEntityTransactional.detectEnhancement("org/bar/Any"));


    FilterQueryBean filterQueryBean = new FilterQueryBean(manifest);
    assertTrue(filterQueryBean.detectEnhancement("org/foo/Any"));
    assertFalse(filterQueryBean.detectEnhancement("com/Any"));
    assertFalse(filterQueryBean.detectEnhancement("org/bar/Any"));
  }

  @Test
  public void testRead_EnhanceContext() throws IOException {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_expected.mf");

    EnhanceContext context = new EnhanceContext(null, null, manifest);
    context.setLogLevel(5);

    assertThat(context.isTransientInternalFields()).isTrue();
    assertThat(context.isCheckNullManyFields()).isFalse();
    assertThat(context.getLogLevel()).isEqualTo(5);

    assertThat(context.getEntityPackages()).containsOnly("org.foo.some.domain", "org.foo.domain");
    assertThat(context.getTransactionalPackages()).containsOnly("org.foo");
    assertThat(context.getQuerybeanPackages()).containsOnly("org.foo");

    assertThat(context.getPackagesSummary()).isEqualTo("packages entity:[org.foo.some.domain, org.foo.domain]  transactional:[org.foo]  querybean:[org.foo]  profileLocation:true  version:0");

    context.collectSummary();
    SummaryInfo emptySummary = context.getSummaryInfo();
    assertThat(emptySummary.loadedResources()).containsOnly("META-INF/test_expected.mf");

    assertThat(emptySummary.entities()).isEqualTo("     Entities (0)  pkgs[] beans[]");

    context.collectSummary();
    context.summaryEntity("org/foo/domain/Customer");

    SummaryInfo summary = context.getSummaryInfo();
    assertThat(summary.entities()).isEqualTo("     Entities (1)  pkgs[org/foo/domain] beans[Customer]");
  }

  @Test
  public void testRead_EnhanceContext_notSet() throws IOException {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_old.mf");

    EnhanceContext context = new EnhanceContext(null, null, manifest);
    assertThat(context.isTransientInternalFields()).isFalse();
    assertThat(context.isCheckNullManyFields()).isTrue();

    assertThat(context.getEntityPackages()).containsOnly("btwo.domain", "aone.domain", "cthree.other");
    assertThat(context.getTransactionalPackages()).isEmpty();
    assertThat(context.getQuerybeanPackages()).isEmpty();
  }

  @Test
  public void testRead_EnhanceContext_topPackages() throws IOException {

    AgentManifest manifest = new AgentManifest();
    manifest.readManifests(this.getClass().getClassLoader(), "META-INF/test_top.mf");

    assertThat(manifest.getEntityPackages()).containsOnly("org.one.myapp.domain");
    assertThat(manifest.getTransactionalPackages()).containsOnly("org.one.myapp");
    assertThat(manifest.getQuerybeanPackages()).containsOnly("org.one.myapp");
  }

}
