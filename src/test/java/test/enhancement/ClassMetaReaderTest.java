package test.enhancement;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import io.ebean.enhance.common.AgentManifest;
import io.ebean.enhance.common.AnnotationInfo;
import io.ebean.enhance.common.ClassMetaCache;
import org.testng.annotations.Test;

import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.ClassMetaReader;
import io.ebean.enhance.entity.ClassPathClassBytesReader;
import io.ebean.enhance.common.EnhanceContext;

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
