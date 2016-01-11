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
package org.kaleidofoundry.core.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * Utilities for managing java system like :
 * <ul>
 * <li>access to java env variables (define as : <code>-Dlog4j.file=....</code>)</li>
 * <li>access to class loader (current thread class loader...),</li>
 * <li>access to class loader resources (used for loading: configuration file, text file, binaries presents in your classpath)</li>
 * </ul>
 * 
 * @author jraduget
 */
public class JavaSystemHelper {

   /**
    * wrapper which provide access to java environment value
    * 
    * @param propKey java environment property name
    * @param defaultValue default value to return if none property found.
    * @return The String value of the property java requested, or default if no property was found
    * @see System#getProperties()
    */
   public static String getSystemProperty(final String propKey, final String defaultValue) {
	return System.getProperty(propKey, defaultValue);
   }

   /**
    * wrapper : see {@link System}<code>.getProperties</code>
    * 
    * @return Java system properties
    */
   public static Properties getSystemProperties() {
	return System.getProperties();
   }

   /**
    * @return current class loader (class loader of Thread.currentThread(), otherwise class loader of current class)
    */
   public static ClassLoader getCurrentClassLoader() {

	ClassLoader classLoader = null;

	// 1. try to get Thread.currentThread class loader
	try {
	   final Method method = Thread.class.getMethod("getContextClassLoader", (Class<?>[]) null);
	   classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
	   if (classLoader != null) { return classLoader; }
	} catch (final Throwable th) {
	}

	// 2. use default JavaSystemHelper classLoader
	return JavaSystemHelper.class.getClassLoader();
   }

   /**
    * Finder all resources in java classpath (classes, and jars...) among the given classLoader<br/>
    * The search order via class loader is follows:
    * <ul>
    * <li>the given classloader in argument</li>
    * <li>current thread class loader</li>
    * <li>current class {@link JavaSystemHelper} class loader</li>
    * </ul>
    * <p>
    * The resources found can be: configuration file, text resource, binaries...
    * </p>
    * 
    * @param classLoader the class loader to be used for research
    * @param resourceName
    * @return it never return null, if none resource was found, return an empty {@link Enumeration}
    * @throws IOException
    */
   public static Enumeration<URL> getResources(@NotNull final ClassLoader classLoader, @NotNull final String resourceName) throws IOException {
	Enumeration<URL> urls = null;

	// 1. given classloader
	urls = classLoader.getResources(resourceName);
	if (urls != null) { return urls; }

	// 2. current thread class loader
	urls = getCurrentClassLoader().getResources(resourceName);
	if (urls != null) { return urls; }

	// 3. current class class loader
	urls = JavaSystemHelper.class.getClassLoader().getResources(resourceName);
	if (urls != null) { return urls; }

	// other case ? ... ?
	// like JavaSystemHelper.class.getClassLoader().getSystemClassLoader().getResources(...) ?
	return urls;
   }

   /**
    * call {@link #getResources(ClassLoader, String)} with {@link JavaSystemHelper} class loader
    * 
    * @see JavaSystemHelper#getResources(ClassLoader, String)
    * @param resourceName
    * @return return will never be null, if none resource was found, return an empty {@link Enumeration}
    * @throws IOException
    */
   public static Enumeration<URL> getResources(final String resourceName) throws IOException {
	return getResources(getCurrentClassLoader(), resourceName);
   }

