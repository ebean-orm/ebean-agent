package test.enhancement;

import java.lang.reflect.Field;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import test.model.BaseEntity;
import test.model.Customer;

import com.avaje.ebean.bean.EntityBean;

public class TestBaseEntity {

  @Test
  public void test() {
    
    BaseEntity baseEntity = new BaseEntity();
    
    Assert.assertTrue(baseEntity instanceof EntityBean);
    
    EntityBean eb = (EntityBean)baseEntity;
    String[] fieldNames = eb._ebean_getPropertyNames();
    
    System.out.println(Arrays.toString(fieldNames));
    
    
    Field field;
    try {
      field = BaseEntity.class.getField("_ebean_props");
      Object propertyNames = field.get(null);
      System.out.println(""+propertyNames);
    } catch (Exception e) {
      Assert.fail();
    } 
    
    Customer customer = new Customer();
    Assert.assertTrue(customer instanceof EntityBean);
    
    EntityBean custEb = (EntityBean)customer;
    String[] custFieldNames = custEb._ebean_getPropertyNames();
    
    String[] custProps = custEb._ebean_getPropertyNames();
    String  custProp0 = custEb._ebean_getPropertyName(0);
    
    System.out.println(Arrays.toString(custFieldNames));
    
    baseEntity.setId(23l);
    boolean[] changed = eb._ebean_getIntercept().getChanged();

    baseEntity.setVersion(1l);
    boolean[] changed2 = eb._ebean_getIntercept().getChanged();
    
    int hc = baseEntity.hashCode();
    Assert.assertTrue(hc > 0);
    
    BaseEntity baseEntity2 = new BaseEntity();
    EntityBean baseEntity2EB = (EntityBean)baseEntity2;
    baseEntity2EB._ebean_setField(1, 34L);
    baseEntity2EB._ebean_setField(0, 12L);
    
    Assert.assertEquals(Long.valueOf(34), baseEntity2.getVersion());
    
    Object versionVal = baseEntity2EB._ebean_getField(1);
    Assert.assertEquals(Long.valueOf(34), versionVal);

    Object idVal = baseEntity2EB._ebean_getField(0);
    Assert.assertEquals(Long.valueOf(12), idVal);

    boolean[] changed3 = baseEntity2EB._ebean_getIntercept().getChanged();
    
    System.out.println("done");
  }
  
}
