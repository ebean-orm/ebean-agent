package io.ebean.enhance.common;

import io.ebean.enhance.asm.AnnotationVisitor;
import io.ebean.enhance.asm.ClassVisitor;
import io.ebean.enhance.asm.FieldVisitor;
import io.ebean.enhance.asm.MethodVisitor;
import io.ebean.enhance.asm.Opcodes;
import io.ebean.enhance.entity.FieldMeta;
import io.ebean.enhance.entity.LocalFieldVisitor;
import io.ebean.enhance.entity.MessageOutput;
import io.ebean.enhance.entity.MethodMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Holds the meta data for an entity bean class that is being enhanced.
 */
public class ClassMeta {

	private static final Logger logger = Logger.getLogger(ClassMeta.class.getName());

	private static final String OBJECT_CLASS = Object.class.getName().replace('.', '/');

	public static final Pattern HAS_SINGLE_ARGUMENT = Pattern.compile("\\(\\[?([ZBCSIJFD]|L.*;)\\).*");

	private final MessageOutput logout;

	private final int logLevel;

	private String className;

	private String superClassName;

	private ClassMeta superMeta;

	/**
	 * Set to true if the class implements th GroovyObject interface.
	 */
	private boolean hasGroovyInterface;

	/**
	 * Set to true if the class implements the ScalaObject interface.
	 */
	private boolean hasScalaInterface;

	/**
	 * Set to true if the class already implements the EntityBean interface.
	 */
	private boolean hasEntityBeanInterface;
	
	private boolean alreadyEnhanced;

	private boolean hasEqualsOrHashcode;

	private boolean hasDefaultConstructor;
	
	private boolean hasStaticInit;

	private HashSet<String> existingMethods = new HashSet<String>();
	
	private Map<String, String> beanSetters = new HashMap<String, String>(); 

	private LinkedHashMap<String, FieldMeta> fields = new LinkedHashMap<String, FieldMeta>();

	private HashSet<String> classAnnotation = new HashSet<String>();

	private AnnotationInfo annotationInfo = new AnnotationInfo(null);

	private ArrayList<MethodMeta> methodMetaList = new ArrayList<MethodMeta>();

	private final EnhanceContext enhanceContext;
	
  private List<FieldMeta> allFields;
  
	public ClassMeta(EnhanceContext enhanceContext, int logLevel, MessageOutput logout) {
		this.enhanceContext = enhanceContext;
		this.logLevel = logLevel;
		this.logout = logout;
	}
	
	/**
	 * Return the enhance context which has options for enhancement.
	 */
	public EnhanceContext getEnhanceContext() {
        return enhanceContext;
    }
	
	/**
	 * Return the AnnotationInfo collected on methods. 
	 * Used to determine Transactional method enhancement.
	 */
	public AnnotationInfo getAnnotationInfo() {
		return annotationInfo;
	}

	/**
	 * Return the transactional annotation information for a matching interface method.
	 */
	public AnnotationInfo getInterfaceTransactionalInfo(String methodName, String methodDesc) {

		AnnotationInfo annotationInfo = null;

		for (int i = 0; i < methodMetaList.size(); i++) {
			MethodMeta meta = methodMetaList.get(i);
			if (meta.isMatch(methodName, methodDesc)) {
				if (annotationInfo != null) {
					String msg = "Error in [" + className + "] searching the transactional methods[" + methodMetaList
							+ "] found more than one match for the transactional method:" + methodName + " "
							+ methodDesc;
					
					logger.log(Level.SEVERE, msg);
					log(msg);
					
				} else {
					annotationInfo = meta.getAnnotationInfo();
					if (isLog(9)){
						log("... found transactional info from interface "+className+" "+methodName+" "+methodDesc);
					}
				}
			}
		}

		return annotationInfo;
	}

	public boolean isCheckSuperClassForEntity() {
    return !superClassName.equals(OBJECT_CLASS) && isCheckEntity();
  }

	public String toString() {
		return className;
	}

	public boolean isTransactional() {
    return classAnnotation.contains(EnhanceConstants.AVAJE_TRANSACTIONAL_ANNOTATION);
  }

