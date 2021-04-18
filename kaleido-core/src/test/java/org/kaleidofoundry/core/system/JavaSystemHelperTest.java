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
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.lang.NotNullException;

/**
 * @author jraduget
 */
public class JavaSystemHelperTest  {

   public static final String UNKNOWN_FOO_RESOURCE = "foo/unkwnown";
   public static final String FOO_RESOURCE = "system/foo.txt";
   public static final String META_INF_JAR_RESOURCE = "META-INF/MANIFEST.MF";

   @Test
   public void getSystemProperties() {
	final Properties props = JavaSystemHelper.getSystemProperties();
	assertNotNull(props);
	assertTrue(props.size() > 0);
	assertNotNull(props.get("os.name"));
	assertNotNull(props.get("java.vm.version"));
   }

   @Test
   public void getSystemProperty() {
	assertNotNull(JavaSystemHelper.getSystemProperty("os.name", null));
	assertNull(JavaSystemHelper.getSystemProperty("foo.unknown", null));
	assertEquals("foo", JavaSystemHelper.getSystemProperty("foo.unknown", "foo"));
   }

   @Test
   public void getCurrentClassLoader() {
	assertNotNull(JavaSystemHelper.getCurrentClassLoader());
   }

   @Test
   public void getResources() throws IOException {
	// unknown resource
	Enumeration<URL> urls = JavaSystemHelper.getResources(UNKNOWN_FOO_RESOURCE);
	assertNotNull(urls);
	assertFalse(urls.hasMoreElements());
	// existing resource
	urls = JavaSystemHelper.getResources(FOO_RESOURCE);
	assertNotNull(urls);
	assertTrue(urls.hasMoreElements());
	final URL url = urls.nextElement();
	assertNotNull(url);
	// existing jar resource (and more than one resources)
	urls = JavaSystemHelper.getResources(META_INF_JAR_RESOURCE);
	assertNotNull(urls);
	assertTrue(urls.hasMoreElements());
	urls.nextElement();
	assertTrue(urls.hasMoreElements());
	urls.nextElement();
   }

   @Test
   public void getResourcesWithClassLoader() throws IOException {
	// unknown resource
	Enumeration<URL> urls = JavaSystemHelper.getResources(JavaSystemHelper.class.getClassLoader(), UNKNOWN_FOO_RESOURCE);
	assertNotNull(urls);
	assertFalse(urls.hasMoreElements());
	// existing resource
	urls = JavaSystemHelper.getResources(JavaSystemHelper.class.getClassLoader(), FOO_RESOURCE);
	assertNotNull(urls);
	assertTrue(urls.hasMoreElements());
	final URL url = urls.nextElement();
	assertNotNull(url);
	// existing jar resource (and more than one resources)
	urls = JavaSystemHelper.getResources(JavaSystemHelper.class.getClassLoader(), META_INF_JAR_RESOURCE);
	assertNotNull(urls);
	assertTrue(urls.hasMoreElements());
	urls.nextElement();
	assertTrue(urls.hasMoreElements());
	urls.nextElement();
   }

   @Test(expected = NotNullException.class)
   public void getResourcesWithIllegalClassLoader() throws IOException {
	JavaSystemHelper.getResources(String.class.getClassLoader(), UNKNOWN_FOO_RESOURCE);
   }

   @Test
   public void getResource() {
	// unknown resource
	URL url = JavaSystemHelper.getResource(UNKNOWN_FOO_RESOURCE);
	assertNull(url);
	// existing resource
	url = JavaSystemHelper.getResource(FOO_RESOURCE);
	assertNotNull(url);
	// existing jar resource
	url = JavaSystemHelper.getResource(META_INF_JAR_RESOURCE);
	assertNotNull(url);
	assertTrue(url.toExternalForm().startsWith("file:/"));
   }

   @Test
   public void getResourceWithClassLoader() {
	// unknown resource
	URL url = JavaSystemHelper.getResource(JavaSystemHelper.class.getClassLoader(), UNKNOWN_FOO_RESOURCE);
	assertNull(url);
	// existing resource
	url = JavaSystemHelper.getResource(JavaSystemHelper.class.getClassLoader(), FOO_RESOURCE);
	assertNotNull(url);
   }

   @Test(expected = NotNullException.class)
   public void getResourceWithIllegalClassLoader() throws IOException {
	// unknown resource
	JavaSystemHelper.getResource(String.class.getClassLoader(), UNKNOWN_FOO_RESOURCE);
   }

   @Test
   public void getResourceAsFile() {
	// unknown resource
	File file = JavaSystemHelper.getResourceAsFile(UNKNOWN_FOO_RESOURCE);
	assertNull(file);
	// existing resource in the classpath (file system)
	file = JavaSystemHelper.getResourceAsFile(FOO_RESOURCE);
	assertNotNull(file);
	// existing resource in the classpath (jar zip)
	file = JavaSystemHelper.getResourceAsFile(META_INF_JAR_RESOURCE);
	assertNotNull(file);
   }

   @Test
   public void getResourceAsFileWithClassLoader() {
	// unknown resource
	File file = JavaSystemHelper.getResourceAsFile(JavaSystemHelper.class.getClassLoader(), UNKNOWN_FOO_RESOURCE);
	assertNull(file);
	// existing resource
	file = JavaSystemHelper.getResourceAsFile(JavaSystemHelper.class.getClassLoader(), FOO_RESOURCE);
	assertNotNull(file);
   }

   @Test
   public void getResourceAsStream() {
	// unknown resource
	InputStream in = JavaSystemHelper.getResourceAsStream(UNKNOWN_FOO_RESOURCE);
	assertNull(in);
	// existing resource
	in = JavaSystemHelper.getResourceAsStream(FOO_RESOURCE);
	assertNotNull(in);
   }

   @Test
   public void getResourceAsStreamWithClassLoader() {
	// unknown resource
	InputStream in = JavaSystemHelper.getResourceAsStream(JavaSystemHelper.class.getClassLoader(), UNKNOWN_FOO_RESOURCE);
	assertNull(in);
	// existing resource
	in = JavaSystemHelper.getResourceAsStream(JavaSystemHelper.class.getClassLoader(), FOO_RESOURCE);
	assertNotNull(in);
   }

}
