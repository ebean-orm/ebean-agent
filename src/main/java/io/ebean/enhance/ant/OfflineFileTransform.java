package io.ebean.enhance.ant;

import io.ebean.enhance.common.InputStreamTransform;
import io.ebean.enhance.Transformer;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Transforms class files when they are on the file system.
 * <p>
 * Typically run as part of an ANT task rather than when Ebean is running.
 * </p>
 */
public class OfflineFileTransform {

  protected final InputStreamTransform inputStreamTransform;

  protected final String inDir;

  protected  TransformationListener listener;

  private int logLevel;

  /**
  * Enhance the class file and replace the file with the the enhanced
  * version of the class.
  *
  * @param transformer
  *            object that actually transforms the class bytes
  * @param classLoader
  *            the ClassLoader used as part of the transformation
  * @param inDir
  *            the root directory where the class files are located
  */
  public OfflineFileTransform(Transformer transformer, ClassLoader classLoader, String inDir) {
    this.inputStreamTransform = new InputStreamTransform(transformer, classLoader);
    logLevel = transformer.getLogLevel();
    inDir = trimSlash(inDir);
    this.inDir = inDir;
  }

  /** Register a listener to receive event notification */
  public void setListener(TransformationListener v) {
    this.listener = v;
  }

  private String trimSlash(String dir) {
    if (dir.endsWith("/")){
      return dir.substring(0, dir.length()-1);
    } else {
      return dir;
    }
  }

  /**
  * Process the packageNames as comma delimited string.
  */
  public void process(String packageNames) {

    if (packageNames == null) {
      // just process all directories
      processPackage("");
      return;
    }

    Set<String> pkgNames = new LinkedHashSet<String>();
    Collections.addAll(pkgNames, packageNames.split(","));

    process(pkgNames);
  }

  /**
  * Process all the comma delimited list of packages.
  * <p>
  * Package names are effectively converted into a directory on the file
  * system, and the class files are found and processed.
  * </p>
  */
  public void process(Set<String> packageNames) {

    if (packageNames == null || packageNames.isEmpty()) {
      // just process all directories
      inputStreamTransform.log(2, "processing all directories (as no explicit packages)");
      processPackage("");
      return;
    }

    for (String pkgName : packageNames) {

      String pkg = pkgName.trim().replace('.', '/');

      if (pkg.endsWith("**")) {
        pkg = pkg.substring(0, pkg.length() - 2);
      } else if (pkg.endsWith("*")) {
        pkg = pkg.substring(0, pkg.length() - 1);
      }

      pkg = trimSlash(pkg);

      processPackage(pkg);
    }
  }

  private void processPackage(String dir) {

    inputStreamTransform.log(3, "transform> pkg: " + dir);

    String dirPath = inDir + "/" + dir;
    File d = new File(dirPath);
    if (!d.exists()) {
      throw new RuntimeException("File not found " + dirPath + "  currentDir:" + new File(".").getAbsolutePath());
    }

    final File[] files = d.listFiles();
    if (files != null) {
      for (final File file : files) {
        try {
          if (file.isDirectory()) {
            final String subDir = dir + "/" + file.getName();
            processPackage(subDir);
          } else {
            final String fileName = file.getName();
            if (fileName.endsWith(".java")) {
              // possibly a common mistake... mixing .java and .class
              System.err.println("Expecting a .class file but got " + fileName + " ... ignoring");

            } else if (fileName.endsWith(".class")) {
              transformFile(file);
            }
          }
        } catch (final Exception e) {
          throw new RuntimeException("Error transforming file " + file.getName(), e);
        }
      }
    } else {
      throw new RuntimeException("Can't read directory " + d.getName());
    }
  }

  private void transformFile(File file) throws IOException, IllegalClassFormatException {

    String className = getClassName(file);

    byte[] result = inputStreamTransform.transform(className, file);

    if (result != null) {
      InputStreamTransform.writeBytes(result, file);
      if(listener!=null && logLevel > 0) {
        listener.logEvent("Enhanced "+file);
      }
    }
  }

  private String getClassName(File file) {
    String path = file.getPath();
    path = path.substring(inDir.length() + 1);
    path = path.substring(0, path.length() - ".class".length());
    // for windows... replace the
    return StringReplace.replace(path,"\\", "/");
  }
}
