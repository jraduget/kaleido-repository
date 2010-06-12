package org.kaleidofoundry.core.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.kaleidofoundry.core.lang.NotNullException;

/**
 * @author Jerome RADUGET
 */
public class JavaSystemHelperTest extends Assert {

   public static final String UNKNOWN_FOO_RESOURCE = "foo/unkwnown";
   public static final String FOO_RESOURCE = "org/kaleidofoundry/core/system/foo.txt";
   public static final String META_INF_JAR_RESOURCE = "META-INF/MANIFEST.MF";

   @Test
   public void getSystemProperties() {
	Properties props = JavaSystemHelper.getSystemProperties();
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
	URL url = urls.nextElement();
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
	URL url = urls.nextElement();
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
	// existing resource
	file = JavaSystemHelper.getResourceAsFile(FOO_RESOURCE);
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
