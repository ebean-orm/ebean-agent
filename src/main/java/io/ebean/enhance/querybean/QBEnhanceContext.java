package io.ebean.enhance.querybean;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Used to hold meta data, arguments and log levels for the enhancement.
 */
public class QBEnhanceContext {

	private final IgnoreClassHelper ignoreClassHelper;

	private final DetectQueryBean detectQueryBean;

	private MessageListener messageListener;

	private int logLevel;

	/**
	 * Construct a context for enhancement.
	 */
	public QBEnhanceContext(String agentArgs, ClassLoader classLoader, Set<String> initialPackages) {

    this.messageListener = new DefaultMessageListener();

    this.detectQueryBean = Distill.convert(AgentManifestReader.read(classLoader, initialPackages));
    if (detectQueryBean.isEmpty()) {
      System.err.println("---------------------------------------------------------------------------------------------");
      System.err.println("QueryBean Agent: No packages containing query beans - Missing ebean.mf files? this won't work.");
      System.err.println("---------------------------------------------------------------------------------------------");
    }

    HashMap<String, String> agentArgsMap = ArgParser.parse(agentArgs);
    String[] packages = Distill.parsePackages(agentArgsMap.get("packages"));
    if (packages.length > 0) {
      String[] all = Distill.mergePackages(detectQueryBean.getPackages(), packages);
      this.ignoreClassHelper = new IgnoreClassHelper(all);
    } else {
      // no explicit packages (so use built in ignores)
      this.ignoreClassHelper = new IgnoreClassHelper(new String[0]);
    }

    String debugValue = agentArgsMap.get("debug");
    if (debugValue != null) {
      try {
        logLevel = Integer.parseInt(debugValue.trim());
      } catch (NumberFormatException e) {
        System.err.println("QueryBean Agent: debug argument [" + debugValue + "] is not an int? ignoring.");
      }
    }
    if (logLevel > 1) {
      log(1, "QueryBean Agent: entity bean packages", detectQueryBean.toString());
      log(1, "QueryBean Agent: application packages", Arrays.toString(packages));
    }
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
	 * Return true if this class should be ignored. That is JDK classes and
	 * known libraries JDBC drivers etc can be skipped.
	 */
	public boolean isIgnoreClass(String className) {
		return ignoreClassHelper.isIgnoreClass(className);
	}

	/**
	 * Change the message listener.
	 */
	void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	/**
	 * Log some debug output.
	 */
	public void log(int level, String msg, String extra) {
		if (logLevel >= level) {
			messageListener.debug(msg + extra);
		}
	}
	
	public void log(String className, String msg) {
		if (className != null) {
			msg = "cls: " + className + "  msg: " + msg;
		}
		messageListener.debug("querybean-enhance> " + msg);
	}
	
	public boolean isLog(int level){
		return logLevel >= level;
	}

	/**
	 * Return the log level.
	 */
	public int getLogLevel() {
		return logLevel;
	}

	private static class DefaultMessageListener implements MessageListener {

		private final PrintStream out = System.out;

		DefaultMessageListener() {
		}

		@Override
		public void debug(String message) {
			out.println(message);
		}

	}
}
