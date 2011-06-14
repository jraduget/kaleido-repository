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
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.service_with_no_provider.MySingleService;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class RuntimeContextFieldInjectorTest extends Assert {

   @Before
   public void setup() throws IOException, StoreException {
	// register & load given configuration
	ConfigurationFactory.provides("contextTest", "classpath:/context/context.properties");
   }

   @After
   public void cleanup() throws StoreException {
	ConfigurationFactory.unregister("contextTest");
   }

   @Test
   public void testUntypedRuntimeContextInjection() {
	final MySingleService service = new MySingleService();
	assertNotNull(service.getUnTypedRuntimeContext());
	assertEquals("jndi.context.jboss", service.getUnTypedRuntimeContext().getName());
	assertNull(service.getUnTypedRuntimeContext().getPrefix());
	assertEquals("localhost:1099", service.getUnTypedRuntimeContext().getProperty("java.naming.provider.url"));
	assertEquals("org.jnp.interfaces.NamingContextFactory", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.initial"));
	assertEquals("org.jboss.naming:org.jnp.interfaces", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.url.pkgs"));
   }

   @Test
   public void testTypedRuntimeContextInjection() {
	final MySingleService service = new MySingleService();
	assertNotNull(service.getTypedRuntimeContext());
	assertEquals("jndi.context.jboss", service.getUnTypedRuntimeContext().getName());
	assertNull(service.getUnTypedRuntimeContext().getPrefix());
	assertEquals("localhost:1099", service.getUnTypedRuntimeContext().getProperty("java.naming.provider.url"));
	assertEquals("org.jnp.interfaces.NamingContextFactory", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.initial"));
	assertEquals("org.jboss.naming:org.jnp.interfaces", service.getUnTypedRuntimeContext().getProperty("java.naming.factory.url.pkgs"));
   }

}
