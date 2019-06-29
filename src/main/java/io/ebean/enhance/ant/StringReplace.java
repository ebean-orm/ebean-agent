package io.ebean.enhance.ant;


/**
 * Utility String class that supports String manipulation functions.
 */
public class StringReplace {


  /**
   * This method takes a String and will replace all occurrences of the match
   * String with that of the replace String.
   *
   * @param source  the source string
   * @param match   the string used to find a match
   * @param replace the string used to replace match with
   * @return the source string after the search and replace
   */
  public static String replace(String source, String match, String replace) {
    return replaceString(source, match, replace, 30, 0, source.length());
  }

  /**
   * Additionally specify the additionalSize to add to the buffer. This will
   * make the buffer bigger so that it doesn't have to grow when replacement
   * occurs.
   */
  private static String replaceString(String source, String match, String replace,
                                      int additionalSize, int startPos, int endPos) {

    char match0 = match.charAt(0);

    int matchLength = match.length();

    if (matchLength == 1 && replace.length() == 1) {
      char replace0 = replace.charAt(0);
      return source.replace(match0, replace0);
    }
    if (matchLength >= replace.length()) {
      additionalSize = 0;
    }


    int sourceLength = source.length();
    int lastMatch = endPos - matchLength;

    StringBuilder sb = new StringBuilder(sourceLength + additionalSize);

    if (startPos > 0) {
      sb.append(source, 0, startPos);
    }

    char sourceChar;
    boolean isMatch;
    int sourceMatchPos;

    for (int i = startPos; i < sourceLength; i++) {
      sourceChar = source.charAt(i);
      if (i > lastMatch || sourceChar != match0) {
        sb.append(sourceChar);

      } else {
        // check to see if this is a match
        isMatch = true;
        sourceMatchPos = i;

        // check each following character...
        for (int j = 1; j < matchLength; j++) {
          sourceMatchPos++;
          if (source.charAt(sourceMatchPos) != match.charAt(j)) {
            isMatch = false;
            break;
          }
        }
        if (isMatch) {
          i = i + matchLength - 1;
          sb.append(replace);
        } else {
          // was not a match
          sb.append(sourceChar);
        }
      }
    }

    return sb.toString();
  }


}
