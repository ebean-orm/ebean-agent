package io.ebean.enhance.querybean;

import io.ebean.enhance.asm.Opcodes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MethodDescTest {

  @Test
  public void testIsGetter() throws Exception {

    MethodDesc desc = new MethodDesc(Opcodes.ACC_PUBLIC, "getName", "()Lio/ebean/typequery/PString;", null, null);
    assertTrue(desc.isGetter());
    assertEquals(desc.proxyMethodName(), "_name");
  }

}
