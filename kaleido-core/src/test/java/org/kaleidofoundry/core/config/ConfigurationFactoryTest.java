package org.kaleidofoundry.core.config;

import junit.framework.Assert;

import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class ConfigurationFactoryTest extends Assert {

   @Test
   public void providePropertiesConfiguration() throws StoreException {

	String configId = "propCpConfig";
	String configResourceUri = "classpath:/org/kaleidofoundry/core/config/test.properties";
	Configuration config = ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>());

	assertNotNull(config);
	assertEquals(PropertiesConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>()));
	assertTrue(config.isLoaded());
   }

   @Test
   public void provideXmlPropertiesConfiguration() throws StoreException {

	String configId = "propXmlCpConfig";
	String configResourceUri = "classpath:/org/kaleidofoundry/core/config/test.properties.xml";
	Configuration config = ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>());

	assertNotNull(config);
	assertEquals(XmlPropertiesConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>()));
	assertTrue(config.isLoaded());
   }

   @Test
   public void provideXmlConfiguration() throws StoreException {

	String configId = "xmlCpConfig";
	String configResourceUri = "classpath:/org/kaleidofoundry/core/config/test.xml";
	Configuration config = ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>());

	assertNotNull(config);
	assertEquals(XmlConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>()));
	assertTrue(config.isLoaded());
   }

   @Test
   public void provideJavaSystemConfiguration() throws StoreException {

	System.getProperties().setProperty("application.name", "app");
	System.getProperties().setProperty("application.description", "description of the application...");
	System.getProperties().setProperty("application.date", "2006-09-01T00:00:00");
	System.getProperties().setProperty("application.version", "1.0.0");
	System.getProperties().setProperty("application.librairies", "dom4j.jar log4j.jar mail.jar");
	System.getProperties().setProperty("application.modules.sales.name", "Sales");
	System.getProperties().setProperty("application.modules.sales.version", "1.1.0");
	System.getProperties().setProperty("application.modules.marketing.name", "Market.");
	System.getProperties().setProperty("application.modules.netbusiness.name", "");
	System.getProperties().setProperty("application.array.bigdecimal", "987.5 1.123456789");
	System.getProperties().setProperty("application.array.boolean", "false true");
	System.getProperties().setProperty("application.single.boolean", "true");
	System.getProperties().setProperty("application.single.bigdecimal", "1.123456789");
	System.getProperties().setProperty("application.array.date", "2009-01-02T00:00:00 2009-12-31T00:00:00 2012-05-15T00:00:00");

	String configId = "javaSystemConfig";
	String configResourceUri = "memory:/test.javasystem";
	Configuration config = ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>());

	assertNotNull(config);
	assertEquals(JavaSystemConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provideConfiguration(configId, configResourceUri, new RuntimeContext<Configuration>()));
	assertTrue(config.isLoaded());
   }
}
