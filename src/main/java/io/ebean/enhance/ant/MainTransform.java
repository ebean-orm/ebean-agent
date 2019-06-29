package io.ebean.enhance.ant;

import io.ebean.enhance.Transformer;

/**
 * A utility object to run transformation from a main method.
 */
public class MainTransform {

  public static void main(String[] args) {

    if (isHelp(args)) {
      printHelp();
      return;
    }

    String transformArgs = "debug=1";
    String inDir = "./target/test-classes";
    String pkg = "test";

    if (args.length > 0) {
      inDir = args[0];
    }
    if (args.length > 1) {
      pkg = args[1];
    }

    if (args.length > 2) {
      transformArgs = args[2];
    }

    ClassLoader cl = ClassLoader.getSystemClassLoader();

    Transformer t = new Transformer(cl, transformArgs);

    OfflineFileTransform ft = new OfflineFileTransform(t, cl, inDir);

    ft.process(pkg);

  }

  private static void printHelp() {
    System.out.println("Usage: [inputDirectory] [packages] [transformArguments]");
  }

  private static boolean isHelp(String[] args) {
    for (String arg : args) {
      if (arg.equalsIgnoreCase("help")) {
        return true;
      }
      if (arg.equalsIgnoreCase("-h")) {
        return true;
      }
    }
    return false;
  }
}
