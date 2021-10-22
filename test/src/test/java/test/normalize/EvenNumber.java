package test.normalize;

/**
 * Intercept method for int property. Demonstrates "evenNumber"
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class EvenNumber {
  public static int normalize(int in) {
    return in / 2 * 2;
  }
}
