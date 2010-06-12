/*
 * $License$
 */
package org.kaleidofoundry.core.context;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.lang.NotNullException;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class RuntimeContextTest extends Assert {

   private static final String TomcatContextName = "tomcat";
   private static final String ContextPrefix = "jndi.context";

   private Properties properties;
   private Configuration configuration;

   @Before
   public void setup() throws IOException, StoreException {

	// runtimeContext create with Configuration
	configuration = ConfigurationFactory.provideConfiguration("contextTest", "classpath:/org/kaleidofoundry/core/context/context.properties",
		new RuntimeContext<Configuration>());

	// runtimeContext create with Properties
	properties = new Properties();
	properties.load(getClass().getClassLoader().getResourceAsStream("org/kaleidofoundry/core/context/context.properties"));
   }

   @After
   public void cleanup() throws StoreException {
	configuration.unload();
	properties.clear();
   }

   // context without name, prefix and empty datas
   // ***************************************************************************

   @Test
   public void createEmptyContext() {
	RuntimeContext<?> runtimeContext = new RuntimeContext<Object>();
	assertNull(runtimeContext.getName());
	assertNull(runtimeContext.getPrefixProperty());
	assertNull(runtimeContext.getConfiguration());
	assertNotNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
   }

   @Test
   public void createEmptyContextFromProperties() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(properties);
	assertNull(runtimeContext.getName());
	assertNull(runtimeContext.getPrefixProperty());
	assertNull(runtimeContext.getConfiguration());
	assertNotNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
	simpleSampleAssertions(runtimeContext, null, null);
   }

   @Test
   public void createEmptyContextFromConfiguration() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(configuration);
	assertNull(runtimeContext.getName());
	assertNull(runtimeContext.getPrefixProperty());
	assertNotNull(runtimeContext.getConfiguration());
	assertNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
	simpleSampleAssertions(runtimeContext, null, null);
   }

   // context with name but without prefix and datas *******************************************************************

   @Test
   public void createNamedContext() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNull(runtimeContext.getPrefixProperty());
	assertNull(runtimeContext.getConfiguration());
	assertNotNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
   }

   @Test
   public void createNamedContextFromProperties() {
	final RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, properties);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNull(runtimeContext.getPrefixProperty());
	assertNull(runtimeContext.getConfiguration());
	assertNotNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
	simpleSampleAssertions(runtimeContext, TomcatContextName, null);
   }

   @Test
   public void createNamedContextFromConfiguration() {
	RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, configuration);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNull(runtimeContext.getPrefixProperty());
	assertNotNull(runtimeContext.getConfiguration());
	assertNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
	simpleSampleAssertions(runtimeContext, TomcatContextName, null);
   }

   // context with name but without prefix and datas *******************************************************************
   @Test
   public void createNamedContextWithPrefix() {
	RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, ContextPrefix);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNotNull(runtimeContext.getPrefixProperty());
	assertEquals(ContextPrefix, runtimeContext.getPrefixProperty());
	assertNull(runtimeContext.getConfiguration());
	assertNotNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
   }

   @Test
   public void createNamedContextWithPrefixFromProperties() {
	RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, properties, ContextPrefix);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNotNull(runtimeContext.getPrefixProperty());
	assertEquals(ContextPrefix, runtimeContext.getPrefixProperty());

	assertNull(runtimeContext.getConfiguration());
	assertNotNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
	simpleSampleAssertions(runtimeContext, TomcatContextName, ContextPrefix);
   }

   @Test
   public void createNamedContextWithPrefixFromConfiguration() {
	RuntimeContext<?> runtimeContext = new RuntimeContext<Object>(TomcatContextName, configuration, ContextPrefix);
	assertNotNull(runtimeContext.getName());
	assertEquals(TomcatContextName, runtimeContext.getName());
	assertNotNull(runtimeContext.getPrefixProperty());
	assertEquals(ContextPrefix, runtimeContext.getPrefixProperty());

	assertNotNull(runtimeContext.getConfiguration());
	assertNull(runtimeContext.getProperties());

	simpleContextAssertions(runtimeContext);
	simpleSampleAssertions(runtimeContext, TomcatContextName, ContextPrefix);
   }

   @Test
   public void echo() {
	RuntimeContext<?> runtimeContext;

	runtimeContext = new RuntimeContext<Object>(properties);
	System.out.println(runtimeContext.toString("\n"));

	runtimeContext = new RuntimeContext<Object>(properties, ContextPrefix);
	System.out.println(runtimeContext.toString("\n"));

	runtimeContext = new RuntimeContext<Object>("tomcat", properties, ContextPrefix);
	System.out.println(runtimeContext.toString("\n"));
   }

   /**
    * simple common assertion for a given {@link RuntimeContext} <br/>
    * assertion does not reference context.properties datas
    * 
    * @param runtimeContext
    */
   void simpleContextAssertions(final RuntimeContext<?> runtimeContext) {

	// set a property with a String
	runtimeContext.setProperty("foo", "value");
	assertEquals("value", runtimeContext.getProperty("foo"));

	// set null property not allow
	try {
	   runtimeContext.setProperty(null, "foo value");
	   fail();
	} catch (NullPointerException npe) {
	} catch (NotNullException npae) {
	}

	// set null value not allow
	try {
	   runtimeContext.setProperty("foonull", null);
	   fail();
	} catch (NullPointerException npe) {
	} catch (NotNullException npae) {
	}

	// remove a property
	runtimeContext.removeProperty("foo");

	// remove null property not allow
	try {
	   runtimeContext.removeProperty(null);
	   fail();
	} catch (NullPointerException npe) {
	} catch (NotNullException npae) {
	}

	assertNull(runtimeContext.getProperty("foo"));

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
