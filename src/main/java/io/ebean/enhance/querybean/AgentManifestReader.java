package io.ebean.enhance.querybean;

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
public class AgentManifestReader {

  private final Set<String> packageSet = new HashSet<>();

  public static Set<String> read(ClassLoader classLoader, Set<String> initialPackages) {

    try {
      return new AgentManifestReader(initialPackages)
          .readManifests(classLoader, "META-INF/ebean-typequery.mf")
          .readManifests(classLoader, "META-INF/ebean.mf")
          .getPackages();

    } catch (IOException e) {
      // log to standard error and return empty
      System.err.println("QueryBean Agent: error reading META-INF/ebean-typequery.mf manifest resources");
      e.printStackTrace();
      return new HashSet<>();
    }
  }

  /**
   * Construct with some packages defined externally.
   */
  public AgentManifestReader(Set<String> initialPackages) {
    if (initialPackages != null) {
      packageSet.addAll(initialPackages);
    }
  }

  /**
   * Construct with no initial packages (to use with addRaw()).
   */
  public AgentManifestReader() {
  }

  /**
   * Add raw packages: content (used for IDEA plugin etc).
   */
  public void addRaw(String content) {
    add(content.replace("packages:", "").trim());
  }

  /**
   * Return the parsed set of packages that type query beans are in.
   */
  public Set<String> getPackages() {
    return packageSet;
  }

  /**
   * Read all the specific manifest files and return the set of packages containing type query beans.
   */
  private AgentManifestReader readManifests(ClassLoader classLoader, String path) throws IOException {
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

  private void addManifest(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    add(attributes.getValue("packages"));
  }

  /**
   * Collect each individual package splitting by delimiters.
   */
  private void add(String packages) {
    if (packages != null) {
      String[] split = packages.split(",|;| ");
      for (int i = 0; i < split.length; i++) {
        String pkg = split[i].trim();
        if (!pkg.isEmpty()) {
          packageSet.add(pkg);
        }
      }
    }
  }
}
