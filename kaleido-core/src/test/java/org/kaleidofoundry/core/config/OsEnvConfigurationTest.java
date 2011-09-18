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

import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class OsEnvConfigurationTest extends Assert {

   private Configuration configuration;

   @Before
   public void setup() throws ResourceException {
	I18nMessagesFactory.disableJpaControl();
	configuration = new OsEnvConfiguration("osEnv", new RuntimeContext<Configuration>(Configuration.class));
	configuration.load();
   }

   @After
   public void cleanup() {
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void isLoaded() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void load() throws ResourceException, URISyntaxException, ResourceException {
	final Configuration configuration = new OsEnvConfiguration("osEnv", new RuntimeContext<Configuration>(Configuration.class));
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	configuration.load();
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void unload() throws ResourceException, URISyntaxException {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	configuration.unload();
	assertTrue(!configuration.isLoaded());
   }

   @Test
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
   public void getProperty() {
	assertNotNull(configuration);
	assertNotNull(configuration.getString("PATH"));
	assertFalse(configuration.getString("PATH").isEmpty());
	// test unknown key
	assertNull(configuration.getProperty("foo"));
   }
}
