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

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class ResourceStoreFactoryTest extends Assert {

   @Test
   public void newClasspathResourceStore() throws ResourceException {
	URI resourceUri = URI.create("classpath:/org/kaleidofoundry/core/store/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.provides(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(ClasspathResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newFileSystemResourceStore() throws ResourceException {
	URI resourceUri = URI
		.create("file:/F:/Developments/workspaces/eclipse/jappy/kaleido-core/src/test/resources/org/kaleidofoundry/core/store/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.provides(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(FileSystemResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newFtpResourceStore() throws ResourceException {
	URI resourceUri = URI.create("ftp://localhost/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.provides(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(FtpResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newHttpResourceStore() throws ResourceException {
	URI resourceUri = URI.create("http://localhost/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.provides(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(HttpResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newJpaResourceStore() throws ResourceException {
	URI resourceUri = URI.create("jpa:/tmp/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.provides(resourceUri);
	assertNotNull(resourceStore);
	assertTrue(resourceStore.isUriManageable(resourceUri));
	assertEquals(JpaResourceStore.class, resourceStore.getClass());
   }

   // @Rule
   @Test
   public void unManagedUri() {
   }

   @Test
   public void illegalImplementation() {
   }

}
