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
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.io.IOHelper;
import org.kaleidofoundry.core.lang.NotNullException;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractResourceStoreTest extends Assert {

   /** mock resource default content */
   static final String DEFAULT_RESOURCE_MOCK_TEST = "line1\nline2";

   // protected property to instantiate by the concrete implementation class
   protected ResourceStore resourceStore;

   // valid uri resource to test
   protected Map<URI, String> existingResources = new LinkedHashMap<URI, String>();;
   // inavlid uri resource that must failed at load time
   protected Set<URI> nonExistingResources = new LinkedHashSet<URI>();

   /**
    * disable i18n message bundle control to speed up test (no need of a local derby instance startup)
    * 
    * @throws Throwable
    */
   @Before
   public void setup() throws Throwable {
	I18nMessagesFactory.disableJpaControl();
   }

   /**
    * re-enable i18n message bundle control
    * 
    * @throws Throwable
    */
   @After
   public void cleanup() throws Throwable {
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void checkStore() {
	assertNotNull(resourceStore);
	assertNotNull(existingResources);
	assertNotNull(nonExistingResources);
   }

   @Test
   public void get() throws ResourceException, IOException {

	ResourceHandler resource = null;

	for (final URI uriToTest : existingResources.keySet()) {
	   try {
		resource = resourceStore.get(uriToTest);
		assertNotNull(resource);
		assertNotNull(resource.getInputStream());
		assertEquals(existingResources.get(uriToTest), IOHelper.toString(resource.getInputStream()));
	   } finally {
		if (resource != null) {
		   resource.release();
		}
	   }
	}
   }

   @Test
   public void loadNotFound() throws ResourceException {

	assertNotNull(resourceStore);

	// null argument not allowed
	try {
	   resourceStore.get(null);
	   fail("NullArgumentException expected");
	} catch (final NotNullException nae) {
	}

	// load not existing resource, throws ResourceNotFoundException
	for (final URI uriToTest : nonExistingResources) {
	   try {
		resourceStore.get(uriToTest);
		fail("uri <" + uriToTest + "> must throws ResourceNotFoundException");
	   } catch (final ResourceNotFoundException rnfe) {
	   }
	}
   }

   @Test
   public void exists() throws ResourceException {
	for (final URI uriToTest : existingResources.keySet()) {
	   assertTrue(resourceStore.exists(uriToTest));
	}
   }

   @Test
   public void notExists() throws ResourceException {

	assertNotNull(resourceStore);

	// null argument not allowed
	try {
	   resourceStore.exists(null);
	   fail("NullArgumentException expected");
	} catch (final NotNullException nae) {
	}
	// test a non existing resource, throws ResourceNotFoundException
	for (final URI uriToTest : nonExistingResources) {
	   assertFalse(resourceStore.exists(uriToTest));
	}
   }

   @Test
   @Ignore
   @NotYetImplemented
   @Review(comment = "store implementation test", category = ReviewCategoryEnum.ImplementIt)
   public void store() throws ResourceException {
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   @Test
   @Ignore
   @NotYetImplemented
   @Review(comment = "move implementation test", category = ReviewCategoryEnum.ImplementIt)
   public void move() throws ResourceException {
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   @Test
   @Ignore
   @NotYetImplemented
   @Review(comment = "remove implementation test", category = ReviewCategoryEnum.ImplementIt)
   public void remove() throws ResourceException {
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }
}
