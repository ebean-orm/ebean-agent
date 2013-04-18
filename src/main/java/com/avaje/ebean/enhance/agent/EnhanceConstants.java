/**
 * Copyright (C) 2006  Robin Bygrave
 * 
 * This file is part of Ebean.
 * 
 * Ebean is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *  
 * Ebean is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ebean; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA  
 */
package com.avaje.ebean.enhance.agent;


/**
 * Constant values used in byte code generation.
 */
public interface EnhanceConstants {

	public static final String AVAJE_TRANSACTIONAL_ANNOTATION = "Lcom/avaje/ebean/annotation/Transactional;";
	
	public static final String ENTITY_ANNOTATION = "Ljavax/persistence/Entity;";

	public static final String EMBEDDABLE_ANNOTATION = "Ljavax/persistence/Embeddable;";
	
	public static final String MAPPEDSUPERCLASS_ANNOTATION = "Ljavax/persistence/MappedSuperclass;";

	public static final String LDAPDOMAIN_ANNOTATION = "Lcom/avaje/ebean/annotation/LdapDomain;";

	public static final String IDENTITY_FIELD = "_ebean_identity";

    public static final String INTERCEPT_FIELD = "_ebean_intercept";
    
    public static final String C_ENHANCEDTRANSACTIONAL = "com/avaje/ebean/bean/EnhancedTransactional";
    
    public static final String C_ENTITYBEAN = "com/avaje/ebean/bean/EntityBean";
    
    public static final String C_SCALAOBJECT = "scala/ScalaObject";
    
    public static final String C_GROOVYOBJECT = "groovy/lang/GroovyObject";
    
    public static final String C_INTERCEPT = "com/avaje/ebean/bean/EntityBeanIntercept";
    
    public static final String C_BEANCOLLECTION = "com/avaje/ebean/bean/BeanCollection";
    
    public static final String L_INTERCEPT = "Lcom/avaje/ebean/bean/EntityBeanIntercept;";

    public static final String L_EMBEDDEDCOLUMNS = "Lcom/avaje/ebean/annotation/EmbeddedColumns;";

    public static final String L_SCOPETRANS = "Lcom/avaje/ebeaninternal/api/ScopeTrans;";
    
    public static final String L_HELPSCOPETRANS ="Lcom/avaje/ebeaninternal/api/HelpScopeTrans;";
    
    public static final String C_TXTYPE = "com/avaje/ebean/TxType";
    
    public static final String C_TXSCOPE = "com/avaje/ebean/TxScope";
    
    public static final String C_TXISOLATION = "com/avaje/ebean/TxIsolation";
    
    public static final String EBEAN_META_PREFIX = "com/avaje/ebean/meta/";

    public static final String EBEAN_PREFIX = "com/avaje/ebean";

    /**
     * The suffix added to the super class name.
     */
    public static final String SUFFIX = "$$EntityBean";
}
