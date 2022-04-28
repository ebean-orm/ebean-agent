package io.ebean.enhance.common;

import io.ebean.enhance.asm.ClassReader;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.ClassWriter;
import io.ebean.enhance.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.ebean.enhance.Transformer.EBEAN_ASM_VERSION;

/**
 * ClassWriter without class loading. Fixes problems on dynamic enhancement mentioned here:
 * <a href="https://github.com/ebean-orm/ebean-agent/issues/59">https://github.com/ebean-orm/ebean-agent/issues/59</a>
 *
 * Idea taken from here:
 *
 * <a href="https://github.com/zygote1984/AspectualAdapters/blob/master/ALIA4J-NOIRIn-all/src/org/alia4j/noirin/transform/ClassWriterWithoutClassLoading.java">https://github.com/zygote1984/AspectualAdapters/blob/master/ALIA4J-NOIRIn-all/src/org/alia4j/noirin/transform/ClassWriterWithoutClassLoading.java</a>
 *
 * @author praml
 */
public class ClassWriterWithoutClassLoading extends ClassWriter {

  private final Map<String, Set<String>> type2instanceOfs = new HashMap<>();
  private final Map<String, String> type2superclass = new HashMap<>();
  private final Map<String, Boolean> type2isInterface = new HashMap<>();
  private final ClassLoader classLoader;
  private final List<CommonSuperUnresolved> unresolved = new ArrayList<>();

  public ClassWriterWithoutClassLoading(ClassReader classReader, int flags, ClassLoader classLoader) {
    super(classReader, flags);
    this.classLoader = classLoader;
  }

  public ClassWriterWithoutClassLoading(int flags, ClassLoader classLoader) {
    super(flags);
    this.classLoader = classLoader;
  }

  public List<CommonSuperUnresolved> getUnresolved() {
    return unresolved;
  }

  /**
  * Returns the common super type of the two given types.
  *
  * @param type1 the internal name of a class.
  * @param type2 the internal name of another class.
  * @return the internal name of the common super class of the two given classes.
  */
  @Override
  protected String getCommonSuperClass(final String type1, final String type2) {
    try {
      if (getInstanceOfs(type2).contains(type1)) {
        return type1;
      }
      if (getInstanceOfs(type1).contains(type2)) {
        return type2;
      }
      if (isInterface(type1) || isInterface(type2)) {
        return "java/lang/Object";
      } else {
        String type = type1;
        do {
          type = getSuperclass(type);
        } while (!getInstanceOfs(type2).contains(type));
        return type;
      }
    } catch (Exception e) {
      unresolved.add(new CommonSuperUnresolved(type1, type2, e.toString()));
      return "java/lang/Object";
    }
  }

  private String getSuperclass(String type) {
    if (!type2superclass.containsKey(type)) {
      initializeTypeHierarchyFor(type);
    }
    return type2superclass.get(type);
  }

  private boolean isInterface(String type) {
    if (!type2isInterface.containsKey(type)) {
      initializeTypeHierarchyFor(type);
    }
    return type2isInterface.get(type);
  }

  private Set<String> getInstanceOfs(String type) {
    if (!type2instanceOfs.containsKey(type)) {
      initializeTypeHierarchyFor(type);
    }
    return type2instanceOfs.get(type);
  }

  /**
  * Here we read the class at bytecode-level.
  */
  private void initializeTypeHierarchyFor(final String internalTypeName) {
    if (classLoader == null) {
      // Bug in Zulu JDK for jdk classes (which we should skip anyway)
      throw new IllegalStateException("ClassLoader is null?");
    }
    try (InputStream classBytes = classLoader.getResourceAsStream(internalTypeName + ".class")){
      ClassReader classReader = new ClassReader(classBytes);
      classReader.accept(new ClassVisitor(EBEAN_ASM_VERSION) {

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
          super.visit(version, access, name, signature, superName, interfaces);
          type2superclass.put(internalTypeName, superName);
          type2isInterface.put(internalTypeName, (access & Opcodes.ACC_INTERFACE) > 0);

          Set<String> instanceOfs = new HashSet<>();
          instanceOfs.add(internalTypeName); // we are instance of ourself
          if (superName != null) {
            instanceOfs.add(superName);
            instanceOfs.addAll(getInstanceOfs(superName));
          }
          for (String superInterface : interfaces) {
            instanceOfs.add(superInterface);
            instanceOfs.addAll(getInstanceOfs(superInterface));
          }
          type2instanceOfs.put(internalTypeName, instanceOfs);
        }
      }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
