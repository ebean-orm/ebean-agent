package test.enhancement;

import test.model.normalize.ClassNormalizeModel;
import test.model.normalize.FieldNormalizeModel;
import test.model.normalize.InheritClassNormalizeModel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NormalizeTest extends BaseTest {


  @Test
  public void testClassNormalize() {
    ClassNormalizeModel m = new ClassNormalizeModel();

    m.setName(null);
    assertEquals(m.getName(), null);

    m.setName("   Hello   ");
    assertEquals(m.getName(), "Hello");


    m.setNoNormalize("   Hello   ");
    assertEquals(m.getNoNormalize(), "   Hello   ");

    m.setZerosNormalize("00001234");
    assertEquals(m.getZerosNormalize(), "1234");

    m.setZerosNormalize("   00001234   ");
    // only trim leading zeros
    assertEquals(m.getZerosNormalize(), "   00001234   ");

    m.setBothNormalize("   00001234   ");
    // first trim spaces, then trim leading zeros.
    assertEquals(m.getBothNormalize(), "1234");
  }

  @Test
  public void testFieldNormalize() {
    FieldNormalizeModel m = new FieldNormalizeModel();

    m.setName(null);
    assertEquals(m.getName(), null);

    m.setName("   Hello   ");
    assertEquals(m.getName(), "Hello");

    m.setEvenNumberOnly(1);
    assertEquals(m.getEvenNumberOnly(), 0);

    m.setEvenNumberOnly(2);
    assertEquals(m.getEvenNumberOnly(), 2);

    m.setEvenNumberOnly(3);
    assertEquals(m.getEvenNumberOnly(), 2);

    m.setEvenNumberOnly(4);
    assertEquals(m.getEvenNumberOnly(), 4);
}

  @Test
  public void testInheritClassNormalize() {
    InheritClassNormalizeModel m = new InheritClassNormalizeModel();

    m.setName(null);
    assertEquals(m.getName(), null);

    m.setName("   Hello   ");
    assertEquals(m.getName(), "Hello");
  }
}
