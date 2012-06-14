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
package org.kaleidofoundry.core.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.kaleidofoundry.core.system.JavaSystemHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO helper tools
 * 
 * @author Jerome RADUGET
 */
public abstract class IOHelper {

   /**
    * default buffer size for reading data
    */
   public static final int DefaultBufferSize = 512;

   private final static Logger LOGGER = LoggerFactory.getLogger(IOHelper.class);

   /**
    * @param reader
    * @return Returns the entire contents of a text reader
    * @throws IOException
    */
   public static StringBuilder getFullText(final Reader reader) throws IOException {
	if (reader == null) { return null; }

	BufferedReader buff = null;

	if (reader instanceof BufferedReader) {
	   buff = (BufferedReader) reader;
	} else {
	   buff = new BufferedReader(reader);
	}

	final StringBuilder str = new StringBuilder();
	String line = null;

	while ((line = buff.readLine()) != null) {
	   str.append(line);
	}

	return str;
   }

   /**
    * @param in
    * @param bufferSize
    * @return byte[] of data input stream, buffered reading
    * @throws IOException
    */
   public static byte[] toByteArray(final InputStream in, final int bufferSize) throws IOException {
	final ByteArrayOutputStream bout = new ByteArrayOutputStream();

	final byte[] data = new byte[bufferSize];
	int dataBuffLenght = 0;
	while ((dataBuffLenght = in.read(data)) > 0) {
	   bout.write(data, 0, dataBuffLenght);
	}

	return bout.toByteArray();
   }

   /**
    * @param in
    * @return byte[] of data input stream, buffered reading is {@link #DefaultBufferSize}
    * @throws IOException
    */
   public static byte[] toByteArray(final InputStream in) throws IOException {
	return toByteArray(in, DefaultBufferSize);
   }

   /**
    * The resource file will be searched as follows:
    * <ul>
    * <li>1. URL connection (without authentification)</li>
    * <li>1. System file</li>
    * <li>2. Java classpath</li>
    * <li>3. Jar classpath</li>
    * </ul>
    * <br/>
    * 
    * @param resourcePath Name of the resource
    * @return inputStream for reading resource (null if no resource found)
    */
   public static InputStream getResourceInputStream(final String resourcePath) {
	return getResourceInputStream(resourcePath, IOHelper.class.getClassLoader());
   }

   /**
    * @param in
    * @return string conversion of the given input stream
    * @throws IOException
    */
   public static String toString(final InputStream in) throws IOException {
	return toString(in, "UTF8");
   }

   /**
    * Convert an {@link InputStream} to {@link String}<br/>
    * It is a helper method, uses it only for small inpustream entry
    * 
    * @param in inputstream
    * @param charset charset to used
    * @return string representation of the inputstream. if input is null, return null
    * @throws IOException
    */
   public static String toString(final InputStream in, final String charset) throws IOException {

	if (in == null) { return null; }

	BufferedReader reader = null;
	StringBuilder str = null;

	try {
	   reader = new BufferedReader(new InputStreamReader(in, charset));
	   String inputLine;

	   while ((inputLine = reader.readLine()) != null) {
		if (str == null) {
		   str = new StringBuilder();
		}
		str.append(inputLine);
		str.append("\n");
	   }

	   return str != null ? str.substring(0, str.length() - 1) : null;

	} finally {
	   if (reader != null) {
		reader.close();
	   }
	}
   }

   /**
    * Helper method to quick iterate on line of a text file<br/>
    * <br/>
    * Simple sample :
    * 
    * <pre>
    * 
    * for (String line : IOHelper.readlines(&quot;/foo.txt&quot;)) {
    *    System.out.println(line);
    * }
    * </pre>
    * 
    * Or better to free io resource :
    * 
    * <pre>
    * try {
    *    IoIterable&lt;String&gt; lines = IOHelper.readlines(&quot;/foo.txt&quot;);
    *    for (String line : IOHelper.readlines(&quot;/foo.txt&quot;)) {
    * 	System.out.println(line);
    *    }
    * } finally {
    *    if (lines != null) {
    * 	lines.close();
    *    }
    * }
    * 
    * </pre>
    * 
    * @param filename
    * @return {@link Iterable} on line of a file content. You have to call {@link IoIterable#close()} when you have finish to use it
    * @throws IOException
    */
   public static IoIterable<String> readlines(final String filename) throws IOException {

	final FileReader fileReader = new FileReader(filename);
	final BufferedReader bufferReader = new BufferedReader(fileReader);

	return new IoIterable<String>() {

	   @Override
	   public Iterator<String> iterator() {
		return new Iterator<String>() {

		   private String currentLine = getNextLine();

		   @Override
		   public boolean hasNext() {
			return currentLine != null;
		   }

		   @Override
		   public String next() {
			String retval = currentLine;
			currentLine = getNextLine();
			return retval;
		   }

		   @Override
		   public void remove() {
			throw new UnsupportedOperationException();
		   }

		   private String getNextLine() {
			String currentLine = null;
			try {
			   currentLine = bufferReader.readLine();
			} catch (IOException ioe) {
			   currentLine = null;
			}
			return currentLine;
		   }
		};
	   }

	   @Override
	   public void close() throws IOException {
		bufferReader.close();
		fileReader.close();
	   }
	};
   }

   /**
    * The resource file will be searched as follows:
    * <ul>
    * <li>1. URL connection (without authentification)</li>
    * <li>1. System file</li>
    * <li>2. Java classpath</li>
    * <li>3. Jar classpath</li>
    * </ul>
    * <br/>
    * 
    * @param resourcePath Name of the resource
    * @param classLoader class loader to use
    * @return inputStream for reading resource (null if no resource found)
    */

   public static InputStream getResourceInputStream(final String resourcePath, final ClassLoader classLoader) {

	// 1. try to get resource from url connection (without authentification)
	try {
	   LOGGER.debug("try getResourceInputStream from url : {}", resourcePath);
	   final URL configUrl = new URL(resourcePath);
	   final URLConnection configConnection = configUrl.openConnection();
	   configConnection.connect();
	   return configConnection.getInputStream();
	} catch (final MalformedURLException e) {
	   LOGGER.debug("malform url : {}", resourcePath);
	} catch (final IOException e) {
	   LOGGER.debug("ioexception from url : {}", resourcePath);
	}

	File fconf = null;
	InputStream fconfIn = null;

	// 2. try to get resource from file system (if full path is given)
	LOGGER.debug("try getResourceInputStream from system file : {}", resourcePath);

	fconf = new File(resourcePath);
	if (!fconf.exists()) {
	   // 3. search file in java classpath
	   LOGGER.debug("try getResourceInputStream from java classpath : {}", resourcePath);
	   fconf = JavaSystemHelper.getResourceAsFile(classLoader, resourcePath);
	}

	if (fconf != null) {
	   try {
		fconfIn = new FileInputStream(fconf);
	   } catch (final FileNotFoundException fne) {
		LOGGER.debug("FileNotFoundException from java classpath : {}", resourcePath);
	   }
	}

	// 4. if still not found, search it in classpath jars
	LOGGER.debug("try getResourceInputStream from java jar classpath: {}", resourcePath);
	if (fconf == null || !fconf.exists()) { return JavaSystemHelper.getResourceAsStream(classLoader, resourcePath); }

	return fconfIn;
   }
}
