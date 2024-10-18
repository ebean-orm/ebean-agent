package io.ebean.enhance.common;

import io.ebean.enhance.Transformer;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.entity.MessageOutput;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.ebean.enhance.asm.Opcodes.INVOKEINTERFACE;
import static io.ebean.enhance.asm.Opcodes.INVOKEVIRTUAL;
import static io.ebean.enhance.common.EnhanceConstants.C_INTERCEPT_I;
import static io.ebean.enhance.common.EnhanceConstants.C_INTERCEPT_RW;

/**
 * Used to hold metadata, arguments and log levels for the enhancement.
 */
public final class EnhanceContext {

  private static final Logger logger = Logger.getLogger(EnhanceContext.class.getName());

  public enum ProfileLineNumberMode {
    /**
     * Line numbering on profile location when method + queryBean type is not unique
     */
    AUTO,
    /**
     * No line numbering on profile locations
     */
    NONE,
    /**
     * Line numbering on all profile locations
     */
    ALL
  }

  private final AgentManifest manifest;
  private final IgnoreClassHelper ignoreClassHelper;
  private final Map<String, String> agentArgsMap;
  private final ClassMetaReader reader;
  private final ClassBytesReader classBytesReader;
  private MessageOutput logout;
  private int logLevel;
  private final HashMap<String, ClassMeta> map = new HashMap<>();
  private final FilterEntityTransactional filterEntityTransactional;
  private final FilterQueryBean filterQueryBean;
  private final PackageFilter packageFilter;
  private boolean throwOnError;
  private final boolean enableProfileLocation;
  private final boolean enableEntityFieldAccess;
  private final ProfileLineNumberMode profileLineNumberMode;
  private final int accPublic;
  private final int accProtected;
  private final int accPrivate;
  private final int enhancementVersion;
  private SummaryInfo summaryInfo;

