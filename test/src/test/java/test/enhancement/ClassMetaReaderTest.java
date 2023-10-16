package test.enhancement;

import io.ebean.enhance.asm.Type;
import io.ebean.enhance.common.*;
import io.ebean.enhance.entity.ClassPathClassBytesReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClassMetaReaderTest {

  @Test
  void checkOtherClass_withAnnotations() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(true, "test.model.SomeClass", classLoader);

    assertNotNull(classMeta);
    assertFalse(classMeta.hasPersistentFields());
    assertFalse(classMeta.isEntity());
  }

  @Test
  void checkNoEnhanceMappedSuper_hasNoPersistentFields() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.NoEnhanceMappedSuper", classLoader);

    assertNotNull(classMeta);
    assertFalse(classMeta.hasPersistentFields());
    assertTrue(classMeta.isEntity());
  }

  @Test
  void checkEnhanceMappedSuper_hasPersistentField() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.EnhanceMappedSuper", classLoader);

    assertNotNull(classMeta);
    assertTrue(classMeta.hasPersistentFields());
    assertTrue(classMeta.isEntity());
  }

  @Test
  void checkEnhanceMappedSuper_hasPersistentFieldId() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.EnhanceMappedSuperId", classLoader);

    assertNotNull(classMeta);
    assertTrue(classMeta.hasPersistentFields());
    assertTrue(classMeta.isEntity());
  }

  @Test
  void checkTransactionalAtClassLevel() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.SomeTransactionalServiceCls", classLoader);

    assertNotNull(classMeta);
    AnnotationInfo annotationInfo = classMeta.annotationInfo();
    assertEquals(annotationInfo.getValue("getGeneratedKeys"), Boolean.FALSE);
    assertEquals(annotationInfo.getValue("batchSize"), Integer.valueOf(50));
  }

  private ClassMeta getClassMetaForOverrideTests() throws ClassNotFoundException {
    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(true, "test.model.SomeTransactionalWithOverride", classLoader);
    assertNotNull(classMeta);
    return classMeta;
  }

  @SuppressWarnings("unchecked")
  @Test
  void checkClassOverrideCls() throws ClassNotFoundException {

    ClassMeta classMeta = getClassMetaForOverrideTests();

    // check class meta annotation
    AnnotationInfo classAi = classMeta.annotationInfo();
    assertThat(classAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (classAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));


  }

  @Test
  void checkClassOverrideMethod1() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method1 has no annotation, so it must take the annotation from class level
    AnnotationInfo methodAi = classMeta.interfaceTransactionalInfo("someMethod1", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));
  }

  @Test
  void checkClassOverrideMethod2() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method2 has @Transactional(rollbackFor = ArrayIndexOutOfBoundsException.class)
    AnnotationInfo methodAi = classMeta.interfaceTransactionalInfo("someMethod2", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(ArrayIndexOutOfBoundsException.class));
  }

  @Test
  void checkClassOverrideMethod3() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method3 has @Transactional(rollbackFor = {})
    AnnotationInfo methodAi = classMeta.interfaceTransactionalInfo("someMethod3", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor"))).isEmpty();
  }

  @Test
  void checkClassOverrideMethod4() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method4 has @Transactional(batchSize = 23)
    AnnotationInfo methodAi = classMeta.interfaceTransactionalInfo("someMethod4", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(23);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));
  }

  @Test
  void checkClassOverrideMethod5() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method5 has @Transactional
    AnnotationInfo methodAi = classMeta.interfaceTransactionalInfo("someMethod5", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));
  }

  @Test
  void checkClassOverrideMethod6() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method5 has @Transactional
    AnnotationInfo methodAi = classMeta.interfaceTransactionalInfo("someMethod6", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(0);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor"))).isEmpty();
  }

  @Test
  void supplyVersionViaArgument() throws IOException {
    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    try (URLClassLoader emptyClassloader = new URLClassLoader(new URL[0])) {

      AgentManifest manifest = new AgentManifest(emptyClassloader);
      assertThat(manifest.enhancementVersion()).isEqualTo(133);

      EnhanceContext enhanceContext0 = new EnhanceContext(reader,"debug=1", manifest);
      assertThat(enhanceContext0.isEnhancedToString()).isTrue();
      assertThat(enhanceContext0.interceptNew()).isEqualTo("io/ebean/bean/EntityBeanIntercept");

      EnhanceContext enhanceContext141 = new EnhanceContext(reader,"version=141;debug=1", manifest);
      assertThat(enhanceContext141.isEnhancedToString()).isTrue();
      assertThat(enhanceContext141.interceptNew()).isEqualTo("io/ebean/bean/InterceptReadWrite");
    }
  }

  @Test
  void testEnhanceContext() {

    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    AgentManifest manifest = new AgentManifest(getClass().getClassLoader());
    EnhanceContext enhanceContext = new EnhanceContext(reader,"debug=9", manifest);

    assertFalse(enhanceContext.isIgnoreClass("jim.bob.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jim.bob.other.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jack.jones.Me"));

    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("jim/bob/Me"));
    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("jim/bob/other/Me"));

    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("jim/Me"));
    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("jack/jones/Me"));

    assertFalse(enhanceContext.isIgnoreClass("jack.jones.fred.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jack.jones.fred.other.Me"));

    assertFalse(enhanceContext.isIgnoreClass("line.foo.Me"));
    assertFalse(enhanceContext.isIgnoreClass("line.foo.other.Me"));
    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("line/foo/Me"));
  }

  @Test
  void testEnhanceContextFilter() {

    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    AgentManifest manifest = new AgentManifest(getClass().getClassLoader());
    EnhanceContext enhanceContext = new EnhanceContext(reader, "debug=1;packages=jim,org.foo", manifest);

    assertFalse(enhanceContext.isIgnoreClass("jim/bob/Me"));
    assertFalse(enhanceContext.isIgnoreClass("jim/Me"));
    assertFalse(enhanceContext.isIgnoreClass("org/foo/Me"));
    assertFalse(enhanceContext.isIgnoreClass("org/foo/bar/Me"));

    assertTrue(enhanceContext.isIgnoreClass("com/Me"));
    assertTrue(enhanceContext.isIgnoreClass("com/some/Me"));
  }

  private ClassMetaReader createClassMetaReader() {

    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    AgentManifest manifest = new AgentManifest(getClass().getClassLoader());

    EnhanceContext enhanceContext = new EnhanceContext(reader, "debug=9", manifest);
    return new ClassMetaReader(enhanceContext, new ClassMetaCache());
  }
}
