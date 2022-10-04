package io.ebean.enhance.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Summary of the enhancement.
 */
public class SummaryInfo {

  private final List<String> loadedResources;
  private final Set<String> entities = new HashSet<>();
  private final Set<String> queryBeans = new HashSet<>();
  private final Set<String> queryBeanCallers = new HashSet<>();
  private final Set<String> fieldAccessUsers = new HashSet<>();
  private final Set<String> transactional = new HashSet<>();

  SummaryInfo(List<String> loadedResources) {
    this.loadedResources = loadedResources;
  }

  /**
   * Remove from queryBeanCallers the queryBeans (due to profile location enhancement).
   */
  SummaryInfo prepare() {
    for (String queryBean : queryBeans) {
      queryBeanCallers.remove(queryBean);
      queryBeanCallers.remove(queryBean + "$Companion");
    }
    return this;
  }

  /**
   * Return true if there was nothing enhanced.
   */
  public boolean isEmpty() {
    return entities.isEmpty() && queryBeans.isEmpty() && queryBeanCallers.isEmpty() && transactional.isEmpty();
  }

  public boolean hasEntities() {
    return !entities.isEmpty();
  }

  public boolean hasQueryBeans() {
    return !queryBeans.isEmpty();
  }

  public boolean hasTransactional() {
    return !transactional.isEmpty();
  }

  public boolean hasQueryCallers() {
    return !queryBeanCallers.isEmpty();
  }

  public boolean hasFieldAccess() {
    return !fieldAccessUsers.isEmpty();
  }

  public String toString() {
    return " entities:" + entities + " queryBeans:" + queryBeans + " tqb:" + transactional;
  }

  void addTransactional(String className) {
    transactional.add(className);
  }

  void addEntity(String className) {
    entities.add(className);
  }

  void addQueryBean(String className) {
    queryBeans.add(className);
  }

  void addQueryBeanCaller(String className) {
    queryBeanCallers.add(className);
  }

  void addFieldAccessUser(String className) {
    fieldAccessUsers.add(className);
  }

  private String summary(String prefix, Set<String> beans) {
    return new SumOut(beans).summary(prefix);
  }

  /**
   * Return the manifest resource paths that were loaded.
   */
  public List<String> loadedResources() {
    return loadedResources;
  }

  /**
   * Return a summary of the entities enhanced.
   */
  public String entities() {
    return summary("     Entities", entities);
  }

  /**
   * Return a summary of the query beans enhanced.
   */
  public String queryBeans() {
    return summary("   QueryBeans", queryBeans);
  }

  /**
   * Return a summary of the beans enhanced that call query beans.
   */
  public String queryCallers() {
    return summary("Query Callers", queryBeanCallers);
  }

  /**
   * Return a summary of the beans enhanced that call query beans.
   */
  public String fieldAccess() {
    return summary(" Field Access", fieldAccessUsers);
  }

  /**
   * Return a summary of the beans with transactional enhanced.
   */
  public String transactional() {
    return summary("Transactional", transactional);
  }

  /**
   * Helper that trims off the common package prefix to shorten the output.
   */
  static class SumOut {

    private final Set<String> beans;
    private final Set<String> trimmedBeans;
    private String[] commonAsArray;
    private String commonPrefix;

    SumOut(Set<String> beans) {
      this.beans = beans;
      initCommonPrefix();
      trimmedBeans = trimmedBeans();
    }

    String sizeOut(int size) {
      return " (" + size + ")" + ((size < 9) ? " " : "");
    }

    String summary(String prefix) {
      return prefix + sizeOut(beans.size()) + " pkgs[" + commonPrefix + "] beans" + trimmedBeans.toString();
    }

    String commonPackage() {
      return commonPrefix;
    }

    String beans() {
      return trimmedBeans.toString();
    }

    private void initCommonPrefix() {
      if (beans.isEmpty()) {
        commonPrefix = "";
      } else {
        for (String bean : beans) {
          final String beanPrefix = prefix(bean);
          if (commonPrefix == null) {
            commonPrefix = beanPrefix;
            commonAsArray = beanPrefix.split("/");
          } else {
            common(beanPrefix);
          }
        }
      }
    }

    private void common(String beanPrefix) {
      if (beanPrefix.equals(commonPrefix)) {
        return;
      }

      final String[] elements = beanPrefix.split("/");
      int min = Math.min(commonAsArray.length, elements.length);
      for (int i = 0; i < min; i++) {
        if (!elements[i].equals(commonAsArray[i])) {
          commonFor(i);
          return;
        }
      }
      if (commonAsArray.length != min) {
        commonFor(min);
      }
    }

    private void setCommonPrefix(String beanPrefix) {
      commonPrefix = beanPrefix;
      commonAsArray = beanPrefix.split("/");
    }

    private void commonFor(int pos) {
      StringJoiner joiner = new StringJoiner("/");
      for (int i = 0; i < pos; i++) {
        joiner.add(commonAsArray[i]);
      }
      setCommonPrefix(joiner.toString());
    }

    String prefix(String bean) {
      final int pos = bean.lastIndexOf('/');
      return (pos == -1) ? bean : bean.substring(0, pos);
    }

    private Set<String> trimmedBeans() {
      if (beans.isEmpty()) {
        return beans;
      }
      Set<String> temp = new TreeSet<>();
      int trimLen = commonPrefix.length() + 1;
      if (trimLen == 1) {
        trimLen = 0;
      }
      for (String bean : beans) {
        if (bean.length() > trimLen) {
          bean = bean.substring(trimLen);
        }
        temp.add(bean);
      }
      return temp;
    }

  }
}
