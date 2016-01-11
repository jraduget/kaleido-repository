/*  
 * Copyright 2008-2016 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.util;

import static org.kaleidofoundry.core.util.ReflectionHelper.getAllDeclaredFields;
import static org.kaleidofoundry.core.util.ReflectionHelper.getAllDeclaredMethodsByName;
import static org.kaleidofoundry.core.util.ReflectionHelper.getAllDeclaredMethodsReturns;
import static org.kaleidofoundry.core.util.ReflectionHelper.getAllInterfaces;
import static org.kaleidofoundry.core.util.ReflectionHelper.getMethodWithNoArgs;
import static org.kaleidofoundry.core.util.ReflectionHelper.getShortClassName;
import static org.kaleidofoundry.core.util.ReflectionHelper.getSuperClasses;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.util.LocalFirstAncestor.LocalInnerClass;

/**
 * test each method (static) in {@link ReflectionHelper}
 * 
 * @author jraduget
 */
public class ReflectionHelperTest  {

   @Test
   public void testGetShortClassName() {
	assertEquals("ReflectionHelperTest", getShortClassName(ReflectionHelperTest.class));
	assertEquals("LocalFirstAncestor", getShortClassName(LocalFirstAncestor.class));
	assertEquals("LocalInnerClass", getShortClassName(LocalInnerClass.class));
	assertEquals("InnerClass", getShortClassName(InnerClass.class));
   }

   @Test
   public void testGetNoArgMethod() {
	final String notArgMethodName = "getNoArgMethod";

	assertEquals(notArgMethodName, getMethodWithNoArgs(ReflectionHelperTest.class, notArgMethodName).getName());
	assertEquals(notArgMethodName, getMethodWithNoArgs(LocalFirstAncestor.class, notArgMethodName).getName());
	assertEquals(notArgMethodName, getMethodWithNoArgs(LocalInnerClass.class, notArgMethodName).getName());
	assertEquals(notArgMethodName, getMethodWithNoArgs(InnerClass.class, notArgMethodName).getName());
   }

   @Test
   public void testGetAllDeclaredMethodsReturns() {
	Set<Class<?>> allDeclaredMethodsReturns = getAllDeclaredMethodsReturns(LocalFirstAncestor.class);

	assertNotNull(allDeclaredMethodsReturns);
	assertTrue(allDeclaredMethodsReturns.size() >= 2);
	assertTrue(allDeclaredMethodsReturns.contains(String.class));
	assertTrue(allDeclaredMethodsReturns.contains(Void.TYPE));
   }

   @Test
   public void testGetAllDeclaredMethods() {
	Map<String, Method> methodsByName = getAllDeclaredMethodsByName(LocalFirstAncestor.class);

	assertNotNull(methodsByName);
	assertTrue(methodsByName.keySet().contains("interface1Method"));
	assertTrue(methodsByName.keySet().contains("interface2Method"));
	assertTrue(methodsByName.keySet().contains("ancestorMethod"));
	assertTrue(methodsByName.keySet().contains("getNoArgMethod"));

	assertNotNull(methodsByName.get("interface1Method"));
	assertNotNull(methodsByName.get("interface2Method"));
	assertNotNull(methodsByName.get("ancestorMethod"));
	assertNotNull(methodsByName.get("getNoArgMethod"));

	assertEquals("interface1Method", methodsByName.get("interface1Method").getName());
	assertEquals("interface2Method", methodsByName.get("interface2Method").getName());
	assertEquals("ancestorMethod", methodsByName.get("ancestorMethod").getName());
	assertEquals("getNoArgMethod", methodsByName.get("getNoArgMethod").getName());

   }

   @Test
   public void testGetAllInterfaces() {
	Set<Class<?>> interfaces = getAllInterfaces(LocalFirstAncestor.class);
	assertNotNull(interfaces);
	assertTrue(interfaces.contains(LocalInterface1.class));
	assertTrue(interfaces.contains(LocalInterface2.class));
	assertTrue(interfaces.contains(Serializable.class));
	assertFalse(interfaces.contains(DateFormat.class));
   }