  public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs, AgentManifest manifest) {
    this(classBytesReader, agentArgs, manifest, new ClassMetaCache());
  }

  /**
   * Construct a context for enhancement.
   */
  public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs, AgentManifest manifest, ClassMetaCache metaCache) {
    this.manifest = manifest;
    this.enableProfileLocation = manifest.isEnableProfileLocation();
    this.enableEntityFieldAccess = manifest.isEnableEntityFieldAccess();
    this.profileLineNumberMode = manifest.profileLineMode();
    this.accPublic = manifest.accPublic();
    this.accProtected = manifest.accProtected();
    this.accPrivate = manifest.accPrivate();
    this.agentArgsMap = ArgParser.parse(agentArgs);
    this.enhancementVersion = versionOf(manifest);
    this.filterEntityTransactional = new FilterEntityTransactional(manifest);
    this.filterQueryBean = new FilterQueryBean(manifest);
    this.ignoreClassHelper = new IgnoreClassHelper();
    this.logout = new SysoutMessageOutput(System.out);
    this.classBytesReader = classBytesReader;
    this.reader = new ClassMetaReader(this, metaCache);
    this.packageFilter = initPackageFilter(agentArgsMap.get("packages"));

    if (manifest.debugLevel() > -1) {
      logLevel = manifest.debugLevel();
    }
    String debugValue = agentArgsMap.get("debug");
    if (debugValue != null) {
      try {
        logLevel = Integer.parseInt(debugValue);
      } catch (NumberFormatException e) {
        logger.log(Level.WARNING, "Agent debug argument [" + debugValue + "] is not an int?");
      }
    }
    if (logLevel > 0 || propertyBoolean("printversion", false)) {
      System.out.println("ebean-agent version:" + Transformer.getVersion() + " enhancement:" + enhancementVersion + " resources:" + manifest.loadedResources());
    }
  }

  private PackageFilter initPackageFilter(String packages) {
    return packages == null ? null : new PackageFilter(packages);
  }

  private int versionOf(AgentManifest manifest) {
    String ver = agentArgsMap.get("version");
    if (ver != null) {
      return Integer.parseInt(ver);
    }
    return manifest.enhancementVersion();
  }

  public void withClassLoader(ClassLoader loader) {
    if (manifest.readManifest(loader)) {
      if (logLevel > 1) {
        log(null, "loaded entity packages: " + manifest.entityPackages());
      }
    }
  }

  public void setLogLevel(int logLevel) {
    this.logLevel = logLevel;
  }

  @Deprecated
  public String getPackagesSummary() {
    return packagesSummary();
  }

  /**
   * Return the summary of the packages controlling enhancement.
   */
  public String packagesSummary() {
    return "packages entity:" + entityPackages()
      + "  transactional:" + transactionalPackages()
      + "  querybean:" + querybeanPackages()
      + "  profileLocation:" + enableProfileLocation
      + "  version:" + enhancementVersion;
  }

  public Set<String> entityPackages() {
    return manifest.entityPackages();
  }

  public Set<String> transactionalPackages() {
    return manifest.transactionalPackages();
  }

  public Set<String> querybeanPackages() {
    return manifest.querybeanPackages();
  }

  public byte[] classBytes(String className, ClassLoader classLoader) {
    return classBytesReader.getClassBytes(className, classLoader);
  }

  public boolean isEntityBean(String owner) {
    return manifest.isDetectEntityBean(owner);
  }

  /**
   * Return true if the owner class is a type query bean.
   * <p>
   * If true typically means the caller needs to change GETFIELD calls to instead invoke the generated
   * 'property access' methods.
   */
  public boolean isQueryBean(String owner, ClassLoader classLoader) {
    if (manifest.isDetectQueryBean(owner)) {
      try {
        final ClassMeta classMeta = reader.get(true, owner, classLoader);
        if (classMeta == null) {
          // For Gradle Kotlin KAPT the generate query bean bytecode
          // isn't available to the classLoader. Just returning true.
          return true;
        }
        return classMeta.isQueryBean();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }

  /**
   * Return a value from the entity arguments using its key.
   */
  private String property(String key) {
    return agentArgsMap.get(key.toLowerCase());
  }

  private boolean propertyBoolean(String key, boolean defaultValue) {
    String s = property(key);
    if (s == null) {
      return defaultValue;
    } else {
      return s.trim().equalsIgnoreCase("true");
    }
  }

  public boolean isEnableEntityFieldAccess() {
    return enableEntityFieldAccess;
  }

  /**
   * Return true if profile location enhancement is on.
   */
  public boolean isEnableProfileLocation() {
    return enableProfileLocation;
  }

  /**
   * Return true if this class should be scanned for transactional enhancement.
   */
  public boolean detectEntityTransactionalEnhancement(String className) {
    return filterEntityTransactional.detectEnhancement(className);
  }

  /**
   * Return true if this class should be scanned for query bean enhancement.
   */
  public boolean detectQueryBeanEnhancement(String className) {
    return filterQueryBean.detectEnhancement(className);
  }

  /**
   * Return true if this class should be ignored. That is JDK classes and
   * known libraries JDBC drivers etc can be skipped.
   */
  public boolean isIgnoreClass(String className) {
    if (packageFilter != null && packageFilter.ignore(className)) {
      return true;
    }
    return ignoreClassHelper.isIgnoreClass(className);
  }

  /**
   * Change the logout to something other than system out.
   */
  public void setLogout(MessageOutput logout) {
    this.logout = logout;
  }

  /**
   * Create a new meta object for enhancing a class.
   */
  public ClassMeta createClassMeta() {
    return new ClassMeta(this, logLevel, logout);
  }

  /**
   * Read the class metadata for a super class.
   * <p>
   * Typically used to read meta data for inheritance hierarchy.
   */
  public ClassMeta superMeta(String superClassName, ClassLoader classLoader) {
    try {
      if (isIgnoreClass(superClassName)) {
        return null;
      }
      return reader.get(false, superClassName, classLoader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Read the class metadata for an interface.
   * <p>
   * Typically used to check the interface to see if it is transactional.
   */
  public ClassMeta interfaceMeta(String interfaceClassName, ClassLoader classLoader) {
    try {
      if (isIgnoreClass(interfaceClassName)) {
        return null;
      }
      return reader.get(true, interfaceClassName, classLoader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void addClassMeta(ClassMeta meta) {
    map.put(meta.className(), meta);
  }

  public ClassMeta get(String className) {
    return map.get(className);
  }

  /**
   * Log some debug output.
   */
  public void log(int level, String className, String msg) {
    if (logLevel >= level) {
      log(className, msg);
    }
  }

  public void log(String className, String msg) {
    if (className != null) {
      msg = "cls: " + className + "  msg: " + msg;
    }
    logout.println("ebean-enhance> " + msg);
  }

  public boolean isLog(int level) {
    return logLevel >= level;
  }

  /**
   * Log an error.
   */
  public void log(Throwable e) {
    e.printStackTrace(
      new PrintStream(new ByteArrayOutputStream()) {
        @Override
        public void print(String message) {
          logout.println(message);
        }

        @Override
        public void println(String message) {
          logout.println(message);
        }
      });
  }


  /**
   * Return the log level.
   */
  public int logLevel() {
    return logLevel;
  }

  public boolean isTransientInit() {
    return manifest.isTransientInit();
  }

  public boolean isTransientInitThrowError() {
    return manifest.isTransientInitThrowError();
  }

  /**
   * Return true if internal ebean fields in entity classes should be transient.
   */
  public boolean isTransientInternalFields() {
    return manifest.isTransientInternalFields();
  }

  /**
   * Return true if we should add null checking on *ToMany fields.
   * <p>
   * On getting a many that is null Ebean will create an empty List, Set or Map. If it is a
   * ManyToMany it will turn on Modify listening.
   */
  public boolean isCheckNullManyFields() {
    return manifest.isCheckNullManyFields();
  }

  public boolean isAllowNullableDbArray() {
    return manifest.isAllowNullableDbArray();
  }

  /**
   * Return true if transform should throw exception rather than log and return null.
   */
  public boolean isThrowOnError() {
    return throwOnError;
  }

  /**
   * Set to true if you want transform to throw exceptions rather than return null.
   */
  public void setThrowOnError(boolean throwOnError) {
    this.throwOnError = throwOnError;
  }

  /**
   * Turn on the summary collection of the enhancement.
   */
  public void collectSummary() {
    this.summaryInfo = new SummaryInfo(manifest.loadedResources());
  }

  /**
   * Add the transactional enhanced class to summary information.
   */
  public void summaryTransactional(String className) {
    if (summaryInfo != null) {
      summaryInfo.addTransactional(className);
    }
  }

  /**
   * Add the entity enhanced class to summary information.
   */
  public void summaryEntity(String className) {
    if (summaryInfo != null) {
      summaryInfo.addEntity(className);
    }
  }

  /**
   * Add the query bean enhanced class to summary information.
   */
  public void summaryQueryBean(String className) {
    if (summaryInfo != null) {
      summaryInfo.addQueryBean(className);
    }
  }

  /**
   * Add the query bean caller enhanced class to summary information.
   */
  public void summaryQueryBeanCaller(String className) {
    if (summaryInfo != null) {
      summaryInfo.addQueryBeanCaller(className);
    }
  }

  /**
   * Add the enhanced class with field access replacement to summary information.
   */
  public void summaryFieldAccessUser(String className) {
    if (summaryInfo != null) {
      summaryInfo.addFieldAccessUser(className);
    }
  }

  @Deprecated
  public SummaryInfo getSummaryInfo() {
    return summaryInfo();
  }

  /**
   * Return the summary of the enhancement.
   * <p>
   * Note that <code>collectSummary()</code> must be called in order for summary
   * information to be collected and returned here.
   */
  public SummaryInfo summaryInfo() {
    return summaryInfo.prepare();
  }

  public int accPublic() {
    return accPublic;
  }

  public int accProtected() {
    return accProtected;
  }

  public int accPrivate() {
    return accPrivate;
  }

  public boolean isToManyGetField() {
    return enhancementVersion > 128;
  }

  public boolean isEnhancedToString() {
    return enhancementVersion > 132;
  }

  public String interceptNew() {
    return enhancementVersion >= 140 ? C_INTERCEPT_RW : C_INTERCEPT_I;
  }

  public void visitMethodInsnIntercept(MethodVisitor mv, String name, String desc) {
    mv.visitMethodInsn(interceptInvoke(), C_INTERCEPT_I, name, desc, interceptIface());
  }

  private int interceptInvoke() {
    return enhancementVersion >= 140 ? INVOKEINTERFACE : INVOKEVIRTUAL;
  }

  private boolean interceptIface() {
    return enhancementVersion >= 140;
  }

  public boolean interceptAddReadOnly() {
    return enhancementVersion >= 141;
  }

  public boolean supportsProfileWithLine() {
    return enhancementVersion >= 143; // Ebean 13.13.1 onwards
  }

  public boolean improvedQueryBeans() {
    return enhancementVersion >= 145;
  }

  public boolean fluidQueryBuilders() {
    return enhancementVersion >= 148;
  }

  public ProfileLineNumberMode profileLineMode() {
    return profileLineNumberMode;
  }
}
