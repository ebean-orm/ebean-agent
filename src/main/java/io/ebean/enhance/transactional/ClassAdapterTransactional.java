package io.ebean.enhance.transactional;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.Label;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.common.AlreadyEnhancedException;
import io.ebean.enhance.common.AnnotationInfo;
import io.ebean.enhance.common.AnnotationInfoVisitor;
import io.ebean.enhance.common.ClassMeta;
import io.ebean.enhance.common.EnhanceConstants;
import io.ebean.enhance.common.EnhanceContext;
import io.ebean.enhance.common.NoEnhancementRequiredException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.ebean.enhance.asm.Opcodes.ACC_PRIVATE;
import static io.ebean.enhance.asm.Opcodes.ACC_STATIC;
import static io.ebean.enhance.asm.Opcodes.BIPUSH;
import static io.ebean.enhance.asm.Opcodes.INVOKESTATIC;
import static io.ebean.enhance.asm.Opcodes.PUTSTATIC;
import static io.ebean.enhance.asm.Opcodes.RETURN;

/**
 * ClassAdapter used to add transactional support.
 */
public class ClassAdapterTransactional extends ClassVisitor {

  private static final Logger logger = Logger.getLogger(ClassAdapterTransactional.class.getName());

  static final String QP_FIELD_PREFIX = "_$ebpq";

  static final String TX_FIELD_PREFIX = "_$ebpt";

  static final String IO_EBEAN_FINDER = "io/ebean/Finder";

  private final Set<String> transactionalMethods = new LinkedHashSet<>();

  private final Set<Integer> transactionalLineNumbers = new LinkedHashSet<>();

  private final EnhanceContext enhanceContext;

  private final ClassLoader classLoader;

  private ArrayList<ClassMeta> transactionalInterfaces = new ArrayList<>();

  /**
  * Class level annotation information.
  */
  private AnnotationInfo classAnnotationInfo;

  private String className;

  private boolean existingStaticInitialiser;

  private boolean finder;

  private int queryProfileCount;

  private int transactionProfileCount;

  private Map<Integer,String> txLabels = new LinkedHashMap<>();

  public ClassAdapterTransactional(ClassVisitor cv, ClassLoader classLoader, EnhanceContext context) {
    super(Opcodes.ASM7, cv);
    this.classLoader = classLoader;
    this.enhanceContext = context;
  }

  public String className() {
    return className;
  }

  public boolean isLog(int level) {
    return enhanceContext.isLog(level);
  }

  public void log(String msg) {
    enhanceContext.log(className, msg);
  }

  public AnnotationInfo getClassAnnotationInfo() {
    return classAnnotationInfo;
  }

  /**
  * Returns Transactional information from a matching interface method.
  * <p>
  * Returns null if no matching (transactional) interface method was found.
  * </p>
  */
  public AnnotationInfo getInterfaceTransactionalInfo(String methodName, String methodDesc) {

    AnnotationInfo interfaceAnnotationInfo = null;

    for (int i = 0; i < transactionalInterfaces.size(); i++) {
      ClassMeta interfaceMeta = transactionalInterfaces.get(i);
      AnnotationInfo ai = interfaceMeta.getInterfaceTransactionalInfo(methodName, methodDesc);
      if (ai != null) {
        if (interfaceAnnotationInfo != null) {
          String msg = "Error in [" + className + "] searching the transactional interfaces ["
              + transactionalInterfaces + "] found more than one match for the transactional method:"
              + methodName + " " + methodDesc;

          logger.log(Level.SEVERE, msg);

        } else {
          interfaceAnnotationInfo = ai;
          if (isLog(2)) {
            log("inherit transactional from interface [" + interfaceMeta + "] method[" + methodName + " "
                + methodDesc + "]");
          }
        }
      }
    }

    return interfaceAnnotationInfo;
  }

  /**
  * Visit the class with interfaces.
  */
  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

    className = name;
    finder = superName.equals(IO_EBEAN_FINDER);

    // Note: interfaces can be an empty array but not null
    int n = 1 + interfaces.length;
    String[] newInterfaces = new String[n];
    for (int i = 0; i < interfaces.length; i++) {
      newInterfaces[i] = interfaces[i];
      if (newInterfaces[i].equals(EnhanceConstants.C_ENHANCEDTRANSACTIONAL)) {
        throw new AlreadyEnhancedException(name);
      }
      ClassMeta interfaceMeta = enhanceContext.getInterfaceMeta(newInterfaces[i], classLoader);
      if (interfaceMeta != null && interfaceMeta.isTransactional()) {
        // the interface was transactional. We gather its information
        // because our methods inherit that transactional configuration
        transactionalInterfaces.add(interfaceMeta);

        if (isLog(6)) {
          log(" implements transactional interface " + interfaceMeta.getDescription());
        }
      }
    }

    // Add the EnhancedTransactional interface
    newInterfaces[newInterfaces.length - 1] = EnhanceConstants.C_ENHANCEDTRANSACTIONAL;

