package test.enhancement;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.avaje.ebean.enhance.agent.ClassMeta;
import com.avaje.ebean.enhance.agent.ClassMetaReader;
import com.avaje.ebean.enhance.agent.ClassPathClassBytesReader;
import com.avaje.ebean.enhance.agent.EnhanceContext;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ClassMetaReaderTests {

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
  public void testEnhanceContext() {

    Set<String> initialPackages = new HashSet<String>();
    initialPackages.add("jim.bob");
    initialPackages.add("jack.jones.fred");


    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    EnhanceContext enhanceContext = new EnhanceContext(reader,"debug=9;packages=line.foo", null, initialPackages);

    assertTrue(enhanceContext.isIgnoreClass("jim.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jim.bob.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jim.bob.other.Me"));

    assertTrue(enhanceContext.isIgnoreClass("jack.jones.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jack.jones.fred.Me"));
    assertFalse(enhanceContext.isIgnoreClass("jack.jones.fred.other.Me"));

    assertTrue(enhanceContext.isIgnoreClass("line.Me"));
    assertFalse(enhanceContext.isIgnoreClass("line.foo.Me"));
    assertFalse(enhanceContext.isIgnoreClass("line.foo.other.Me"));

  }

  private ClassMetaReader createClassMetaReader() {
    
    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    EnhanceContext enhanceContext = new EnhanceContext(reader,"debug=9", null, null);
    return new ClassMetaReader(enhanceContext);
  }
}
