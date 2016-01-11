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
package org.kaleidofoundry.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * IOHelper test cases
 * 
 * @author jraduget
 */
public class IOHelperTest  {

   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();

   private String tempFilename;

   @Before
   public void setup() throws IOException {
	File tempFile = tempFolder.newFile("iohelper.test");
	tempFilename = tempFile.getCanonicalPath();

	final FileWriter writer = new FileWriter(tempFile);
	writer.append("foo");
	writer.close();
   }

   @After
   public void cleanup() {
   }

   // ** Test methods ************************************************************************************************

   // illegal test getResourceInputStream
   @Test
   public void testIllegalHttpResourceInputStream() {
	final InputStream in = IOHelper.getResourceInputStream("http://none.none");
	assertNull(in);
   }

   @Test
   public void testIllegalFileResourceInputStream() {
	final InputStream in = IOHelper.getResourceInputStream("/none.none");
	assertNull(in);
   }

   @Test
   public void testIllegalClasspathResourceInputStream() {
	final InputStream in = IOHelper.getResourceInputStream("none/none");
	assertNull(in);
   }

   @Test
   public void testIllegalJarClasspathResourceInputStream() {
	final InputStream in = IOHelper.getResourceInputStream("none/none");
	assertNull(in);
   }

   // legal test getResourceInputStream
   @Test
   public void testLegalHttpResourceInputStream() {
	// if failed, be careful to proxy settings ;-)
	final String resource = "http://www.google.fr";
	final InputStream in = IOHelper.getResourceInputStream(resource);
	legalAssert(resource, in);
   }

   @Test
   public void testLegalFileResourceInputStream() {
	final String resource = tempFilename;
	final InputStream in = IOHelper.getResourceInputStream(tempFilename);
	legalAssert(resource, in);
   }

   @Test
   public void testLegalClasspathResourceInputStream() {
	final String resource = "log4j.xml";
	final InputStream in = IOHelper.getResourceInputStream(resource);
	legalAssert(resource, in);
   }

   @Test
   public void testLegalJarClasspathResourceInputStream() {
	final String resource = "META-INF/MANIFEST.MF";
	final InputStream in = IOHelper.getResourceInputStream(resource);
	legalAssert(resource, in);
   }

   @Test
   public void testLegalReadlines() throws IOException {
	IoIterable<String> lines = null;
	try {
	   lines = IOHelper.readlines(tempFilename);
	   assertNotNull(lines);
	   assertEquals("foo", lines.iterator().next());
	   assertNull(lines.iterator().next());
	} finally {
	   if (lines != null) {
		lines.close();
	   }
	}
   }

   @Test(expected = IOException.class)
   public void testIllegalReadlines() throws IOException {
	IOHelper.readlines("/none.none");
   }

   /**
    * @param resourcename
    * @param in
    */
   void legalAssert(final String resourcename, final InputStream in) {
	assertNotNull(in);
	debugInputStreamContent(resourcename, in);
   }

   /**
    * @param resourcename
    * @param in
    */
   protected void debugInputStreamContent(final String resourcename, final InputStream in) {

	if (in != null) {
	   final BufferedInputStream buffIn = new BufferedInputStream(in);
	   final byte[] buff = new byte[128];

	   try {
		while (buffIn.read(buff) > 0) {
		   System.out.print("debug print 128 first char from [" + resourcename + "]:");
		   System.out.println(new String(buff));
		   System.out.println();
		   return;
		}
	   } catch (final IOException ioe) {
		ioe.printStackTrace();
	   } finally {
		try {
		   buffIn.close();
		} catch (final IOException ioe) {
		}
	   }

	}
   }

}
