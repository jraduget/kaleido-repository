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
package org.kaleidofoundry.core.store;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;

/**
 * @author Jerome RADUGET
 */
public class ResourceStoreJunitLauncher {

   @BeforeClass
   public static void setupClass() throws ResourceException {
	// -Dkaleido.configurations=myContext=classpath:/store/myContext.properties
	System.getProperties().put(ConfigurationConstants.JavaEnvProperties, "myContext=classpath:/store/myContext.properties");
	// load given configurations
	ConfigurationFactory.init();
   }

   @AfterClass
   public static void cleanupClass() throws ResourceException {
	ConfigurationFactory.destroyAll();
   }

   @Test
   public void testResourceStoreSample01() throws ResourceException, IOException {
	System.out.println("Test with simple context injection:");
	ResourceStoreSample01 resourceStore = new ResourceStoreSample01();
	assertNotNull(resourceStore);
	assertNotNull(resourceStore.echo());
	assertEquals("line1\nline2\n", resourceStore.echo());
	assertEquals("line1\nline2\n", resourceStore.echo());
   }

   @Test
   public void testResourceStoreSample02() throws ResourceException, IOException {
	System.out.println("Test with advanced context injection:");
	ResourceStoreSample02 resourceStore = new ResourceStoreSample02();
	assertNotNull(resourceStore);
	assertNotNull(resourceStore.echo());
	assertEquals("line1\nline2\n", resourceStore.echo());
	assertEquals("line1\nline2\n", resourceStore.echo());
   }

   @Test
   public void testResourceStoreSample03() throws ResourceException, IOException {
	System.out.println("Test with manually context build:");
	ResourceStoreSample03 resourceStore = new ResourceStoreSample03();
	assertNotNull(resourceStore);
	assertNotNull(resourceStore.echo());
	assertEquals("line1\nline2\n", resourceStore.echo());
	assertEquals("line1\nline2\n", resourceStore.echo());
   }
}
