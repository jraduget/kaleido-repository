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

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class ResourceStoreFactoryTest extends Assert {

   @Test
   public void newClasspathResourceStore() throws ResourceException {
	final ResourceStore resourceStore = ResourceStoreFactory.provides("classpath:/");
	assertNotNull(resourceStore);
	assertEquals(ClasspathResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable("classpath:/store/foo.txt"));
   }

   @Test
   public void newFileSystemResourceStore() throws ResourceException {
	final ResourceStore resourceStore = ResourceStoreFactory.provides("file:/dev/workspace/kaleidofoundry");
	assertNotNull(resourceStore);
	assertEquals(FileSystemResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable("file:/dev/workspace/kaleidofoundry/kaleido-core/src/test/resources/store/foo.txt"));
   }

   @Test
   public void newFtpResourceStore() throws ResourceException {
	final ResourceStore resourceStore = ResourceStoreFactory.provides("ftp://localhost/");
	assertNotNull(resourceStore);
	assertEquals(FtpResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable("ftp://localhost/foo.txt"));
   }

   @Test
   public void newHttpResourceStore() throws ResourceException {
	final ResourceStore resourceStore = ResourceStoreFactory.provides("http://localhost/");
	assertNotNull(resourceStore);
	assertEquals(HttpResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable("http://localhost/foo.txt"));
   }

   @Test
   public void newJpaResourceStore() throws ResourceException {
	final ResourceStore resourceStore = ResourceStoreFactory.provides("jpa:/");
	assertNotNull(resourceStore);
	assertTrue(resourceStore.isUriManageable("jpa:/tmp/foo.txt"));
	assertEquals(JpaResourceStore.class, resourceStore.getClass());
   }
}