   @Test
   public void testGetSuperClasses() {
	assertNotNull(getSuperClasses(LocalSecondAncestor.class));
	assertEquals(2, getSuperClasses(LocalSecondAncestor.class).length);
	assertEquals(LocalFirstAncestor.class, getSuperClasses(LocalSecondAncestor.class)[0]);
	assertEquals(LocalAncestor.class, getSuperClasses(LocalSecondAncestor.class)[1]);
   }

   @Test
   public void testGetAllDeclaredFields() throws NoSuchFieldException {
	Set<Field> fields = null;

	// ** 1. test with multiple modifiers ***************************
	fields = getAllDeclaredFields(LocalSecondAncestor.class, Modifier.PRIVATE, Modifier.PUBLIC, Modifier.PROTECTED);
	assertNotNull(fields);
	// direct field
	assertTrue(fields.contains(LocalSecondAncestor.class.getDeclaredField("privateLocalSecondAncestorField")));
	assertTrue(fields.contains(LocalSecondAncestor.class.getField("publicLocalSecondAncestorField")));
	assertTrue(fields.contains(LocalSecondAncestor.class.getDeclaredField("protectedLocalSecondAncestorField")));
	// first ancestor accessible field
	assertTrue(fields.contains(LocalFirstAncestor.class.getDeclaredField("privateLocalFirstAncestorField")));
	assertTrue(fields.contains(LocalFirstAncestor.class.getDeclaredField("protectedLocalFirstAncestorField")));
	assertTrue(fields.contains(LocalFirstAncestor.class.getField("publicLocalFirstAncestorField")));
	// second ancestor accessible field
	assertTrue(fields.contains(LocalAncestor.class.getDeclaredField("privateLocalAncestorField")));
	assertTrue(fields.contains(LocalAncestor.class.getDeclaredField("protectedLocalAncestorField")));
	assertTrue(fields.contains(LocalAncestor.class.getField("publicLocalAncestorField")));
	assertEquals(10, fields.size()); // 9 + 1 (serialVersionUid)

	// ** 2. test with single modifiers ***************************
	fields = getAllDeclaredFields(LocalSecondAncestor.class, Modifier.PUBLIC);
	assertNotNull(fields);
	assertEquals(3, fields.size());

	// ** 3. test with single modifiers + annotation ***************************
	fields = getAllDeclaredFields(LocalSecondAncestor.class, Column.class, Modifier.PRIVATE);
	assertNotNull(fields);
	assertEquals(3, fields.size());
   }

   // need by test
   public String getNoArgMethod() {
	return null;
   }

   class InnerClass {
	public String getNoArgMethod() {
	   return null;
	}
   }
}

interface LocalInterface1 {
   public void interface1Method();
}

interface LocalInterface2 {
   public void interface2Method();
}

class LocalAncestor implements LocalInterface1, LocalInterface2 {

   @SuppressWarnings("unused")
   @Column
   private String privateLocalAncestorField;
   protected String protectedLocalAncestorField;
   public String publicLocalAncestorField;

   @Override
   public void interface2Method() {
   }

   @Override
   public void interface1Method() {
   }

   public void ancestorMethod() {
   }

}

class LocalFirstAncestor extends LocalAncestor implements Serializable {
   private static final long serialVersionUID = 6056399394375997622L;

   @SuppressWarnings("unused")
   // @Column a runtime annotation to test field filter
   @Column
   private String privateLocalFirstAncestorField;
   protected String protectedLocalFirstAncestorField;
   public String publicLocalFirstAncestorField;

   public String getNoArgMethod() {
	return null;
   }

   static class LocalInnerClass {
	public String getNoArgMethod() {
	   return null;
	}
   }
}

class LocalSecondAncestor extends LocalFirstAncestor {
   private static final long serialVersionUID = -4618815239259030789L;

   @SuppressWarnings("unused")
   @Column
   private String privateLocalSecondAncestorField;
   protected String protectedLocalSecondAncestorField;
   public String publicLocalSecondAncestorField;
}