    super.visit(version, access, name, signature, superName, newInterfaces);
  }

  /**
  * Visit class level annotations.
  */
  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

    AnnotationVisitor av = super.visitAnnotation(desc, visible);

    if (desc.equals(EnhanceConstants.AVAJE_TRANSACTIONAL_ANNOTATION)) {
      // we have class level Transactional annotation
      // which will act as default for all methods in this class
      classAnnotationInfo = new AnnotationInfo(null);
      return new AnnotationInfoVisitor(null, classAnnotationInfo, av);

    } else {
      return av;
    }
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    if (name.startsWith("_$ebp") && desc.equals("Lio/ebean/ProfileLocation;")) {
      throw new AlreadyEnhancedException(className);
    }
    return super.visitField(access, name, desc, signature, value);
  }

  /**
  * Visit the methods specifically looking for method level transactional
  * annotations.
  */
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    if (name.equals("_$initProfileLocations")) {
      throw new AlreadyEnhancedException(className);
    }
    if (name.equals("<init>")) {
      // not enhancing constructors
      return mv;
    }
    if (name.equals("<clinit>")) {
      if (!enhanceContext.isEnableProfileLocation()) {
        // not enhancing class static initialiser
        return mv;
      } else {
        if (isLog(3)) {
          log("... <clinit> exists - adding call to _$initProfileLocations()");
        }
        existingStaticInitialiser = true;
        return new StaticInitAdapter(mv, access, name, desc, className);
      }
    }

    return new ScopeTransAdapter(this, mv, access, name, desc);
  }

  @Override
  public void visitEnd() {
    if (queryProfileCount == 0 && transactionProfileCount == 0) {
      throw new NoEnhancementRequiredException(className);
    }
    if (isLog(2)) {
      log("methods:" + transactionalMethods + " qp:" + queryProfileCount + " tp:" + transactionProfileCount + " profileLocation:" + isEnableProfileLocation());
    }
    if (enhanceContext.isEnableProfileLocation()) {
      addStaticFieldDefinitions();
      addStaticFieldInitialisers();
      if (!existingStaticInitialiser) {
        if (isLog(5)) {
          log("... add <clinit> to call _$initProfileLocations()");
        }
        addStaticInitialiser();
      }
    }
    super.visitEnd();
  }

  private void addStaticFieldDefinitions() {
    for (int i = 0; i < queryProfileCount; i++) {
      FieldVisitor fv = cv.visitField(ACC_PRIVATE + ACC_STATIC, QP_FIELD_PREFIX+i, "Lio/ebean/ProfileLocation;", null, null);
      fv.visitEnd();
    }
    for (int i = 0; i < transactionProfileCount; i++) {
      FieldVisitor fv = cv.visitField(ACC_PRIVATE + ACC_STATIC, TX_FIELD_PREFIX+i, "Lio/ebean/ProfileLocation;", null, null);
      fv.visitEnd();
    }
  }

  private void addStaticFieldInitialisers() {
    MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, "_$initProfileLocations", "()V", null, null);
    mv.visitCode();

    for (int i = 0; i < queryProfileCount; i++) {
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(1, l0);
      mv.visitMethodInsn(INVOKESTATIC, "io/ebean/ProfileLocation", "create", "()Lio/ebean/ProfileLocation;", true);
      mv.visitFieldInsn(PUTSTATIC, className, QP_FIELD_PREFIX+i, "Lio/ebean/ProfileLocation;");
    }

    boolean withLineNumbers = (transactionProfileCount == transactionalLineNumbers.size());
    List<Integer> lineNumbers = new ArrayList<>(transactionalLineNumbers);

    for (int i = 0; i < transactionProfileCount; i++) {
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(2, l0);
      if (withLineNumbers) {
        int txnLineNumber = lineNumbers.get(i);
        mv.visitIntInsn(BIPUSH, txnLineNumber);
        String label = getTxnLabel(i);
        mv.visitLdcInsn(label);
        mv.visitMethodInsn(INVOKESTATIC, "io/ebean/ProfileLocation", "create", "(ILjava/lang/String;)Lio/ebean/ProfileLocation;", true);

      } else {
        mv.visitMethodInsn(INVOKESTATIC, "io/ebean/ProfileLocation", "create", "()Lio/ebean/ProfileLocation;", true);
      }
      mv.visitFieldInsn(PUTSTATIC, className, TX_FIELD_PREFIX + i, "Lio/ebean/ProfileLocation;");
    }

    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(3, l1);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 0);
    mv.visitEnd();
  }

  private String getTxnLabel(int i) {
    String label = txLabels.get(i);
    return (label != null) ? label : "";
  }

  /**
  * Add a static initialization block when there was not one on the class.
  */
  private void addStaticInitialiser() {

    MethodVisitor mv = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(4, l0);
    mv.visitMethodInsn(INVOKESTATIC, className, "_$initProfileLocations", "()V", false);
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(5, l1);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  void transactionalMethod(TransactionalMethodKey methodKey) {

    transactionalLineNumbers.add(methodKey.getLineNumber());
    transactionalMethods.add(methodKey.getMethodName());
    if (isLog(3)) {
      log("method - " + methodKey);
    }
  }

  /**
  * Create and return the TransactionalMethodKey.
  *
  * Takes into account the profiling mode (as per manifest) and explicit profileId.
  */
  public TransactionalMethodKey createMethodKey(String methodName, String methodDesc, int profId) {
    return enhanceContext.createMethodKey(className, methodName, methodDesc, profId);
  }

  /**
  * Return true if profile location enhancement is on.
  */
  public boolean isEnableProfileLocation() {
    return enhanceContext.isEnableProfileLocation();
  }

  /**
  * Return the next index for query profile location.
  */
  int nextQueryProfileLocation() {
    return queryProfileCount++;
  }

  /**
  * Return the next index for transaction profile location.
  */
  int nextTransactionLocation() {
    return transactionProfileCount++;
  }

  /**
  * Return true if this enhancing class extends Ebean Finder and we have profile location enabled.
  */
  public boolean isFinderProfileLocation() {
    return finder && isEnableProfileLocation();
  }

  /**
  * Set the transaction label for a given index.
  */
  public void putTxnLabel(int locationField, String txLabel) {
    txLabels.put(locationField, txLabel);
  }
}
