/*  
 * Copyright 2008-2010 the original author or authors 
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

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static java.lang.reflect.Modifier.TRANSIENT;
import static java.lang.reflect.Modifier.VOLATILE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.lang.annotation.TestIt;
import org.kaleidofoundry.core.lang.annotation.Tested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for managing class introspection
 * 
 * @author Jerome RADUGET
 */
public abstract class ReflectionHelper {

   private static Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);

   /**
    * @param c Class that you want the "short name"
    * @return short name of the class in argument (for inner class too) <br/>
    *         example :
    *         <ul>
    *         <li>org.kaleido.core.Configuration -> Configuration</li>
    *         <li>org.kaleido.core.Configuration$InnerClass -> InnerClass</li>
    *         </ul>
    */
   @Tested
   @NotNull
   public static final String getShortClassName(@NotNull final Class<?> c) {
	final String name = c.getCanonicalName();
	final int pos = name.lastIndexOf(".");
	if (pos > 0) {
	   return name.substring(pos + 1, name.length());
	} else {
	   return name;
	}
   }

   /**
    * @param pClass class to introspect
    * @param name method name with no arg that we search
    * @return Method instance
    */
   @Tested
   @Nullable
   public static final Method getMethodWithNoArgs(@NotNull final Class<?> pClass, final String name) {
	try {
	   final Method method = pClass.getMethod(name, new Class[] {});
	   return method;
	} catch (final NoSuchMethodException nsme) {
	   return null;
	}
   }

   /**
    * @param pClass
    * @return array of all classes, return by class declared methods (be careful, {@link Void} & {@link Void#TYPE} is a
    *         type ;-) )
    */
   @Tested
   @NotNull
   public static final Set<Class<?>> getAllDeclaredMethodsReturns(@NotNull final Class<?> pClass) {

	final Set<Class<?>> result = new HashSet<Class<?>>();
	final Collection<Method> methods = getAllDeclaredMethods(pClass);

	for (final Method method : methods) {
	   if (method.getReturnType() != null) {
		result.add(method.getReturnType());
	   }
	}
	return result;
   }

   /**
    * @param c class which we seek all methods
    * @return Returns the full declared and inherited methods of a class. Look in the super class and implemented
    *         interfaces to build its result
    */
   @Tested
   @NotNull
   public static final Collection<Method> getAllDeclaredMethods(@NotNull final Class<?> c) {
	return getAllDeclaredMethodsByName(c).values();
   }

   /**
    * @param c class which we seek all methods
    * @return Returns map by name of the full declared and inherited methods of a class. Look in the super class and
    *         implemented
    *         interfaces to build its result
    */
   @Tested
   @NotNull
   public static final Map<String, Method> getAllDeclaredMethodsByName(@NotNull final Class<?> c) {

	final Map<String, Method> allMethods = new HashMap<String, Method>();
	Class<?> superClass = c;

	// Super Class
	while (superClass != null && !superClass.equals(Object.class)) {

	   for (final Method m : superClass.getDeclaredMethods()) {
		allMethods.put(m.getName(), m);
	   }

	   if (!superClass.equals(c.getSuperclass())) {
		superClass = c.getSuperclass();
	   } else {
		break;
	   }
	}

	// Interfaces
	final Class<?>[] interfaces = c.getInterfaces();
	if (interfaces != null) {
	   for (final Class<?> interface1 : interfaces) {
		allMethods.putAll(getAllDeclaredMethodsByName(interface1));
	   }
	}
	return allMethods;
   }

   /**
    * @param c Class which we seek all interfaces
    * @return list of all interfaces implemented by the class as argument
    */
   @Tested
   @NotNull
   public static final Set<Class<?>> getAllInterfaces(final Class<?> c) {

	final Set<Class<?>> set = new HashSet<Class<?>>();
	Class<?> superClass = c;

	// Super Class
	while (superClass != null && !superClass.equals(Object.class)) {

	   for (final Class<?> i : superClass.getInterfaces()) {
		set.add(i);
	   }

	   if (!superClass.equals(c.getSuperclass())) {
		superClass = c.getSuperclass();
	   } else {
		break;
	   }
	}

	// Interfaces
	final Class<?>[] interfaces = c.getInterfaces();
	if (interfaces != null) {
	   for (final Class<?> interface1 : interfaces) {
		set.add(interface1);
		set.addAll(getAllInterfaces(interface1));
	   }
	}

	return set;
   }

   /**
    * @param c
    * @return Extract all super classes of a given class ({@link Object} is not included)
    */
   @Tested
   @NotNull
   public static final Class<?>[] getSuperClasses(@NotNull final Class<?> c) {

	final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
	Class<?> superClass = c.getSuperclass();

	// Super Class
	while (superClass != null && !superClass.equals(superClass.getSuperclass()) && !superClass.equals(Object.class)) {
	   classSet.add(superClass);
	   superClass = superClass.getSuperclass();
	}

	return classSet.toArray(new Class<?>[classSet.size()]);
   }

   /**
    * @param c
    * @param fieldname
    * @return Extract the field named "fieldname" of the current class, or from the super classe<b>s</b> of the given class.
    */
   public static final Field getDeclaredField(@NotNull final Class<?> c, @NotNull final String fieldname) {
	for (Field f : getAllDeclaredFields(c)) {
	   if (fieldname.equalsIgnoreCase(f.getName())) { return f; }
	}
	return null;
   }

   /**
    * @param c
    * @return Extract all declared fields of the current class, and from the super classe<b>s</b> of the given class.
    */
   @NotNull
   @Tested
   public static final Set<Field> getAllDeclaredFields(@NotNull final Class<?> c) {
	return getAllDeclaredFields(c, (Class<? extends Annotation>) null);
   }

   /**
    * @param c
    * @param annotation filter fields that are annotated by this annotation (is not null it is ignored)
    * @return Extract all declared fields of the current class, and from of the super classe<b>s</b> of the given class.
    */
   @NotNull
   @Tested
   public static final Set<Field> getAllDeclaredFields(@NotNull final Class<?> c, final Class<? extends Annotation> annotation) {
	return getAllDeclaredFields(c, annotation, new int[0]);
   }

   /**
    * @param c
    * @param modifiers the field modifiers filter, see {@link Modifier}
    * @return Extract all declared fields of the current class, and from of the super classe<b>s</b> of the given class.
    */
   @Tested
   public static final Set<Field> getAllDeclaredFields(@NotNull final Class<?> c, final int... modifiers) {
	return getAllDeclaredFields(c, null, modifiers);
   }

   /**
    * @param c
    * @param annotation filter fields that are annotated by this annotation (is not null it is ignored)
    * @param modifiers the field modifiers filter, see {@link Modifier}
    * @return Extract all declared fields of the current class and from the super classe<b>s</b> of the given class.
    * @see Modifier
    */
   @NotNull
   @Tested
   public static final Set<Field> getAllDeclaredFields(@NotNull final Class<?> c, final Class<? extends Annotation> annotation, final int... modifiers) {

	final Set<Field> fieldSet = new LinkedHashSet<Field>();

	// all fields (private, protected, public, package)
	for (Field f : c.getDeclaredFields()) {
	   if (annotation == null || f.isAnnotationPresent(annotation)) {
		if (modifiers.length <= 0) {
		   fieldSet.add(f);
		} else {
		   for (int mod : modifiers) {
			if ((mod & PUBLIC) != 0 && Modifier.isPublic(f.getModifiers())) {
			   fieldSet.add(f);
			}
			if ((mod & PROTECTED) != 0 && Modifier.isProtected(f.getModifiers())) {
			   fieldSet.add(f);
			}
			if ((mod & PRIVATE) != 0 && Modifier.isPrivate(f.getModifiers())) {
			   fieldSet.add(f);
			}
			if ((mod & STATIC) != 0 && Modifier.isStatic(f.getModifiers())) {
			   fieldSet.add(f);
			}
			if ((mod & FINAL) != 0 && Modifier.isFinal(f.getModifiers())) {
			   fieldSet.add(f);
			}
			if ((mod & TRANSIENT) != 0 && Modifier.isTransient(f.getModifiers())) {
			   fieldSet.add(f);
			}
			if ((mod & VOLATILE) != 0 && Modifier.isVolatile(f.getModifiers())) {
			   fieldSet.add(f);
			}
		   }
		}
	   }
	}

	for (Class<?> lc : getSuperClasses(c)) {
	   // all accessible field (public, protected, package)
	   for (Field f : lc.getDeclaredFields()) {
		if ((annotation == null || f.isAnnotationPresent(annotation)) && !"serialVersionUID".equals(f.getName())) {
		   if (modifiers.length <= 0) {
			fieldSet.add(f);
		   } else {
			for (int mod : modifiers) {
			   if ((mod & PUBLIC) != 0 && Modifier.isPublic(f.getModifiers())) {
				fieldSet.add(f);
			   }
			   if ((mod & PROTECTED) != 0 && Modifier.isProtected(f.getModifiers())) {
				fieldSet.add(f);
			   }
			   if ((mod & PRIVATE) != 0 && Modifier.isPrivate(f.getModifiers())) {
				fieldSet.add(f);
			   }
			   if ((mod & STATIC) != 0 && Modifier.isStatic(f.getModifiers())) {
				fieldSet.add(f);
			   }
			   if ((mod & FINAL) != 0 && Modifier.isFinal(f.getModifiers())) {
				fieldSet.add(f);
			   }
			   if ((mod & TRANSIENT) != 0 && Modifier.isTransient(f.getModifiers())) {
				fieldSet.add(f);
			   }
			   if ((mod & VOLATILE) != 0 && Modifier.isVolatile(f.getModifiers())) {
				fieldSet.add(f);
			   }
			}
		   }
		}
	   }
	}
	return fieldSet;
   }

   /**
    * Calling a static method on a class. <br/>
    * Note: The exceptions are silent. They are intercepted and ignored,but logged in!
    * 
    * @param method Method name to invoke
    * @return static method result (or Void.TYPE if exception...)
    */
   @TestIt
   public static final Object invokeStaticMethodSilently(final Method method) {
	return invokeStaticMethodSilently(method, null);
   }

   /**
    * Calling a static method on a class with parameters. <br/>
    * Note: The exceptions are silent. They are intercepted and ignored,but logged in!
    * 
    * @param method Method name to invoke
    * @param args method args
    * @return static method result (or Void.TYPE if exception...)
    */
   @TestIt
   public static final Object invokeStaticMethodSilently(@NotNull final Method method, final Object[] args) {

	try {
	   return method.invoke(null, args != null ? args : new Object[] {});
	} catch (final IllegalAccessException iae) {
	   LOGGER.error(iae.getMessage(), iae);
	   return Void.TYPE;
	} catch (final InvocationTargetException ite) {
	   LOGGER.error(ite.getMessage(), ite.getTargetException());
	   return Void.TYPE;
	}
   }

   /**
    * Invocation property chained to an instance <br/>
    * Example : if an instance have a getter getName(), you could do :
    * <ul>
    * <li>person.name</li>
    * <li>person.name.length</li>
    * </ul>
    * 
    * @param propertiesChain
    * @param instance
    * @param propertiesChainSeparator
    * @return method invoke result
    * @throws NoSuchMethodException If no method has been found (or getter method) for the property argument
    * @throws IllegalAccessException Attempted execution of the method due to a problem of access (private ,...)
    * @throws InvocationTargetException Exception during execution of the method body
    */
   @TestIt
   public static Object invokePropertyChain(final String propertiesChain, final Object instance, final String propertiesChainSeparator)
	   throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
	int propertiesSeparatorIndex = -1;
	String firstProperty = null;

	propertiesSeparatorIndex = propertiesChain.indexOf(propertiesChainSeparator);

	if (propertiesSeparatorIndex >= 0) {

	   firstProperty = propertiesChain.substring(0, propertiesSeparatorIndex);

	   final Object result = invokePropertyWithNoArgs(instance, firstProperty);

	   return invokePropertyChain(propertiesChain.substring(propertiesSeparatorIndex + 1), result, propertiesChainSeparator);

	} else {

	   if (instance != null) {
		firstProperty = propertiesChain;
		return invokePropertyWithNoArgs(instance, firstProperty);
	   } else {
		return null;
	   }
	}
   }

   /**
    * @param instance
    * @param property
    * @return method invoke result
    * @throws NoSuchMethodException If no method has been found (or getter method) for the property argument
    * @throws IllegalAccessException Attempted execution of the method due to a problem of access (private ,...)
    * @throws InvocationTargetException Exception during execution of the method body
    * @see #invokePropertyChain(String, Object, String)
    */
   @TestIt
   public static Object invokePropertyWithNoArgs(final Object instance, final String property) throws NoSuchMethodException, IllegalAccessException,
	   InvocationTargetException {
	Method method = null;

	method = ReflectionHelper.getMethodWithNoArgs(instance.getClass(), "get" + StringHelper.upperCase1(property));

	if (method == null) {
	   method = ReflectionHelper.getMethodWithNoArgs(instance.getClass(), property);
	}

	if (method == null) { throw new NoSuchMethodException(property); }

	return method.invoke(instance, new Object[] {});
   }

}
