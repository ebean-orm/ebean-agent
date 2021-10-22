package test.normalize;

/**
 * Intercept method for string property to remove leading zeros.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class LeadingZeroRemover {
  public static String normalize(String in) {
    if (in == null) {
      return null;
    }
    int i = 0;
    while(i < in.length() && in.charAt(i) == '0') {
      i++;
    }
    return in.substring(i);
  }
}
