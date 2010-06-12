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

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractResourceStoreTest extends Assert {

   /** mock resource default content */
   static final String DEFAULT_RESOURCE_MOCK_TEST = "line1\nline2";

   // protected property to instanciate by the concrete implementation class
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
   public void load() throws StoreException, IOException {

	ResourceHandler resource = null;

	for (URI uriToTest : existingResources.keySet()) {
	   try {
		resource = resourceStore.load(uriToTest);
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
   public void loadNotFound() throws StoreException {

	// null argument not allowed
	try {
	   resourceStore.load(null);
	   fail("NullArgumentException expected");
	} catch (NotNullException nae) {
	}

	// load not existing resource, throws ResourceNotFoundException
	for (URI uriToTest : nonExistingResources) {
	   try {
		resourceStore.load(uriToTest);
		fail("uri <" + uriToTest + "> must throws ResourceNotFoundException");
	   } catch (ResourceNotFoundException rnfe) {
	   }
	}
   }

   @Test
   public void exists() throws StoreException {
	for (URI uriToTest : existingResources.keySet()) {
	   assertTrue(resourceStore.exists(uriToTest));
	}
   }

   @Test
   public void notExists() throws StoreException {
	// null argument not allowed
	try {
	   resourceStore.exists(null);
	   fail("NullArgumentException expected");
	} catch (NotNullException nae) {
	}
	// test a non existing resource, throws ResourceNotFoundException
	for (URI uriToTest : nonExistingResources) {
	   assertFalse(resourceStore.exists(uriToTest));
	}
   }

   @Test
   @Ignore
   @NotYetImplemented
   public void store() throws StoreException {
	// TODO store implementation test
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   @Test
   @Ignore
   @NotYetImplemented
   public void move() throws StoreException {
	// TODO move implementation test
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   @Test
   @Ignore
   @NotYetImplemented
   public void remove() throws StoreException {
	// TODO remove implementation test
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }
}
