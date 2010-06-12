package org.kaleidofoundry.core.util;

import static org.kaleidofoundry.core.util.ReflectionHelper.getAllDeclaredMethodsByName;
import static org.kaleidofoundry.core.util.ReflectionHelper.getAllDeclaredMethodsReturns;
import static org.kaleidofoundry.core.util.ReflectionHelper.getAllInterfaces;
import static org.kaleidofoundry.core.util.ReflectionHelper.getMethodWithNoArgs;
import static org.kaleidofoundry.core.util.ReflectionHelper.getShortClassName;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.kaleidofoundry.core.util.LocalClass.LocalInnerClass;

/**
 * test each method (static) in {@link ReflectionHelper}
 * 
 * @author Jerome RADUGET
 */
public class ReflectionHelperTest extends Assert {

   @Test
   public void testGetShortClassName() {
	assertEquals("ReflectionHelperTest", getShortClassName(ReflectionHelperTest.class));
	assertEquals("LocalClass", getShortClassName(LocalClass.class));
	assertEquals("LocalInnerClass", getShortClassName(LocalInnerClass.class));
	assertEquals("InnerClass", getShortClassName(InnerClass.class));
   }

   @Test
   public void testGetNoArgMethod() {
	final String notArgMethodName = "getNoArgMethod";

	assertEquals(notArgMethodName, getMethodWithNoArgs(ReflectionHelperTest.class, notArgMethodName).getName());
	assertEquals(notArgMethodName, getMethodWithNoArgs(LocalClass.class, notArgMethodName).getName());
	assertEquals(notArgMethodName, getMethodWithNoArgs(LocalInnerClass.class, notArgMethodName).getName());
	assertEquals(notArgMethodName, getMethodWithNoArgs(InnerClass.class, notArgMethodName).getName());
   }

   @Test
   public void testGetAllDeclaredMethodsReturns() {
	Set<Class<?>> allDeclaredMethodsReturns = getAllDeclaredMethodsReturns(LocalClass.class);

	assertNotNull(allDeclaredMethodsReturns);
	assertTrue(allDeclaredMethodsReturns.size() >= 2);
	assertTrue(allDeclaredMethodsReturns.contains(String.class));
	assertTrue(allDeclaredMethodsReturns.contains(Void.TYPE));
   }

   @Test
   public void testGetAllDeclaredMethods() {
	Map<String, Method> methodsByName = getAllDeclaredMethodsByName(LocalClass.class);

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
	Set<Class<?>> interfaces = getAllInterfaces(LocalClass.class);
	assertNotNull(interfaces);
	assertTrue(interfaces.contains(LocalInterface1.class));
	assertTrue(interfaces.contains(LocalInterface2.class));
	assertTrue(interfaces.contains(Serializable.class));
	assertFalse(interfaces.contains(DateFormat.class));
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

   private static final long serialVersionUID = 6056399394375997621L;

   @Override
   public void interface2Method() {
   }

   @Override
   public void interface1Method() {
   }

   public void ancestorMethod() {
   }

}

class LocalClass extends LocalAncestor implements Serializable {
   private static final long serialVersionUID = 6056399394375997622L;

   public String getNoArgMethod() {
	return null;
   }

   static class LocalInnerClass {
	public String getNoArgMethod() {
	   return null;
	}
   }
}