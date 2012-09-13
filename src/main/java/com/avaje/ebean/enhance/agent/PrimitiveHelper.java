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

import com.avaje.ebean.enhance.asm.Type;

/**
 * Maps primitive types to their Object types.
 */
public class PrimitiveHelper {

	private static Type INTEGER_OBJECT = Type.getType(Integer.class);
	private static Type SHORT_OBJECT = Type.getType(Short.class);
	private static Type CHARACTER_OBJECT = Type.getType(Character.class);
	private static Type LONG_OBJECT = Type.getType(Long.class);
	private static Type DOUBLE_OBJECT = Type.getType(Double.class);
	private static Type FLOAT_OBJECT = Type.getType(Float.class);
	private static Type BYTE_OBJECT = Type.getType(Byte.class);
	private static Type BOOLEAN_OBJECT = Type.getType(Boolean.class);
	
	public static Type getObjectWrapper(Type primativeAsmType){
		
		int sort = primativeAsmType.getSort();
		switch (sort) {
		case Type.INT: return INTEGER_OBJECT;
		case Type.SHORT: return SHORT_OBJECT;
		case Type.CHAR: return CHARACTER_OBJECT;
		case Type.LONG: return LONG_OBJECT;
		case Type.DOUBLE: return DOUBLE_OBJECT;
		case Type.FLOAT: return FLOAT_OBJECT;
		case Type.BYTE: return BYTE_OBJECT;
		case Type.BOOLEAN: return BOOLEAN_OBJECT;
			
		default:
			throw new RuntimeException("Expected primative? "+primativeAsmType);
		}
	}

}
