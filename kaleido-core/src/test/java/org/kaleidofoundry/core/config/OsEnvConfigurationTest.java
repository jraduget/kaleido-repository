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

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class OsEnvConfigurationTest extends Assert {

   private Configuration configuration;

   @Before
   public void setup() throws StoreException {
	configuration = new OsEnvConfiguration("osEnv", new RuntimeContext<Configuration>());
	configuration.load();
   }

   @Test
   public void isLoaded() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void load() throws StoreException, URISyntaxException, StoreException {
	Configuration configuration = new OsEnvConfiguration("osEnv", new RuntimeContext<Configuration>());
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	configuration.load();
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void unload() throws StoreException, URISyntaxException {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	configuration.unload();
	assertTrue(!configuration.isLoaded());
   }

   @Test(expected = IllegalStateException.class)
   public void store() throws StoreException {
	assertNotNull(configuration);
	configuration.store();
   }

   @Test
   public void getProperty() {
	assertNotNull(configuration);
	assertNotNull(configuration.getString("OS"));
	assertFalse(configuration.getString("OS").isEmpty());
	// test unknown key
	assertNull(configuration.getRawProperty("foo"));
   }
}
