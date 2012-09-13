package com.avaje.ebean.enhance.ant;

import com.avaje.ebean.enhance.agent.Transformer;

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
		String inDir = "D:/dev/workspace/ebeanExample/bin";
		String pkg = "app/data/test";

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

		Transformer t = new Transformer("", transformArgs);

		OfflineFileTransform ft = new OfflineFileTransform(t, cl, inDir, inDir);

		ft.process(pkg);

	}

	private static void printHelp() {
		System.out.println("Usage: [inputDirectory] [packages] [transformArguments]");
	}

	private static boolean isHelp(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("help")) {
				return true;
			}
			if (args[i].equalsIgnoreCase("-h")) {
				return true;
			}
		}
		return false;
	}
}
