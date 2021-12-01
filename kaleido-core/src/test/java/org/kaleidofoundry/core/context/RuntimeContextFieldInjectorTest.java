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
package org.kaleidofoundry.core.context;

import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.service_with_no_provider.MySingleService;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author jraduget
 */
public class RuntimeContextFieldInjectorTest  {

   @Before
   public void setup() throws IOException, ResourceException {
	// register & load given configuration
	ConfigurationFactory.provides("contextTest", "classpath:/context/context.properties");
   }

   @After
   public void cleanup() throws ResourceException {
	ConfigurationFactory.unregister("contextTest");
   }

   @Test
   public void testUnnamedRuntimeContextInjection() {
	final MySingleService service = new MySingleService();
	assertNotNull(service.getDefaultNamingService());
	assertEquals("defaultNamingService", service.getDefaultNamingService().getName());
	assertNull(service.getDefaultNamingService().getPrefix());
	assertEquals("localhost:123", service.getDefaultNamingService().getProperty("providerUrl"));
	assertEquals("java:comp/env", service.getDefaultNamingService().getProperty("envPrefix"));
   }

   @Test
   public void testUnnamedTypedRuntimeContextInjection() {
	final MySingleService service = new MySingleService();
	assertNotNull(service.getDefaultTypedNamingService());
	assertEquals("defaultTypedNamingService", service.getDefaultTypedNamingService().getName());
	assertNull(service.getDefaultTypedNamingService().getPrefix());
	assertEquals("localhost:1234", service.getDefaultTypedNamingService().getProperty("providerUrl"));
	assertEquals("java:comp/env", service.getDefaultTypedNamingService().getProperty("envPrefix"));
   }

   @Test
   public void testUntypedRuntimeContextInjection() {
	final MySingleService service = new MySingleService();
	assertNotNull(service.getUnTypedRuntimeContext());
	assertEquals("namingServices.jboss", service.getUnTypedRuntimeContext().getName());
	assertNull(service.getUnTypedRuntimeContext().getPrefix());
	assertEquals("localhost:1099", service.getUnTypedRuntimeContext().getProperty("providerUrl"));
	assertEquals("org.jnp.interfaces.NamingContextFactory", service.getUnTypedRuntimeContext().getProperty("factoryInitialClass"));
	assertEquals("org.jboss.naming:org.jnp.interfaces", service.getUnTypedRuntimeContext().getProperty("factoryUrlPkgs"));
   }

   @Test
   public void testTypedRuntimeContextInjection() {
	final MySingleService service = new MySingleService();
	assertNotNull(service.getTypedRuntimeContext());
	assertEquals("namingServices.jboss", service.getUnTypedRuntimeContext().getName());
	assertNull(service.getUnTypedRuntimeContext().getPrefix());
	assertEquals("localhost:1099", service.getUnTypedRuntimeContext().getProperty("providerUrl"));
	assertEquals("org.jnp.interfaces.NamingContextFactory", service.getUnTypedRuntimeContext().getProperty("factoryInitialClass"));
	assertEquals("org.jboss.naming:org.jnp.interfaces", service.getUnTypedRuntimeContext().getProperty("factoryUrlPkgs"));
   }

}
