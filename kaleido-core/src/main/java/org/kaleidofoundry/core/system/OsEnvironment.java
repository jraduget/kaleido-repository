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
package org.kaleidofoundry.core.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import org.kaleidofoundry.core.lang.annotation.Tested;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Allows access to environment variables of the OS<br/>
 * <br/>
 * Use <code>(new OsEnvironment()).getProperty("JAVA_HOME") ....</code> <br/>
 * <p>
 * This class extends Properties (which is thread safe)<br/>
 * Other methods which can modify state of the property (not read only use) throws IllegalAccessError... <br/>
 * </p>
 * <p>
 * Class instance use internal cache, so env. variable are loading via static way, at first access. <br/>
 * But you can reload reload internal cache using reload() method <br/>
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Tested
@ThreadSafe
public class OsEnvironment extends Properties {

   private static final long serialVersionUID = 1125394943855L;

   private boolean loading;

   /**
    * @throws IOException
    */
   public OsEnvironment() throws IOException {
	init();
   }

   /**
    * @throws IOException
    */
   protected synchronized void init() throws IOException {

	loading = true;

	String cmd = "env";
	final String os = System.getProperty("os.name").toLowerCase();
	if (os.indexOf("windows") != -1) {
	   cmd = (os.indexOf("95") != -1 || os.indexOf("98") != -1 || os.indexOf("ME") != -1 ? "command.com" : "cmd") + " /Cset";
	}

	final Process p = Runtime.getRuntime().exec(cmd);
	BufferedReader br = null;

	try {
	   br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	   for (String line = ""; (line = br.readLine()) != null;) {
		if (!StringHelper.isEmpty(line.trim())) {
		   setProperty(line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1));
		}
	   }
	} finally {
	   if (br != null) {
		br.close();
	   }
	   loading = false;
	}

   }

   /**
    * reload internal cache
    * 
    * @throws IOException
    */
   public void reload() throws IOException {
	clear();
	init();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#getProperty(java.lang.String)
    */
   @Override
   public synchronized String getProperty(final String key) {
	final String envVarName = key.replaceAll("[.]", "_").toUpperCase();
	return super.getProperty(envVarName);
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#load(java.io.InputStream)
    */
   @Override
   public synchronized void load(final InputStream inStream) throws IOException {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#load(java.io.Reader)
    */
   @Override
   public synchronized void load(final Reader reader) throws IOException {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#loadFromXML(java.io.InputStream)
    */
   @Override
   public synchronized void loadFromXML(final InputStream in) throws IOException, InvalidPropertiesFormatException {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
    */
   @Override
   public synchronized Object setProperty(final String key, final String value) {
	if (loading) {
	   return super.setProperty(key, value);
	} else {
	   throw new IllegalAccessError();
	}
   }

   /*
    * (non-Javadoc)
    * @see java.util.Hashtable#clear()
    */
   @Override
   public synchronized void clear() {
	if (loading) {
	   super.clear();
	} else {
	   throw new IllegalAccessError();
	}
   }

   /*
    * (non-Javadoc)
    * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
    */
   @Override
   public synchronized Object put(final Object key, final Object value) {
	if (loading) {
	   return super.put(key, value);
	} else {
	   throw new IllegalAccessError();
	}
   }

   /*
    * (non-Javadoc)
    * @see java.util.Hashtable#putAll(java.util.Map)
    */
   @Override
   public synchronized void putAll(final Map<? extends Object, ? extends Object> t) {
	if (loading) {
	   super.putAll(t);
	} else {
	   throw new IllegalAccessError();
	}
   }

   /*
    * (non-Javadoc)
    * @see java.util.Hashtable#remove(java.lang.Object)
    */
   @Override
   public synchronized Object remove(final Object key) {
	if (loading) {
	   return super.remove(key);
	} else {
	   throw new IllegalAccessError();
	}
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#save(java.io.OutputStream, java.lang.String)
    */
   @Override
   public synchronized void save(final OutputStream out, final String comments) {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#store(java.io.OutputStream, java.lang.String)
    */
   @Override
   public void store(final OutputStream out, final String comments) throws IOException {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#store(java.io.Writer, java.lang.String)
    */
   @Override
   public void store(final Writer writer, final String comments) throws IOException {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#storeToXML(java.io.OutputStream, java.lang.String, java.lang.String)
    */
   @Override
   public synchronized void storeToXML(final OutputStream os, final String comment, final String encoding) throws IOException {
	throw new IllegalAccessError();
   }

   /*
    * (non-Javadoc)
    * @see java.util.Properties#storeToXML(java.io.OutputStream, java.lang.String)
    */
   @Override
   public synchronized void storeToXML(final OutputStream os, final String comment) throws IOException {
	throw new IllegalAccessError();
   }

   @Override
   public synchronized boolean equals(final Object o) {
	return super.equals(o);
   }

   @Override
   public synchronized int hashCode() {
	return super.hashCode();
   }

}
