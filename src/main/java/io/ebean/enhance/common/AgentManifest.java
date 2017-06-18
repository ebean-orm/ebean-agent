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

  private final Set<String> entityPackages = new HashSet<>();

  private final Set<String> transactionalPackages = new HashSet<>();

  private final Set<String> querybeanPackages = new HashSet<>();

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
  public AgentManifest() {
  }

  public String toString() {
    return "entityPackages:" + entityPackages + " querybeanPackages:" + querybeanPackages + " transactionalPackages:" + transactionalPackages;
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
        addResource(url.openStream());
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
  public void addResource(InputStream is) throws IOException {
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

  private void addManifest(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    add(entityPackages, attributes.getValue("packages"));
    add(entityPackages, attributes.getValue("entity-packages"));
    add(transactionalPackages, attributes.getValue("transactional-packages"));
    add(querybeanPackages, attributes.getValue("querybean-packages"));
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
