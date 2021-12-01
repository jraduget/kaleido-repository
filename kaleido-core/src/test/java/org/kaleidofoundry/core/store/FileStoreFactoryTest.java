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
package org.kaleidofoundry.core.store;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author jraduget
 */
public class FileStoreFactoryTest  {

   @Test
   public void newClasspathFileStore() throws ResourceException {
	final FileStore fileStore = FileStoreFactory.provides("classpath:/");
	assertNotNull(fileStore);
	assertEquals(ClasspathFileStore.class, fileStore.getClass());
	assertTrue(fileStore.isUriManageable("classpath:/store/foo.txt"));
   }

   @Test
   public void newFileSystemFileStore() throws ResourceException {
	final FileStore fileStore = FileStoreFactory.provides("file:/dev/workspace/kaleidofoundry");
	assertNotNull(fileStore);
	assertEquals(FileSystemStore.class, fileStore.getClass());
	assertTrue(fileStore.isUriManageable("file:/dev/workspace/kaleidofoundry/kaleido-core/src/test/resources/store/foo.txt"));
   }

   @Test
   public void newFtpFileStore() throws ResourceException {
	final FileStore fileStore = FileStoreFactory.provides("ftp://localhost/");
	assertNotNull(fileStore);
	assertEquals(FtpStore.class, fileStore.getClass());
	assertTrue(fileStore.isUriManageable("ftp://localhost/foo.txt"));
   }

   @Test
   public void newHttpFileStore() throws ResourceException {
	final FileStore fileStore = FileStoreFactory.provides("http://localhost/");
	assertNotNull(fileStore);
	assertEquals(HttpFileStore.class, fileStore.getClass());
	assertTrue(fileStore.isUriManageable("http://localhost/foo.txt"));
   }

   @Test
   public void newJpaFileStore() throws ResourceException {
	final FileStore fileStore = FileStoreFactory.provides("jpa:/");
	assertNotNull(fileStore);
	assertTrue(fileStore.isUriManageable("jpa:/tmp/foo.txt"));
	assertEquals(JpaFileStore.class, fileStore.getClass());
   }
}
