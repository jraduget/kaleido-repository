/*
 *  Copyright 2008-2012 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.store;

import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.kaleidofoundry.core.context.RuntimeContext;

import com.google.appengine.tools.development.testing.LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * https://developers.google.com/appengine/docs/java/tools/localunittesting
 * 
 * @author Jerome RADUGET
 */
@Ignore
public class GsFileStoreTest extends AbstractFileStoreTest {

   private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalFileServiceTestConfig());

   @Before
   public void setUp() throws ResourceException, UnsupportedEncodingException {
	helper.setUp();
	
	final RuntimeContext<FileStore> context = new FileStoreContextBuilder().withBaseUri("gs:/kaleido/store/test").build();
	fileStore = new GSFileStore(context);

	existingResources.put("path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	existingResources.put("/path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add("gs:/path/foo");
	nonExistingResources.add("path/foo");
	
	for (Entry<String, String> entryToTest : existingResources.entrySet()) {
	   fileStore.store(entryToTest.getKey(), entryToTest.getValue());	   
	}
   }

   @After
   public void tearDown() {
	helper.tearDown();
   }

}
