package test.enhancement;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.avaje.ebean.enhance.agent.ClassMeta;
import com.avaje.ebean.enhance.agent.ClassMetaReader;
import com.avaje.ebean.enhance.agent.ClassPathClassBytesReader;
import com.avaje.ebean.enhance.agent.EnhanceContext;

public class TestClassMetaReader {

  @Test
  public void checkNoEnhanceMappedSuper_hasNoPersistentFields() throws ClassNotFoundException {
    
    ClassMetaReader classMetaReader = createClassMetaReader();
    
    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.NoEnhanceMappedSuper", classLoader);
    
    Assert.assertNotNull(classMeta);
    Assert.assertFalse(classMeta.hasPersistentFields());
    Assert.assertFalse(classMeta.isEntity());
  }
  
  @Test
  public void checkEnhanceMappedSuper_hasPersistentField() throws ClassNotFoundException {
    
    ClassMetaReader classMetaReader = createClassMetaReader();
    
    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.EnhanceMappedSuper", classLoader);
    
    Assert.assertNotNull(classMeta);
    Assert.assertTrue(classMeta.hasPersistentFields());
    Assert.assertTrue(classMeta.isEntity());
  }
  
  @Test
  public void checkEnhanceMappedSuper_hasPersistentFieldId() throws ClassNotFoundException {
    
    ClassMetaReader classMetaReader = createClassMetaReader();
    
    ClassLoader classLoader = this.getClass().getClassLoader();
    ClassMeta classMeta = classMetaReader.get(false, "test.model.EnhanceMappedSuperId", classLoader);
    
    Assert.assertNotNull(classMeta);
    Assert.assertTrue(classMeta.hasPersistentFields());
    Assert.assertTrue(classMeta.isEntity());
  }
  
  private ClassMetaReader createClassMetaReader() {
    
    ClassPathClassBytesReader reader = new ClassPathClassBytesReader(new URL[0]);
    EnhanceContext enhanceContext = new EnhanceContext(reader,"debug=9");
    return new ClassMetaReader(enhanceContext);
  }
}
