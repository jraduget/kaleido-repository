/*  
 * Copyright 2008-2021 the original author or authors 
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

import java.net.URISyntaxException;

import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ResourceException;

import static org.junit.Assert.*;

/**
 * @author jraduget
 */
public class JavaSystemConfigurationTest extends AbstractConfigurationTest {

   @Override
   protected Configuration newInstance() throws ResourceException, URISyntaxException {

	// WARN : do not clear System.getProperties() to init test...
	// -> it is possible but very unpredictable... I prefer to override default assertion

	// add system properties
	System.getProperties().setProperty("application.name", "app");
	System.getProperties().setProperty("application.description", "description of the application...");
	System.getProperties().setProperty("application.date", "2006-09-01T00:00:00");
	System.getProperties().setProperty("application.version", "1.0.0");
	System.getProperties().setProperty("application.librairies", "dom4j.jar|log4j.jar|mail.jar");
	System.getProperties().setProperty("application.modules.sales.name", "Sales");
	System.getProperties().setProperty("application.modules.sales.version", "1.1.0");
	System.getProperties().setProperty("application.modules.marketing.name", "Market.");
	System.getProperties().setProperty("application.modules.netbusiness.name", "");
	System.getProperties().setProperty("application.array.bigdecimal", "987.5|1.123456789");
	System.getProperties().setProperty("application.array.boolean", "false|true");
	System.getProperties().setProperty("application.single.boolean", "true");
	System.getProperties().setProperty("application.single.bigdecimal", "1.123456789");
	System.getProperties().setProperty("application.array.date", "2009-01-02T00:00:00|2009-12-31T00:00:00|2012-05-15T00:00:00");

	return new JavaSystemConfiguration("javaSystemConfig", new RuntimeContext<Configuration>(Configuration.class));
   }

   @Override
   public void store() throws ResourceException {
	assertNotNull(configuration);
	try {
	   configuration.store();
	   fail();
	} catch (ConfigurationException ce) {
	   assertEquals("config.readonly.store", ce.getCode());
	}
   }

   @Test
   public void isStorable() {
	assertNotNull(configuration);
	assertFalse(configuration.isStorable());
   }

   @Override
   @Test
   public void keySet() {
	assertNotNull(configuration);
	assertNotNull(configuration.keySet());
	assertTrue(configuration.keySet().size() >= 14);
   }

   @Override
   @Test
   public void roots() {
	assertNotNull(configuration);
	assertNotNull(configuration.roots());
	assertTrue(configuration.roots().size() >= 1);
   }

   @Override
   @Test
   public void toPropertiesCount() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	assertTrue(configuration.toProperties().stringPropertyNames().size() >= 14);
   }
}
