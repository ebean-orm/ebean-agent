package test.normalize;

/**
 * Intercept method for string property to trim it.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class Trimmer {
  public static String normalize(String in) {
    return in == null ? in : in.trim();
  }
}
