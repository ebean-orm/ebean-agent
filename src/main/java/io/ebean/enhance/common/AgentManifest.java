package io.ebean.enhance.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Reads all the META-INF/ebean.mf and META-INF/ebean-typequery.mf resources with the locations
 * of all the entity beans (and hence locations of query beans).
 */
public class AgentManifest {

  enum TxProfileMode {
    NONE,
    ENABLED,
    MANUAL
  }

  private final Set<String> entityPackages = new HashSet<>();

  private final Set<String> transactionalPackages = new HashSet<>();

  private final Set<String> querybeanPackages = new HashSet<>();

  private TxProfileMode transactionProfilingMode = TxProfileMode.NONE;

  /**
  * Start profileId when automatically assigned by enhancement.
  */
  private int transactionProfilingStart = 1000;

  private int debugLevel = -1;

  private boolean transientInternalFields;

  private boolean checkNullManyFields = true;

  private boolean enableProfileLocation;

  private boolean enableQueryAutoLabel;

  public static AgentManifest read(ClassLoader classLoader, Set<String> initialPackages) {

    try {
      return new AgentManifest(initialPackages)
          .readManifests(classLoader, "META-INF/ebean-typequery.mf")
          .readManifests(classLoader, "META-INF/ebean.mf")
          .readManifests(classLoader, "ebean.mf");

    } catch (IOException e) {
      // log to standard error and return empty
      System.err.println("Agent: error reading ebean manifest resources");
      e.printStackTrace();
      return new AgentManifest();
    }
  }

  /**
  * Construct with some packages defined externally.
  */
  public AgentManifest(Set<String> initialPackages) {
    if (initialPackages != null) {
      entityPackages.addAll(initialPackages);
    }
  }

  /**
  * Construct with no initial packages (to use with addRaw()).
  */
  private AgentManifest() {
  }

  @Override
  public String toString() {
    return "entityPackages:" + entityPackages + " querybeanPackages:" + querybeanPackages
      + " transactionalPackages:" + transactionalPackages + " profilingMode:" + transactionProfilingMode;
  }

  /**
  * Return true if enhancement of profileLocations should be added.
  */
  public boolean isEnableProfileLocation() {
    return enableProfileLocation;
  }

  /**
   * Return true if enhancement should add labels to query bean queries.
   */
  public boolean isEnableQueryAutoLabel() {
    return enableQueryAutoLabel;
  }

  /**
  * Return the initial starting profileId when automatically assigned.
  */
  int transactionProfilingStart() {
    switch (transactionProfilingMode) {
      case NONE:
        return -1;
      case MANUAL:
        return 0;
      case ENABLED:
        return transactionProfilingStart;
      default: {
        return transactionProfilingStart;
      }
    }
  }

  /**
   * Return the debug level read from ebean.mf
   */
  public int getDebugLevel() {
    return debugLevel;
  }

  /**
  * Return the parsed set of packages that type query beans are in.
  */
  public Set<String> getEntityPackages() {
    return entityPackages;
  }

  /**
  * Return true if transactional enhancement is turned off.
  */
  public boolean isTransactionalNone() {
    return transactionalPackages.contains("none") && transactionalPackages.size() == 1;
  }

  /**
  * Return true if we should use transient internal fields.
  */
  public boolean isTransientInternalFields() {
    return transientInternalFields;
  }

  /**
  * Return false if enhancement should skip checking for null many fields.
  */
  public boolean isCheckNullManyFields() {
    return checkNullManyFields;
  }

  /**
  * Return true if query bean enhancement is turned off.
  */
  public boolean isQueryBeanNone() {
    return querybeanPackages.contains("none") && querybeanPackages.size() == 1;
  }

  /**
  * Return the packages that should be enhanced for transactional.
  * An empty set means all packages are scanned for transaction classes and methods.
  */
  public Set<String> getTransactionalPackages() {
    return transactionalPackages;
  }

  /**
  * Return the packages that should be enhanced for query bean use.
  * An empty set means all packages are scanned for transaction classes and methods.
  */
  public Set<String> getQuerybeanPackages() {
    return querybeanPackages;
  }

  /**
  * Read all the specific manifest files and return the set of packages containing type query beans.
  */
  AgentManifest readManifests(ClassLoader classLoader, String path) throws IOException {
    Enumeration<URL> resources = classLoader.getResources(path);
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      try {
        addResource(UrlHelper.openNoCache(url));
      } catch (IOException e) {
        System.err.println("Error reading manifest resources " + url);
        e.printStackTrace();
      }
    }
    return this;
  }

  /**
  * Add given the manifest InputStream.
  */
  private void addResource(InputStream is) throws IOException {
    try {
      addManifest(new Manifest(is));
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        System.err.println("Error closing manifest resource");
        e.printStackTrace();
      }
    }
  }

  private void readProfilingMode(Attributes attributes) {

    String debug = attributes.getValue("debug");
    if (debug != null) {
      debugLevel = Integer.parseInt(debug);
    }

    String locationMode = attributes.getValue("profile-location");
    if (locationMode != null) {
      enableProfileLocation = Boolean.parseBoolean(locationMode);
    }

    String queryLabelMode = attributes.getValue("query-labels");
    if (queryLabelMode != null) {
      enableQueryAutoLabel = Boolean.parseBoolean(queryLabelMode);
    }

    String mode = attributes.getValue("transaction-profiling");
    if (mode != null) {
      transactionProfilingMode = parseMode(mode);
    }
  }

  private TxProfileMode parseMode(String mode) {
    switch (mode.trim().toLowerCase()) {
      case "enabled":
      case "auto":
      case "enable":
        return TxProfileMode.ENABLED;
      case "manual":
        return TxProfileMode.MANUAL;
      default:
        return TxProfileMode.NONE;
    }
  }

  private void readProfilingStart(Attributes attributes) {
    String start = attributes.getValue("transaction-profiling-startvalue");
    if (start != null) {
      try {
        transactionProfilingStart = Integer.parseInt(start);
      } catch (NumberFormatException e) {
        // ignore
      }
    }
  }

  private void addManifest(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    readProfilingMode(attributes);
    readProfilingStart(attributes);
    readOptions(attributes);

    add(entityPackages, attributes.getValue("packages"));
    add(entityPackages, attributes.getValue("entity-packages"));
    add(transactionalPackages, attributes.getValue("transactional-packages"));
    add(querybeanPackages, attributes.getValue("querybean-packages"));

    final String topPackages = attributes.getValue("top-packages");
    if (topPackages != null) {
      add(transactionalPackages, topPackages);
      add(querybeanPackages, topPackages);
    }
  }

  private void readOptions(Attributes attributes) {
    transientInternalFields = bool("transient-internal-fields", transientInternalFields, attributes);
    checkNullManyFields = bool("check-null-many-fields", checkNullManyFields, attributes);
  }

  private boolean bool(String key, boolean defaultValue, Attributes attributes) {
    String val = attributes.getValue(key);
    return val != null ? Boolean.parseBoolean(val) : defaultValue;
  }

  /**
  * Collect each individual package splitting by delimiters.
  */
  private void add(Set<String> addTo, String packages) {
    if (packages != null) {
      String[] split = packages.split(",|;| ");
      for (String aSplit : split) {
        String pkg = aSplit.trim();
        if (!pkg.isEmpty()) {
          addTo.add(pkg);
        }
      }
    }
  }
}
