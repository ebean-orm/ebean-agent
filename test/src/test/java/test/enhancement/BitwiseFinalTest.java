package test.enhancement;

import io.ebean.enhance.asm.Opcodes;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

public class BitwiseFinalTest {

  @Test
  public void isFinal() {

    int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;
    assertTrue((access & Opcodes.ACC_FINAL) != 0);

    int a2 = (access ^ Opcodes.ACC_FINAL);
    assertFalse((a2 & Opcodes.ACC_FINAL) != 0);

    int a3 = (access ^ Opcodes.ACC_PUBLIC);
    assertTrue((a3 & Opcodes.ACC_FINAL) != 0);

  }
}
