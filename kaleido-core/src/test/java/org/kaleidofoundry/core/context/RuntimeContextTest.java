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
package org.kaleidofoundry.core.context;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreConstants;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
@Task(comment = "test parameters builder priority to configuration")
public class RuntimeContextTest extends Assert {

   private static final String TomcatContextName = "tomcat";
   private static final String ContextPrefix = "jndi.context";

   private Configuration configuration;

   @Before
   public void setup() throws IOException, StoreException {
	// runtimeContext create with Configuration
	configuration = ConfigurationFactory.provides("contextTest", "classpath:/context/context.properties");

   }

   @After
   public void cleanup() throws StoreException {
	if (configuration != null) {
	   ConfigurationFactory.unregister("contextTest");
	}
   }

   // context without name, prefix and empty datas
   // ***************************************************************************

   @Test
   public void createEmptyContext() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>();
	assertNull(runtimeContext.getName());
	assertNull(runtimeContext.getPrefix());
   }

   @Test
   public void createEmptyContextFromConfiguration() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(configuration);
	assertNull(runtimeContext.getName());
	assertNull(runtimeContext.getPrefix());
	assertNotNull(runtimeContext.getConfigurations());

	simpleSampleAssertions(runtimeContext, null, null);
   }

   // context with name but without prefix and datas *******************************************************************

   @Test
   public void createNamedContext() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNull(runtimeContext.getPrefix());
   }

   @Test
   public void createNamedContextFromConfiguration() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, configuration);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNull(runtimeContext.getPrefix());
	assertNotNull(runtimeContext.getConfigurations());

	simpleSampleAssertions(runtimeContext, TomcatContextName, null);
   }

   // context with name but without prefix and datas *******************************************************************
   @Test
   public void createNamedContextWithPrefix() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, ContextPrefix);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNotNull(runtimeContext.getPrefix());
	assertEquals(ContextPrefix, runtimeContext.getPrefix());
   }

   @Test
   public void createNamedContextWithPlugin() {
	final RuntimeContext<FileStore> runtimeContext = new RuntimeContext<FileStore>(TomcatContextName, FileStore.class);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNotNull(runtimeContext.getPrefix());
	assertEquals(FileStoreConstants.FileStorePluginName, runtimeContext.getPrefix());
   }

   @Test
   public void createNamedContextWithPrefixFromConfiguration() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, ContextPrefix, configuration);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNotNull(runtimeContext.getPrefix());
	assertEquals(ContextPrefix, runtimeContext.getPrefix());
	assertNotNull(runtimeContext.getConfigurations());

	simpleSampleAssertions(runtimeContext, TomcatContextName, ContextPrefix);
   }

   @Test
   public void echo() {
	RuntimeContext<?> runtimeContext;

	runtimeContext = new RuntimeContext<Object>(configuration);
	System.out.println(runtimeContext.toString("\n"));

	runtimeContext = new RuntimeContext<Object>(ContextPrefix, configuration);
	System.out.println(runtimeContext.toString("\n"));

	runtimeContext = new RuntimeContext<Object>("tomcat", ContextPrefix, configuration);
	System.out.println(runtimeContext.toString("\n"));
   }

   /**
    * common assertion for a sample the context.properties test file <br/>
    * 
    * @param runtimeContext
    */
   void simpleSampleAssertions(final RuntimeContext<?> runtimeContext, final String name, final String prefix) {

	if (TomcatContextName.equals(name) && ContextPrefix.equals(prefix)) {
	   assertNotNull(runtimeContext);
	   assertNotNull(runtimeContext.keySet());
	   assertEquals(5, runtimeContext.keySet().size());

	   /*
	    * jndi.context.tomcat.java.naming.env.prefix=java:comp/env
	    * jndi.context.tomcat.java.naming.provider.url=
	    * jndi.context.tomcat.java.naming.factory.initial=org.apache.naming.java.javaURLContextFactory
	    * jndi.context.tomcat.java.naming.factory.url.pkgs=
	    * jndi.context.tomcat.java.naming.dns.url=
	    */

	   // assertions on keys
	   assertTrue(runtimeContext.keySet().contains("java.naming.env.prefix"));
	   assertTrue(runtimeContext.keySet().contains("java.naming.provider.url"));
	   assertTrue(runtimeContext.keySet().contains("java.naming.factory.initial"));
	   assertTrue(runtimeContext.keySet().contains("java.naming.factory.url.pkgs"));
	   assertTrue(runtimeContext.keySet().contains("java.naming.dns.url"));

	   // assertions on keys values
	   assertEquals("java:comp/env", runtimeContext.getProperty("java.naming.env.prefix"));
	   assertEquals("", runtimeContext.getProperty("java.naming.provider.url"));
	   assertEquals("org.apache.naming.java.javaURLContextFactory", runtimeContext.getProperty("java.naming.factory.initial"));
	   assertEquals("", runtimeContext.getProperty("java.naming.factory.url.pkgs"));
	   assertEquals("", runtimeContext.getProperty("java.naming.dns.url"));

	}

   }

}
