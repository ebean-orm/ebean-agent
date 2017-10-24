package io.ebean.enhance.common;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AgentManifestTest {

	@Test
	public void testRead() throws Exception {

		AgentManifest manifest = AgentManifest.read(this.getClass().getClassLoader(), null);

		assertThat(manifest.getTransactionalPackages()).contains("test");
		assertThat(manifest.getEntityPackages()).contains("test");

		FilterEntityTransactional filterEntityTransactional = new FilterEntityTransactional(manifest);
		assertTrue(filterEntityTransactional.detectEnhancement("test/model/domain/Foo"));
		assertTrue(filterEntityTransactional.detectEnhancement("test/Anything"));
		assertFalse(filterEntityTransactional.detectEnhancement("foo/Any"));


		FilterQueryBean filterQueryBean = new FilterQueryBean(manifest);
		assertTrue(filterQueryBean.detectEnhancement("test/Any"));
		assertTrue(filterQueryBean.detectEnhancement("foo/Any"));
	}


	@Test
	public void testRead_basic() throws Exception {

		AgentManifest manifest =
				new AgentManifest(null)
						.readManifests(this.getClass().getClassLoader(), "META-INF/test_basic.mf");

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

		AgentManifest manifest =
				new AgentManifest(null)
						.readManifests(this.getClass().getClassLoader(), "META-INF/test_none.mf");

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

		AgentManifest manifest =
				new AgentManifest(null)
						.readManifests(this.getClass().getClassLoader(), "META-INF/test_old.mf");

		assertThat(manifest.getEntityPackages()).contains("aone.domain", "btwo.domain", "cthree.other");
		assertThat(manifest.isTransactionalNone()).isFalse();
		assertThat(manifest.isQueryBeanNone()).isFalse();

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

		AgentManifest manifest =
				new AgentManifest(null)
						.readManifests(this.getClass().getClassLoader(), "META-INF/test_expected.mf");

		assertThat(manifest.getEntityPackages()).contains("org.foo.domain", "org.foo.some.domain");
		assertThat(manifest.isTransactionalNone()).isFalse();
		assertThat(manifest.isQueryBeanNone()).isFalse();

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

}