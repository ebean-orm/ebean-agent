package io.ebean.enhance.common;

import io.ebean.enhance.entity.MessageOutput;
import io.ebean.enhance.querybean.DetectQueryBean;
import io.ebean.enhance.querybean.Distill;
import io.ebean.enhance.transactional.TransactionalMethodKey;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to hold meta data, arguments and log levels for the enhancement.
 */
public class EnhanceContext {

  private static final Logger logger = Logger.getLogger(EnhanceContext.class.getName());

  private final IgnoreClassHelper ignoreClassHelper;

  private final HashMap<String, String> agentArgsMap;

  private final boolean transientInternalFields;

  private final boolean checkNullManyFields;

  private final ClassMetaReader reader;

  private final ClassBytesReader classBytesReader;

  private MessageOutput logout;

  private int logLevel;

  private HashMap<String, ClassMeta> map = new HashMap<>();

  private final DetectQueryBean detectQueryBean;

  private final FilterEntityTransactional filterEntityTransactional;

  private final FilterQueryBean filterQueryBean;

  /**
  * Current profileId when automatically assigned.
  */
  private int autoProfileId;

  private boolean throwOnError;

  private boolean enableProfileLocation;

  /**
  * Mapping of profileId to transactional method descriptions (for decoding profiling).
  */
  private final List<TransactionalMethodKey> profilingKeys = new ArrayList<>();

  public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs, AgentManifest manifest) {
    this(classBytesReader, agentArgs, manifest, new ClassMetaCache());
  }

  /**
  * Construct a context for enhancement.
  */
  public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs, AgentManifest manifest, ClassMetaCache metaCache) {

    this.autoProfileId = manifest.transactionProfilingStart();
    this.enableProfileLocation = manifest.isEnableProfileLocation();

    this.agentArgsMap = ArgParser.parse(agentArgs);
    this.filterEntityTransactional = new FilterEntityTransactional(manifest);
    this.filterQueryBean = new FilterQueryBean(manifest);

    this.detectQueryBean = Distill.convert(manifest.getEntityPackages());
    if (detectQueryBean.isEmpty()) {
      logger.log(Level.FINE, "No ebean.mf detected");
    }

    this.ignoreClassHelper = new IgnoreClassHelper();
    this.logout = new SysoutMessageOutput(System.out);
    this.classBytesReader = classBytesReader;
    this.reader = new ClassMetaReader(this, metaCache);

    String debugValue = agentArgsMap.get("debug");
    if (debugValue != null) {
      try {
        logLevel = Integer.parseInt(debugValue);
      } catch (NumberFormatException e) {
        logger.log(Level.WARNING, "Agent debug argument [" + debugValue + "] is not an int?");
      }
    }

    this.transientInternalFields = getPropertyBoolean("transientInternalFields", manifest.isTransientInternalFields());
    this.checkNullManyFields = getPropertyBoolean("checkNullManyFields", manifest.isCheckNullManyFields());
  }

  public byte[] getClassBytes(String className, ClassLoader classLoader) {
    return classBytesReader.getClassBytes(className, classLoader);
  }

  /**
  * Return true if the owner class is a type query bean.
  * <p>
  * If true typically means the caller needs to change GETFIELD calls to instead invoke the generated
  * 'property access' methods.
  * </p>
  */
  public boolean isQueryBean(String owner) {
    return detectQueryBean.isQueryBean(owner);
  }

  /**
  * Return a value from the entity arguments using its key.
  */
  private String getProperty(String key) {
    return agentArgsMap.get(key.toLowerCase());
  }

  private boolean getPropertyBoolean(String key, boolean defaultValue) {
    String s = getProperty(key);
    if (s == null) {
      return defaultValue;
    } else {
      return s.trim().equalsIgnoreCase("true");
    }
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
  * Read the class meta data for a super class.
  * <p>
  * Typically used to read meta data for inheritance hierarchy.
  * </p>
  */
  public ClassMeta getSuperMeta(String superClassName, ClassLoader classLoader) {

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
  * Read the class meta data for an interface.
  * <p>
  * Typically used to check the interface to see if it is transactional.
  * </p>
  */
  public ClassMeta getInterfaceMeta(String interfaceClassName, ClassLoader classLoader) {

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
    map.put(meta.getClassName(), meta);
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
  public int getLogLevel() {
    return logLevel;
  }

  /**
  * Return true if internal ebean fields in entity classes should be transient.
  */
  public boolean isTransientInternalFields() {
    return transientInternalFields;
  }

  /**
  * Return true if we should add null checking on *ToMany fields.
  * <p>
  * On getting a many that is null Ebean will create an empty List, Set or Map. If it is a
  * ManyToMany it will turn on Modify listening.
  * </p>
  */
  public boolean isCheckNullManyFields() {
    return checkNullManyFields;
  }

  /**
  * Create a TransactionalMethodKey with (maybe) a profileId.
  */
  public TransactionalMethodKey createMethodKey(String className, String methodName, String methodDesc, int profileId) {

    TransactionalMethodKey key = new TransactionalMethodKey(className, methodName, methodDesc);

    if (autoProfileId == -1) {
      // disabled (including disabling profileIds on @Transactional)
      key.setProfileId(0);
    } else {
      if (profileId == 0 && autoProfileId > 0) {
        // enabled mode automatically setting to the next profileId
        profileId = ++autoProfileId;
      }
      key.setProfileId(profileId);
      if (profileId > 0) {
        // we are only interested in the profiling transactions
        profilingKeys.add(key);
      }
    }

    return key;
  }

  /**
  * Return the profiling transaction keys.
  */
  public List<TransactionalMethodKey> getTransactionProfilingKeys() {
    return profilingKeys;
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
}
