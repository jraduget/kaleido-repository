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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.lang.NotNullException;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractFileStoreTest extends Assert {

   /** mock resource default content */
   static final String DEFAULT_RESOURCE_MOCK_TEST = "line1\nline2";

   // protected property to instantiate by the concrete implementation class
   protected FileStore fileStore;

   // valid uri resource to test
   protected Map<String, String> existingResources = new LinkedHashMap<String, String>();
   // inavlid uri resource that must failed at load time
   protected Set<String> nonExistingResources = new LinkedHashSet<String>();
   // valid uri resource to store
   protected Map<String, String> existingResourcesForStore = new LinkedHashMap<String, String>();
   // valid uri resource to remove
   protected Map<String, String> existingResourcesForRemove = new LinkedHashMap<String, String>();
   // valid uri resource to move
   protected Map<String, String> existingResourcesForMove = new LinkedHashMap<String, String>();

   @Before
   public void setup() throws Throwable {
   }

   @After
   public void cleanup() throws Throwable {
   }

   @Test
   public void checkStore() {
	assertNotNull(fileStore);
	assertNotNull(existingResources);
	assertNotNull(nonExistingResources);
   }

   @Test
   public void get() throws ResourceException, IOException {

	ResourceHandler resource = null;

	for (final String uriToTest : existingResources.keySet()) {
	   try {
		resource = fileStore.get(uriToTest);
		assertNotNull(resource);
		assertNotNull(resource.getInputStream());
		assertEquals(existingResources.get(uriToTest), resource.getText());
	   } finally {
		if (resource != null) {
		   resource.close();
		}
	   }
	}
   }

   @Test
   public void loadNotFound() throws ResourceException {

	assertNotNull(fileStore);

	// null argument not allowed
	try {
	   fileStore.get(null);
	   fail("NotNullException expected");
	} catch (final NotNullException nae) {
	}

	// load not existing resource, throws ResourceNotFoundException
	for (final String uriToTest : nonExistingResources) {
	   try {
		fileStore.get(uriToTest);
		fail("uri <" + uriToTest + "> must throws ResourceNotFoundException");
	   } catch (final ResourceNotFoundException rnfe) {
	   }
	}
   }

   @Test
   public void exists() throws ResourceException {
	for (final String uriToTest : existingResources.keySet()) {
	   assertTrue(fileStore.exists(uriToTest));
	}
   }

   @Test
   public void notExists() throws ResourceException {

	assertNotNull(fileStore);

	// null argument not allowed
	try {
	   fileStore.exists(null);
	   fail("NotNullException expected");
	} catch (final NotNullException nae) {
	}
	// test a non existing resource, throws ResourceNotFoundException
	for (final String uriToTest : nonExistingResources) {
	   assertFalse(fileStore.exists(uriToTest));
	}
   }

   @Test
   public void store() throws ResourceException, UnsupportedEncodingException {

	assertNotNull(fileStore);
	assertTrue("there is no resource entry to store in the test", existingResourcesForStore.size() > 0);

	// null argument not allowed
	try {
	   fileStore.store(null);
	   fail("NotNullException expected");
	} catch (final NotNullException nae) {
	}

	// for each resources to store
	for (final String uriToTest : existingResourcesForStore.keySet()) {

	   // get initial content
	   try {
		fileStore.get(uriToTest);
		fail("resource '" + uriToTest + "' already exists");
	   } catch (final ResourceNotFoundException rnfe) {
	   }

	   // store the resource
	   final String resourceToStoreAsText = existingResourcesForStore.get(uriToTest);
	   assertNotNull(resourceToStoreAsText);
	   fileStore.store(uriToTest, resourceToStoreAsText);

	   // get the stored resource
	   final ResourceHandler resourceToGet = fileStore.get(uriToTest);
	   assertNotNull(resourceToGet);
	   assertEquals(resourceToStoreAsText, resourceToGet.getText());
	}
   }

   @Test
   public void move() throws ResourceException {

	assertNotNull(fileStore);
	assertTrue("there is no resource entry to move in the test", existingResourcesForMove.size() > 0);

	// null argument not allowed
	try {
	   fileStore.move(null, null);
	   fail("NotNullException expected");
	} catch (final NotNullException nae) {
	}

	// for each resources to remove
	for (final String uriToTest : existingResourcesForMove.keySet()) {

	   // get resource
	   ResourceHandler resource = null;
	   try {
		resource = fileStore.get(uriToTest);
	   } catch (final ResourceNotFoundException rnfe) {
		fail("resource '" + rnfe.getMessage() + "' does not exists");
	   } finally {
		if (resource != null) {
		   resource.close();
		}
	   }

	   // move the resource
	   final String newResourcePath = existingResourcesForMove.get(uriToTest);
	   fileStore.move(uriToTest, newResourcePath);

	   // get the original resource
	   try {
		resource = fileStore.get(uriToTest);
		fail("move resource failed '" + uriToTest + "' . The resource still exists");
	   } catch (final ResourceNotFoundException rnfe) {
	   } finally {
		if (resource != null) {
		   resource.close();
		}
	   }

	   // get the new resource
	   try {
		resource = fileStore.get(newResourcePath);
	   } catch (final ResourceNotFoundException rnfe) {
		fail("move resource failed '" + newResourcePath + "' does not exists");
	   } finally {
		if (resource != null) {
		   resource.close();
		}
	   }

	   // remove the move resource
	   fileStore.remove(newResourcePath);

	}

   }

   @Test
   public void remove() throws ResourceException {
	assertNotNull(fileStore);
	assertTrue("there is no resource entry to remove in the test", existingResourcesForStore.size() > 0);

	// null argument not allowed
	try {
	   fileStore.remove(null);
	   fail("NotNullException expected");
	} catch (final NotNullException nae) {
	}

	// for each resources to remove
	for (final String uriToTest : existingResourcesForRemove.keySet()) {

	   // get resource
	   try {
		fileStore.get(uriToTest);
	   } catch (final ResourceNotFoundException rnfe) {
		fail("resource '" + rnfe.getMessage() + "' does not exists");
	   }

	   // remove the resource
	   fileStore.remove(uriToTest);

	   // get remove resource
	   try {
		fileStore.get(uriToTest);
		fail("remove failed, resource '" + uriToTest + "' already exists");
	   } catch (final ResourceNotFoundException rnfe) {
	   }
	}
   }
}
