package com.avaje.ebean.enhance.agent;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to hold meta data, arguments and log levels for the enhancement.
 */
public class EnhanceContext {

	private static final Logger logger = Logger.getLogger(EnhanceContext.class.getName());

	private final IgnoreClassHelper ignoreClassHelper;

	private final HashMap<String, String> agentArgsMap;

	private final boolean readOnly;

	private final boolean transientInternalFields;

  private final boolean checkNullManyFields;

	private final ClassMetaReader reader;

	private final ClassBytesReader classBytesReader;

	private PrintStream logout;

	private int logLevel;

	private HashMap<String, ClassMeta> map = new HashMap<String, ClassMeta>();

	/**
	 * Construct a context for enhancement.
	 */
	public EnhanceContext(ClassBytesReader classBytesReader, String agentArgs) {

		this.agentArgsMap = ArgParser.parse(agentArgs);
    this.ignoreClassHelper = new IgnoreClassHelper(agentArgsMap.get("packages"));

		this.logout = System.out;

		this.classBytesReader = classBytesReader;
		this.reader = new ClassMetaReader(this);

		String debugValue = agentArgsMap.get("debug");
		if (debugValue != null) {
			try {
				logLevel = Integer.parseInt(debugValue);
			} catch (NumberFormatException e) {
				String msg = "Agent debug argument [" + debugValue+ "] is not an int?";
				logger.log(Level.WARNING, msg);
			}
		}

    this.readOnly = getPropertyBoolean("readonly", false);
    this.transientInternalFields = getPropertyBoolean("transientInternalFields", false);
    this.checkNullManyFields = getPropertyBoolean("checkNullManyFields", true);
	}

	public byte[] getClassBytes(String className, ClassLoader classLoader){
		return classBytesReader.getClassBytes(className, classLoader);
	}
	
	/**
	 * Return a value from the agent arguments using its key.
	 */
	public String getProperty(String key){
		return agentArgsMap.get(key.toLowerCase());
	}

	public boolean getPropertyBoolean(String key, boolean dflt){
		String s = getProperty(key);
		if (s == null){
			return dflt;
		} else {
			return s.trim().equalsIgnoreCase("true");
		}
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
	public void setLogout(PrintStream logout) {
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
			if (isIgnoreClass(superClassName)){
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
			if (isIgnoreClass(interfaceClassName)){
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
	
	public boolean isLog(int level){
		return logLevel >= level;
	}

	/**
	 * Log an error.
	 */
	public void log(Throwable e) {
		e.printStackTrace(logout);
	}

	/**
	 * Return the log level.
	 */
	public int getLogLevel() {
		return logLevel;
	}

	/**
	 * Return true if this should go through the enhancement process but not
	 * actually save the enhanced classes.
	 * <p>
	 * Set this to true to run through the enhancement process without actually
	 * doing the enhancement for debugging etc.
	 * </p>
	 */
	public boolean isReadOnly() {
		return readOnly;
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

}
