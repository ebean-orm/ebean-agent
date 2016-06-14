package com.avaje.ebean.enhance.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Reads all the META-INF/ebean.mf resources with the package locations of all entity beans.
 */
public class AgentManifestReader {

  private final Set<String> packageSet = new HashSet<String>();

  /**
   * Construct with some packages defined externally.
   */
  public AgentManifestReader(Set<String> initialPackages) {
    if (initialPackages != null) {
      packageSet.addAll(initialPackages);
    }
  }

  /**
   * Construct with no initial packages.
   */
  public AgentManifestReader() {
  }

  /**
   * Read the packages from the manifest file.
   */
  public void read(File file) {
    if (file.exists()) {
      try {
        read(new FileInputStream(file));
      } catch (IOException e) {
        throw new RuntimeException("Error reading META-INF/ebean.mf manifest file", e);
      }
    }
  }

  /**
   * Read the packages from the manifest InputStream.
   */
  public void read(InputStream is) throws IOException {
    try {
      read(new Manifest(is));
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        System.err.println("Error closing Manifest inputStream");
        e.printStackTrace();
      }
    }
  }

  /**
   * Read the packages from the manifest.
   */
  public void read(Manifest manifest) throws IOException {
    Attributes attributes = manifest.getMainAttributes();
    String packages = attributes.getValue("packages");
    if (packages != null) {
      addRaw(packages);
    }
  }

  /**
   * Add raw packages: content (used for IDEA plugin etc).
   */
  public void addRaw(String content) {
    if (content != null) {
      add(content.replace("packages:", "").trim());
    }
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
  public void readManifests(ClassLoader classLoader)  {

    try {
      if (classLoader != null) {
        Enumeration<URL> resources = classLoader.getResources("META-INF/ebean.mf");
        while (resources.hasMoreElements()) {
          read(resources.nextElement().openStream());
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading META-INF/ebean.mf manifest resources");
      e.printStackTrace();
    }
  }

  /**
   * Collect each individual package splitting by delimiters.
   */
  private void add(String packages) {
    String[] split = packages.split(",|;| ");
    for (int i = 0; i <split.length; i++) {
      String pkg = split[i].trim();
      if (!pkg.isEmpty()) {
        packageSet.add(pkg);
      }
    }
  }
}
