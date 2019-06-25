package test.enhancement;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.ebean.annotation.Transactional;
import io.ebean.enhance.asm.Type;
import io.ebean.enhance.common.AgentManifest;
import io.ebean.enhance.common.AnnotationInfo;
import io.ebean.enhance.common.ClassMetaCache;
import org.testng.annotations.Test;

import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.ClassMetaReader;
import io.ebean.enhance.entity.ClassPathClassBytesReader;
import io.ebean.enhance.common.EnhanceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class ClassMetaReaderTest {

  @Test
  public void checkOtherClass_withAnnotations() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(true, "test.model.SomeClass", classLoader);

    assertNotNull(classMeta);
    assertFalse(classMeta.hasPersistentFields());
    assertFalse(classMeta.isEntity());
  }

  @Test
  public void checkNoEnhanceMappedSuper_hasNoPersistentFields() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.NoEnhanceMappedSuper", classLoader);

    assertNotNull(classMeta);
    assertFalse(classMeta.hasPersistentFields());
    assertFalse(classMeta.isEntity());
  }

  @Test
  public void checkEnhanceMappedSuper_hasPersistentField() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.EnhanceMappedSuper", classLoader);

    assertNotNull(classMeta);
    assertTrue(classMeta.hasPersistentFields());
    assertTrue(classMeta.isEntity());
  }

  @Test
  public void checkEnhanceMappedSuper_hasPersistentFieldId() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.EnhanceMappedSuperId", classLoader);

    assertNotNull(classMeta);
    assertTrue(classMeta.hasPersistentFields());
    assertTrue(classMeta.isEntity());
  }

  @Test
  public void checkTransactionalAtClassLevel() throws ClassNotFoundException {

    ClassMetaReader classMetaReader = createClassMetaReader();

    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.SomeTransactionalServiceCls", classLoader);

    assertNotNull(classMeta);
    AnnotationInfo annotationInfo = classMeta.getAnnotationInfo();
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
  public void checkClassOverrideCls() throws ClassNotFoundException {

    ClassMeta classMeta = getClassMetaForOverrideTests();

    // check class meta annotation
    AnnotationInfo classAi = classMeta.getAnnotationInfo();
    assertThat(classAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (classAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));


  }
  
  @Test
  public void checkClassOverrideMethod1() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method1 has no annotation, so it must take the annotation from class level
    AnnotationInfo methodAi = classMeta.getInterfaceTransactionalInfo("someMethod1", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));
  }

  @Test
  public void checkClassOverrideMethod2() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method2 has @Transactional(rollbackFor = ArrayIndexOutOfBoundsException.class)
    AnnotationInfo methodAi = classMeta.getInterfaceTransactionalInfo("someMethod2", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(ArrayIndexOutOfBoundsException.class));
  }

  @Test
  public void checkClassOverrideMethod3() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method3 has @Transactional(rollbackFor = {})
    AnnotationInfo methodAi = classMeta.getInterfaceTransactionalInfo("someMethod3", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor"))).isEmpty();
  }

  
  @Test
  public void checkClassOverrideMethod4() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method4 has @Transactional(batchSize = 23)
    AnnotationInfo methodAi = classMeta.getInterfaceTransactionalInfo("someMethod4", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(23);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));
  }

  
  @Test
  public void checkClassOverrideMethod5() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method5 has @Transactional
    AnnotationInfo methodAi = classMeta.getInterfaceTransactionalInfo("someMethod5", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(42);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor")))
      .containsExactly(Type.getType(IOException.class), Type.getType(IllegalStateException.class));
  }

  @Test
  public void checkClassOverrideMethod6() throws ClassNotFoundException {
    ClassMeta classMeta = getClassMetaForOverrideTests();

    // Method5 has @Transactional
    AnnotationInfo methodAi = classMeta.getInterfaceTransactionalInfo("someMethod6", "()V");
    assertThat(methodAi.getValue("batchSize")).isEqualTo(0);
    assertThat((List<Type>) (methodAi.getValue("rollbackFor"))).isEmpty();
  }
  
  @Test
  public void testEnhanceContext() {

    Set<String> initialPackages = new HashSet<>();
    initialPackages.add("jim.bob");
    initialPackages.add("jack.jones.fred");
    initialPackages.add("line.foo");

    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    AgentManifest manifest = AgentManifest.read(getClass().getClassLoader(), initialPackages);
    EnhanceContext enhanceContext = new EnhanceContext(reader,"debug=9", manifest);

    assertFalse(enhanceContext.isIgnoreClass("jim.bob.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jim.bob.other.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jack.jones.Me"));

    assertTrue(enhanceContext.detectEntityTransactionalEnhancement("jim/bob/Me"));
    assertTrue(enhanceContext.detectEntityTransactionalEnhancement("jim/bob/other/Me"));

    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("jim/Me"));
    assertFalse(enhanceContext.detectEntityTransactionalEnhancement("jack/jones/Me"));

    assertFalse(enhanceContext.isIgnoreClass("jack.jones.fred.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jack.jones.fred.other.Me"));

    assertFalse(enhanceContext.isIgnoreClass("line.foo.Me"));
    assertFalse(enhanceContext.isIgnoreClass("line.foo.other.Me"));
    assertTrue(enhanceContext.detectEntityTransactionalEnhancement("line/foo/Me"));
  }

  private ClassMetaReader createClassMetaReader() {

    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    AgentManifest manifest = AgentManifest.read(getClass().getClassLoader(), null);

    EnhanceContext enhanceContext = new EnhanceContext(reader, "debug=9", manifest);
    return new ClassMetaReader(enhanceContext, new ClassMetaCache());
  }
}
