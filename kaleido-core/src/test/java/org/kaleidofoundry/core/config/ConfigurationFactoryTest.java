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
package org.kaleidofoundry.core.config;

import junit.framework.Assert;

import org.junit.Test;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class ConfigurationFactoryTest extends Assert {

   @Test
   public void providePropertiesConfiguration() throws ResourceException {

	final String configId = "propCpConfig";
	final String configResourceUri = "classpath:/config/test.properties";
	final Configuration config = ConfigurationFactory.provides(configId, configResourceUri);

	assertNotNull(config);
	assertEquals(PropertiesConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provides(configId, configResourceUri));
	assertTrue(config.isLoaded());
   }

   @Test
   public void provideXmlPropertiesConfiguration() throws ResourceException {

	final String configId = "propXmlCpConfig";
	final String configResourceUri = "classpath:/config/test.properties.xml";
	final Configuration config = ConfigurationFactory.provides(configId, configResourceUri);

	assertNotNull(config);
	assertEquals(XmlPropertiesConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provides(configId, configResourceUri));
	assertTrue(config.isLoaded());
   }

   @Test
   public void provideXmlConfiguration() throws ResourceException {

	final String configId = "xmlCpConfig";
	final String configResourceUri = "classpath:/config/test.xml";
	final Configuration config = ConfigurationFactory.provides(configId, configResourceUri);

	assertNotNull(config);
	assertEquals(XmlConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provides(configId, configResourceUri));
	assertTrue(config.isLoaded());
   }

   @Test
   public void provideJavaSystemConfiguration() throws ResourceException {

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

	final String configId = "javaSystemConfig";
	final String configResourceUri = "memory:/test.javasystem";
	final Configuration config = ConfigurationFactory.provides(configId, configResourceUri);

	assertNotNull(config);
	assertEquals(JavaSystemConfiguration.class, config.getClass());
	assertSame(config, ConfigurationFactory.provides(configId, configResourceUri));
	assertTrue(config.isLoaded());
   }
}
