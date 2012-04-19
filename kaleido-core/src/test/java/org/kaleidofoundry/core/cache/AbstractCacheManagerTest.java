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
package org.kaleidofoundry.core.cache;

import java.text.DateFormat;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Test Cache Factory
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractCacheManagerTest extends Assert {

   /** @return cache implementation code */
   protected abstract String getCacheImplementationCode();

   /** @return available legal configuration file */
   protected abstract String getAvailableConfiguration();

   /** Cache type to test */
   protected abstract RuntimeContext<CacheManager> getCacheManagerContext();

   private CacheManager cacheManager;

   private int cacheManagerCountBeforeCreation = -1;

   // ** init & clean
   // **************************************************************************************************

   @Before
   public void setup() {
	cacheManagerCountBeforeCreation = CacheManagerFactory.getRegistry().size();
	cacheManager = CacheManagerFactory.provides(getCacheImplementationCode(), getAvailableConfiguration(), getCacheManagerContext());
   }

   /**
    * clean all after each test case
    */
   @After
   public void destroyAll() {

	if (cacheManager != null) {
	   cacheManager.destroyAll();
	}
	if (cacheManagerCountBeforeCreation >= 0) {
	   assertEquals("Leak detected on cache manager destroyAll", cacheManagerCountBeforeCreation, CacheManagerFactory.getRegistry().size());
	}
   }

   // ** tests *********************************************************************************************************

   @Test
   public void defaultConfiguration() {

	CacheManager defaultCacheManager = null;

	try {
	   defaultCacheManager = CacheManagerFactory.provides(getCacheImplementationCode());

	   _testCacheFactory(defaultCacheManager);

	   // same instance have to be provided the second / third / ... times
	   assertSame(defaultCacheManager, CacheManagerFactory.provides(getCacheImplementationCode()));
	   assertSame(defaultCacheManager, CacheManagerFactory.provides(getCacheImplementationCode()));
	   assertSame(defaultCacheManager, CacheManagerFactory.provides(getCacheImplementationCode()));

	   // not same instance as default instance
	   assertNotSame(defaultCacheManager, cacheManager);

	} finally {
	   if (defaultCacheManager != null) {
		defaultCacheManager.destroyAll();
	   }
	}
   }

   @Test
   public void legalConfiguration() {

	_testCacheFactory(cacheManager);

	// same instance have to be provided the second / third / ... times
	assertSame(cacheManager, CacheManagerFactory.provides(getCacheImplementationCode(), getAvailableConfiguration(), getCacheManagerContext()));
	assertSame(cacheManager, CacheManagerFactory.provides(getCacheImplementationCode(), getAvailableConfiguration(), getCacheManagerContext()));
	assertSame(cacheManager, CacheManagerFactory.provides(getCacheImplementationCode(), getAvailableConfiguration(), getCacheManagerContext()));
   }

   @Test(expected = CacheConfigurationException.class)
   public void illegalConfiguration() {
	try {
	   CacheManagerFactory.provides(getCacheImplementationCode(), "classpath:/cache/illegal-configuration.txt");
	} catch (final CacheConfigurationNotFoundException cnfe) {
	   fail("except CacheConfigurationException not CacheConfigurationNotFoundException");
	}
   }

   @Test(expected = CacheConfigurationException.class)
   public void invalidConfiguration() {
	try {
	   CacheManagerFactory.provides(getCacheImplementationCode(), "classpath:/cache/invalid-configuration.xml");
	} catch (final CacheConfigurationNotFoundException cnfe) {
	   fail("except CacheConfigurationException not CacheConfigurationNotFoundException");
	}
   }

   @Test(expected = CacheConfigurationNotFoundException.class)
   public void configurationNotFoundException() {
	CacheManagerFactory.provides(getCacheImplementationCode(), "classpath:/cache/unknown.xml");
   }

   @Ignore
   @Test(expected = CacheDefinitionNotFoundException.class)
   public void cacheDefinitionNotFoundException() {
	cacheManager.getCache(DateFormat.class);
   }

   /**
    * @param cacheManager
    */
   protected static void _testCacheFactory(final CacheManager cacheManager) {
	assertNotNull(cacheManager);

	// some cache basic test
	final Cache<Integer, Person> cache = cacheManager.getCache(Person.class);

	assertNotNull(cache);
	assertEquals(cache.getName(), Person.class.getName());
	assertSame(cache, cacheManager.getCache(Person.class));

	cacheManager.destroy(Person.class.getName());
	assertTrue(cache.hasBeenDestroy());
	assertNotSame(cache, cacheManager);
   }
}
