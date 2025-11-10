package io.ebean.enhance.ant;

import io.ebean.enhance.Transformer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * An ANT task that can enhance entity beans etc for use by Ebean.
 * <p>
 * You can use this ANT task as part of your build process to enhance entity
 * beans etc.
 * </p>
 * <p>
 * The parameters are:
 * <ul>
 * <li> <b>classSource</b> This is the root directory where the .class files
 * are found. </li>
 * <li> <b>packages</b> A comma delimited list of packages that is searched for
 * classes that need to be enhanced. If the package ends with ** or * then all
 * subpackages are also searched. </li>
 * <li> <b>transformArgs</b> Arguments passed to the transformer. Typically a
 * debug level in the form of debug=1 etc. </li>
 * </ul>
 * </p>
 *
 * <pre class="code">{@code
 *
 * 	 <taskdef name="ebeanEnhance" classname="AntEnhanceTask" classpath="bin" />
 *   <target name="enhance" depends="compile">
 *       <ebeanEnhance
 *            classSource="${bin.dir}"
 *            packages="com.avaje.ebean.meta.**, com.acme.myapp.entity.**"
 *            transformArgs="debug=1" />
 *   </target>
 * }</pre>
 */
public class AntEnhanceTask extends Task {

  private String classpath;
  private String classSource;
  private String transformArgs;
  private String packages;

  @Override
  public void execute() throws BuildException {
    String agentArgs = combine(packages, transformArgs);
    Transformer t = new Transformer(null, agentArgs);

    ClassLoader cl = AntEnhanceTask.class.getClassLoader();
    OfflineFileTransform ft = new OfflineFileTransform(t, cl, classSource);
    ft.process(packages);
  }

  /**
   * Combine the packages into the transformArgs to filter enhanced classes via PackageFilter.
   */
  static String combine(String packages, String transformArgs) {
    StringBuilder args = new StringBuilder();
    args.append("packages=").append(packages.replace("**", ""));
    if (transformArgs != null && !transformArgs.isEmpty()) {
      args.append(',').append(transformArgs);
    }
    return args.toString();
  }

  /**
   * the classpath used to search for e.g. inerited classes
   */
  public String getClasspath() {
    return classpath;
  }

  /**
   * the classpath used to search for e.g. inerited classes
   */
  public void setClasspath(String classpath) {
    this.classpath = classpath;
  }

  /**
   * Set the directory holding the class files we want to transform.
   */
  public void setClassSource(String source) {
    this.classSource = source;
  }

  /**
   * Set the arguments passed to the transformer.
   */
  public void setTransformArgs(String transformArgs) {
    this.transformArgs = transformArgs;
  }

  /**
   * Set the package name to search for classes to transform.
   * <p>
   * If the package name ends in "/**" then this recursively transforms all
   * sub packages as well.
   * </p>
   */
  public void setPackages(String packages) {
    this.packages = packages;
  }

}
