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
import org.kaleidofoundry.core.context.test.MySingleService;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class InjectSimpleRuntimeContextTest extends Assert {

   private Configuration configuration;

   @Before
   public void setup() throws IOException, StoreException {

	// runtimeContext create with Configuration
	configuration = ConfigurationFactory.provideConfiguration("contextTest", "classpath:/org/kaleidofoundry/core/context/context.properties",
		new RuntimeContext<Configuration>());

   }

   @After
   public void cleanup() throws StoreException {
	if (configuration != null) {
	   configuration.unload();
	}
   }

   @Test
   public void testUntypedRuntimeContextInjection() {
	MySingleService service = new MySingleService();
	assertNotNull(service.getUnTypedRuntimeContext());
	assertEquals("jndi.context.jboss", service.getUnTypedRuntimeContext().getName());
	assertNull(service.getUnTypedRuntimeContext().getPrefixProperty());
	assertEquals("localhost:1099", service.getUnTypedRuntimeContext().getProperty("java.naming.provider.url"));
	assertEquals("org.jnp.interfaces.NamingContextFactory", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.initial"));
	assertEquals("org.jboss.naming:org.jnp.interfaces", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.url.pkgs"));
   }

   @Test
   public void testTypedRuntimeContextInjection() {
	MySingleService service = new MySingleService();
	assertNotNull(service.getTypedRuntimeContext());
	assertEquals("jndi.context.jboss", service.getUnTypedRuntimeContext().getName());
	assertNull(service.getUnTypedRuntimeContext().getPrefixProperty());
	assertEquals("localhost:1099", service.getUnTypedRuntimeContext().getProperty("java.naming.provider.url"));
	assertEquals("org.jnp.interfaces.NamingContextFactory", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.initial"));
	assertEquals("org.jboss.naming:org.jnp.interfaces", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.url.pkgs"));
   }

}