	public void setClassName(String className, String superClassName) {
		this.className = className;
		this.superClassName = superClassName;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public boolean isLog(int level) {
		return level <= logLevel;
	}

	public void log(String msg) {
		if (className != null) {
			msg = "cls: " + className + "  msg: " + msg;
		}
		logout.println("ebean-enhance> " + msg);
	}
	
	public void logEnhanced() {
		String m = "enhanced ";
		if (hasScalaInterface()){
			m += " (scala)";
		}
		if (hasGroovyInterface()){
			m += " (groovy)";
		}
		log(m);
	}

	public void setSuperMeta(ClassMeta superMeta) {
		this.superMeta = superMeta;
	}

	/**
	 * Set to true if the class has an existing equals() or hashcode() method.
	 */
	public void setHasEqualsOrHashcode(boolean hasEqualsOrHashcode) {
		this.hasEqualsOrHashcode = hasEqualsOrHashcode;
	}

	/**
	 * Return true if Equals/hashCode is implemented on this class or a super class.
	 */
	public boolean hasEqualsOrHashCode() {
	  if (hasEqualsOrHashcode) {
	    return true;
	    
	  } else {
      return (superMeta != null && superMeta.hasEqualsOrHashCode());
    }
	}

	/**
	 * Return true if the field is a persistent field.
	 */
	public boolean isFieldPersistent(String fieldName) {

		FieldMeta f = getFieldPersistent(fieldName);
		return (f == null) ? false: f.isPersistent();
	}

  /**
   * Return true if the field is a persistent many field.
   */
  public boolean isFieldPersistentMany(String fieldName) {
    FieldMeta f = getFieldPersistent(fieldName);
    return (f != null && f.isPersistent() && f.isMany());
  }

  /**
   * Return the field - null when not found.
   */
  public FieldMeta getFieldPersistent(String fieldName) {

    FieldMeta f = fields.get(fieldName);
    if (f != null) {
      return f;
    }
    return (superMeta == null) ? null : superMeta.getFieldPersistent(fieldName);
  }

	/**
	 * Return the list of fields local to this type (not inherited).
	 */
	public List<FieldMeta> getLocalFields() {

		ArrayList<FieldMeta> list = new ArrayList<FieldMeta>();

    for (FieldMeta fm : fields.values()) {
      if (!fm.isObjectArray()) {
        // add field local to this entity type
        list.add(fm);
      }
    }

		return list;
	}

	/**
	 * Return the list of fields inherited from super types that are entities.
	 */
	private List<FieldMeta> getInheritedFields(List<FieldMeta> list) {

		if (list == null){
			list = new ArrayList<FieldMeta>();
		}

		if (superMeta != null) {
			superMeta.addFieldsForInheritance(list);
		}
		return list;
	}

	/**
	 * Add all fields to the list.
	 */
	private void addFieldsForInheritance(List<FieldMeta> list) {
		if (isEntity()) {
			list.addAll(0, fields.values());
			if (superMeta != null) {
				superMeta.addFieldsForInheritance(list);
			}
		}
	}
	
	/**
	 * Return true if the class contains persistent fields.
	 */
  public boolean hasPersistentFields() {
    
    for (FieldMeta fieldMeta : fields.values()) {
      if (fieldMeta.isPersistent() || fieldMeta.isTransient()) {
        return true;
      }
    }

    return superMeta != null && superMeta.hasPersistentFields();
  }
  
	/**
	 * Return the list of all fields including ones inherited from entity super
	 * types and mappedSuperclasses.
	 */
	public List<FieldMeta> getAllFields() {

	  if (allFields != null) {
	    return allFields;
	  }
		List<FieldMeta> list = getLocalFields();
		getInheritedFields(list);
		
		this.allFields = list;
		for (int i=0; i<allFields.size(); i++) {
		  allFields.get(i).setIndexPosition(i);
		}
		
		return list;
	}

	/**
	 * Add field level get set methods for each field.
	 */
	public void addFieldGetSetMethods(ClassVisitor cv) {

		if (isEntityEnhancementRequired()) {
      for (FieldMeta fm : fields.values()) {
        fm.addGetSetMethods(cv, this);
      }
		}
	}

	/**
	 * Return true if the class has an Entity, Embeddable, MappedSuperclass (with persistent fields).
	 */
	public boolean isEntity() {
		if (!EntityCheck.hasEntityAnnotation(classAnnotation)) {
			return false;
		}
		if (classAnnotation.contains(EnhanceConstants.MAPPEDSUPERCLASS_ANNOTATION)) {
		  // only 'interesting' if it has persistent fields or equals/hashCode.
		  // Some MappedSuperclass like com.avaje.ebean.Model don't need any enhancement
		  boolean shouldEnhance = hasEqualsOrHashCode() || hasPersistentFields();
		  if (isLog(8)) {
		    log("mappedSuperClass with equals/hashCode or persistentFields: "+shouldEnhance);
		  }
		  return shouldEnhance;
		}
		return true;
	}

  /**
   * Return true if the class has an Entity, Embeddable, or MappedSuperclass.
   */
  private boolean isCheckEntity() {
		return EntityCheck.hasEntityAnnotation(classAnnotation);
  }

	/**
	 * Return true for classes not already enhanced and yet annotated with entity, embeddable or mappedSuperclass.
	 */
	public boolean isEntityEnhancementRequired() {
    return !alreadyEnhanced && isEntity();
  }

  /**
   * Return true if the bean is already enhanced.
   */
  public boolean isAlreadyEnhanced() {
    return alreadyEnhanced;
  }

  /**
	 * Return the className of this entity class.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Return true if this entity bean has a super class that is an entity.
	 */
	public boolean isSuperClassEntity() {
    return superMeta != null && superMeta.isEntity();
	}

	/**
	 * Add a class annotation.
	 */
	public void addClassAnnotation(String desc) {
		classAnnotation.add(desc);
	}

	/**
	 * Add an existing method.
	 */
	public void addExistingMethod(String methodName, String methodDesc) {
		existingMethods.add(methodName + methodDesc);
		if (isSetter(methodName, methodDesc)) {
			String fieldDesc = methodDesc.substring(0,methodDesc.indexOf(')')+1);
			beanSetters.put(methodName + fieldDesc, methodDesc);
		}
	}
	
	private boolean isSetter(String methodName, String methodDesc) {
		return methodName.length() >= 4
				&& methodName.startsWith("set")
				&& Character.isUpperCase(methodName.charAt(3))
				&& HAS_SINGLE_ARGUMENT.matcher(methodDesc).matches();
	}

	public String getSetterDesc(String methodName, String fieldDesc) {
		return getSetterDesc(methodName + "(" + fieldDesc + ")");
	}
	public String getSetterDesc(String methodNameAndParamDesc) {
		String ret = beanSetters.get(methodNameAndParamDesc);
		if (ret == null && superMeta != null) {
			return superMeta.getSetterDesc(methodNameAndParamDesc);
		}
		return ret;
	}
	/**
	 * Return true if the method already exists on the bean.
	 */
	public boolean isExistingMethod(String methodName, String methodDesc) {
		return isExistingMethod(methodName + methodDesc);
	}
	
	/**
	 * Return true if the method already exists on the bean.
	 */
	public boolean isExistingMethod(String methodNameAndDesc) {
		return existingMethods.contains(methodNameAndDesc) 
				|| (superMeta != null && superMeta.isExistingMethod(methodNameAndDesc));
	}

	public MethodVisitor createMethodVisitor(MethodVisitor mv, int access, String name, String desc) {

		MethodMeta methodMeta = new MethodMeta(annotationInfo, access, name, desc);
		methodMetaList.add(methodMeta);

		return new MethodReader(mv, methodMeta);
	}

	private static final class MethodReader extends MethodVisitor {

    final MethodMeta methodMeta;

		MethodReader(MethodVisitor mv, MethodMeta methodMeta) {
      super(Opcodes.ASM5, mv);
			this.methodMeta = methodMeta;
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

      if (mv == null) {
        return null;
      }

      AnnotationVisitor av = mv.visitAnnotation(desc, visible);
      return new AnnotationInfoVisitor(null, methodMeta.getAnnotationInfo(), av);
		}

	}

	/**
	 * Create and return a read only fieldVisitor for subclassing option.
	 */
	public FieldVisitor createLocalFieldVisitor(String name, String desc) {
		return createLocalFieldVisitor(null, name, desc);
	}

	/**
	 * Create and return a new fieldVisitor for use when enhancing a class.
	 */
	public FieldVisitor createLocalFieldVisitor(FieldVisitor fv, String name, String desc) {

		FieldMeta fieldMeta = new FieldMeta(this, name, desc, className);
		LocalFieldVisitor localField = new LocalFieldVisitor(fv, fieldMeta);
		if (name.startsWith("_ebean")) {
			// can occur when reading inheritance information on
			// a entity that has already been enhanced
			if (isLog(5)) {
				log("... ignore field " + name);
			}
		} else {
			fields.put(localField.getName(), fieldMeta);
		}
		return localField;
	}

	public void setAlreadyEnhanced(boolean alreadyEnhanced) {
		this.alreadyEnhanced = alreadyEnhanced;
	}

	public boolean hasDefaultConstructor() {
		return hasDefaultConstructor;
	}

	public void setHasDefaultConstructor(boolean hasDefaultConstructor) {
		this.hasDefaultConstructor = hasDefaultConstructor;
	}

  public void setHasStaticInit(boolean hasStaticInit) {
    this.hasStaticInit = hasStaticInit;
  }

	public boolean hasStaticInit() {
	  return hasStaticInit;
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		appendDescription(sb);
		return sb.toString();
	}

	private void appendDescription(StringBuilder sb) {
		sb.append(className);
		if (superMeta != null) {
			sb.append(" : ");
			superMeta.appendDescription(sb);
		}
	}

	public boolean hasScalaInterface() {
		return hasScalaInterface;
	}

	public void setScalaInterface(boolean hasScalaInterface) {
		this.hasScalaInterface = hasScalaInterface;
	}

	public boolean hasEntityBeanInterface() {
		return hasEntityBeanInterface;
	}

	public void setEntityBeanInterface(boolean hasEntityBeanInterface) {
		this.hasEntityBeanInterface = hasEntityBeanInterface;
	}

	public boolean hasGroovyInterface() {
		return hasGroovyInterface;
	}

	public void setGroovyInterface(boolean hasGroovyInterface) {
		this.hasGroovyInterface = hasGroovyInterface;
	}
	
}
