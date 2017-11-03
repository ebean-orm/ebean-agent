package test.enhancement;

import io.ebean.Ebean;
import io.ebean.plugin.BeanType;
import io.ebean.plugin.ExpressionPath;
import org.testng.annotations.Test;
import test.model.WithGetterSetter;

import static org.assertj.core.api.Assertions.assertThat;

public class WithGetterSetterTests extends BaseTest {


  @Test
  public void testGetterSetterRoundtrip() {
	  WithGetterSetter bean = new WithGetterSetter();
	  assertThat(bean.log).isEmpty();
	  
	  bean.setName("foo");
	  assertThat(bean.log).containsExactly("setName");
	  
	  Ebean.save(bean);
	  
	  bean = Ebean.find(WithGetterSetter.class, bean.getId());
	  assertThat(bean.log).isEmpty();
	  assertThat(bean.getName()).isEqualTo("foo");
	  assertThat(bean.log).containsExactly("getName");

  }
  @Test
  public void testGetterSetterElPath() {
	  WithGetterSetter bean = new WithGetterSetter();
	  assertThat(bean.log).isEmpty();

	  BeanType<WithGetterSetter> beanType = Ebean.getDefaultServer().getPluginApi().getBeanType(WithGetterSetter.class);
	  ExpressionPath path = beanType.getExpressionPath("name");
	  path.pathGet(bean);
	  assertThat(bean.log).containsExactly("getName");
	  
	  bean.log.clear();
	  
	  path.pathSet(bean, "foo");
	  assertThat(bean.log).containsExactly("setName");

  }
}
