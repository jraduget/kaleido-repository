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
public class FileStoreIntegrationTest {

   @BeforeClass
   public static void setupClass() throws StoreException {
	// -Dkaleido.configurations=myContext=classpath:/store/myContext.properties
	System.getProperties().put(ConfigurationConstants.JavaEnvProperties, "myContext=classpath:/store/myContext.properties");
	// load given configurations
	ConfigurationFactory.init();
   }

   @AfterClass
   public static void cleanupClass() throws StoreException {
	ConfigurationFactory.destroyAll();
   }

   @Test
   public void testFileStoreSample01() throws StoreException, IOException {
	System.out.println("Test with simple context injection:");
	FileStoreSample01 fileStore = new FileStoreSample01();
	assertNotNull(fileStore);
	assertNotNull(fileStore.echo());
	assertEquals("line1\nline2\n", fileStore.echo());
	assertEquals("line1\nline2\n", fileStore.echo());
   }

   @Test
   public void testFileStoreSample02() throws StoreException, IOException {
	System.out.println("Test with advanced context injection:");
	FileStoreSample02 fileStore = new FileStoreSample02();
	assertNotNull(fileStore);
	assertNotNull(fileStore.echo());
	assertEquals("line1\nline2\n", fileStore.echo());
	assertEquals("line1\nline2\n", fileStore.echo());
   }

   @Test
   public void testFileStoreSample03() throws StoreException, IOException {
	System.out.println("Test with manually context build:");
	FileStoreSample03 fileStore = new FileStoreSample03();
	assertNotNull(fileStore);
	assertNotNull(fileStore.echo());
	assertEquals("line1\nline2\n", fileStore.echo());
	assertEquals("line1\nline2\n", fileStore.echo());
   }
}
