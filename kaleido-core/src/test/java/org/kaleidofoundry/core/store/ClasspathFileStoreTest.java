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

import java.io.ByteArrayInputStream;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author Jerome RADUGET
 */
public class ClasspathFileStoreTest extends AbstractFileStoreTest {

   @Before
   @Override
   public void setup() throws Throwable {

	super.setup();

	final RuntimeContext<FileStore> context = new FileStoreContextBuilder().withBaseUri("classpath:/").build();
	fileStore = new ClasspathFileStore(context);

	existingResources.put("store/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	existingResources.put("/store/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add("classpath:/store/foo");
	nonExistingResources.add("store/foo");

   }

   @Test
   @Override
   public void store() throws ResourceException {
	try {
	   fileStore.store("store/toStore.txt", new ByteArrayInputStream("foo".getBytes()));
	   fail();
	} catch (final ResourceException rse) {
	   assertEquals("store.readonly.illegal", rse.getCode());
	}
   }

   @Test
   @Override
   public void move() throws ResourceException {
	try {
	   fileStore.move("store/foo.txt", "newstore/foo.txt");
	   fail();
	} catch (final ResourceException rse) {
	   assertEquals("store.readonly.illegal", rse.getCode());
	}
   }

   @Test
   @Override
   public void remove() throws ResourceException {
	try {
	   fileStore.remove("store/toRemove.txt");
	   fail();
	} catch (final ResourceException rse) {
	   assertEquals("store.readonly.illegal", rse.getCode());
	}
   }

}
