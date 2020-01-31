package io.ebean.enhance.common;

import io.ebean.enhance.querybean.DetectQueryBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Reads all the META-INF/ebean.mf and META-INF/ebean-generated-info.mf resources with the locations
 * of all the entity beans (and hence locations of query beans).
 */
public class AgentManifest {

  private final Set<Integer> classLoaderIdentities = new HashSet<>();

  private final List<String> loadedResources = new ArrayList<>();

  private final Set<String> entityPackages = new HashSet<>();

  private final Set<String> transactionalPackages = new HashSet<>();

  private final Set<String> querybeanPackages = new HashSet<>();

  private final DetectQueryBean detectQueryBean;

  private int debugLevel = -1;

  private boolean transientInternalFields;

  private boolean checkNullManyFields = true;

  private boolean enableProfileLocation;

  public AgentManifest(ClassLoader classLoader) {
    this.detectQueryBean = new DetectQueryBean();
    readManifest(classLoader);
  }

  public AgentManifest() {
    this.detectQueryBean = new DetectQueryBean();
  }

  /**
   * Return true if more entity packages were loaded.
   */
  public boolean readManifest(ClassLoader classLoader) {
    if (classLoader == null) {
      return false;
    }
    final int loaderIdentity = System.identityHashCode(classLoader);
    if (classLoaderIdentities.add(loaderIdentity)) {
      try {
        int beforeSize = entityPackages.size();
        readManifests(classLoader, "META-INF/ebean-generated-info.mf");
        readManifests(classLoader, "META-INF/ebean.mf");
        readManifests(classLoader, "ebean.mf");
        int afterSize = entityPackages.size();
        if (afterSize > beforeSize) {
          detectQueryBean.addAll(entityPackages);
          return true;
        }
      } catch (IOException e) {
        // log to standard error and return empty
        System.err.println("Agent: error reading ebean manifest resources");
        e.printStackTrace();
      }
    }
    return false;
  }

  public boolean isDetectQueryBean(String owner) {
    return detectQueryBean.isQueryBean(owner);
  }

  @Override
  public String toString() {
    return "entityPackages:" + entityPackages + " querybeanPackages:" + querybeanPackages
      + " transactionalPackages:" + transactionalPackages;
  }

  /**
   * Return true if enhancement of profileLocations should be added.
   */
  public boolean isEnableProfileLocation() {
    return enableProfileLocation;
  }


  /**
   * Return the debug level read from ebean.mf
   */
  public int getDebugLevel() {
    return debugLevel;
  }

  /**
   * Return the paths that manifests were loaded from.
   */
  public List<String> getLoadedResources() {
    return loadedResources;
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
        loadedResources.add(path);
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
  }

  private void addManifest(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    readProfilingMode(attributes);
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
      String[] split = packages.split("[,; ]");
      for (String aSplit : split) {
        String pkg = aSplit.trim();
        if (!pkg.isEmpty()) {
          addTo.add(pkg);
        }
      }
    }
  }
}