   /**
    * Finder all resources in java classpath (classes, and jars...) among the given classLoader<br/>
    * The search order via class loader is follows:
    * <ul>
    * <li>the given classloader in argument</li>
    * <li>current thread class loader</li>
    * <li>current class {@link JavaSystemHelper} class loader</li>
    * </ul>
    * <p>
    * The resources found can be: configuration file, text resource, binaries...
    * </p>
    * 
    * @param classLoader the class loader to be used for research
    * @param resourceName relative path of the resource
    * @return the url of the first found resource in classpath (classes + jar) of the given classLoader
    */
   @Nullable
   public static URL getResource(@NotNull final ClassLoader classLoader, @NotNull final String resourceName) {
	URL url = null;

	// 1. given classloader
	url = classLoader.getResource(resourceName);
	if (url != null) { return url; }

	// 2. current thread class loader
	url = getCurrentClassLoader().getResource(resourceName);
	if (url != null) { return url; }

	// 3. current class class loader
	url = JavaSystemHelper.class.getClassLoader().getResource(resourceName);
	if (url != null) { return url; }

	// other case ? ... ?
	// like JavaSystemHelper.class.getClassLoader().getSystemClassLoader().getResource(...) ?
	return url;

   }

   /**
    * call {@link #getResource(ClassLoader, String)} with {@link JavaSystemHelper} class loader
    * 
    * @see JavaSystemHelper#getResource(ClassLoader, String)
    * @param resourceName relative path of the resource
    * @return the url of the first found resource in classpath (classes + jar)
    */
   @Nullable
   public static URL getResource(final String resourceName) {
	return getResource(getCurrentClassLoader(), resourceName);
   }

   /**
    * use {@link JavaSystemHelper#getResource(ClassLoader, String)} to get a {@link File} instance of the resource
    * 
    * @param classLoader {@link ClassLoader} to use
    * @param resourceName relative path of the resource
    * @return {@link File} instance on the found resource, or null if not found
    */
   @Nullable
   public static File getResourceAsFile(final ClassLoader classLoader, final String resourceName) {

	final URL url = getResource(classLoader, resourceName);
	if (url != null) {
	   return new File(url.getFile());
	} else {
	   return null;
	}
   }

   /**
    * use {@link JavaSystemHelper#getResource(String)} to get a {@link File} instance of the resource
    * 
    * @param resourceName relative path of the resource
    * @return the first found resource in classpath (classes + jar)<br/> {@link File} instance on the found resource, or null if not found
    */
   @Nullable
   public static File getResourceAsFile(final String resourceName) {
	return getResourceAsFile(getCurrentClassLoader(), resourceName);
   }

   /**
    * Finder resource in java classpath (classes, and jars...) among the given classLoader<br/>
    * The search order via class loader is follows:
    * <ul>
    * <li>the given classloader in argument</li>
    * <li>current thread class loader</li>
    * <li>current class {@link JavaSystemHelper} class loader</li>
    * </ul>
    * <p>
    * The resources found can be: configuration file, text resource, binaries...
    * </p>
    * 
    * @param classLoader the class loader to be used for research
    * @param resourceName
    * @return the first found resource in classpath (classes + jar) of the given classLoader
    */
   @Nullable
   public static InputStream getResourceAsStream(@NotNull final ClassLoader classLoader, @NotNull final String resourceName) {
	InputStream in = null;

	// 1. given classloader
	in = classLoader.getResourceAsStream(resourceName);
	if (in != null) { return in; }

	// 2. current thread class loader
	in = getCurrentClassLoader().getResourceAsStream(resourceName);
	if (in != null) { return in; }

	// 3. current class class loader
	in = JavaSystemHelper.class.getClassLoader().getResourceAsStream(resourceName);
	if (in != null) { return in; }

	// other case ? ... ?
	// like JavaSystemHelper.class.getClassLoader().getSystemClassLoader().getResourceAsStream(...) ?
	return in;

   }

   /**
    * use {@link JavaSystemHelper#getResourceAsStream(ClassLoader, String)} with {@link JavaSystemHelper#getCurrentClassLoader()}
    * 
    * @param resourceName relative path of the resource
    * @return the first found resource in classpath (classes + jar)<br/>
    *         inputstream instance on the found resource, or null if not found
    */
   @Nullable
   public static InputStream getResourceAsStream(final String resourceName) {
	return getResourceAsStream(getCurrentClassLoader(), resourceName);
   }
}
