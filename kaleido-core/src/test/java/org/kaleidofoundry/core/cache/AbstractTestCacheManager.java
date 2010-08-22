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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

/**
 * Test Cache Factory
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractTestCacheManager extends Assert {

   /** @return cache implementation code */
   protected abstract String getCacheImplementationCode();

   /** @return available legal configuration file */
   protected abstract String getAvailableConfiguration();

   /** Cache type to test */
   protected abstract RuntimeContext<CacheManager> getCacheManagerContext();

   private CacheManager cacheManager;

   // ** init & clean
   // **************************************************************************************************

   /**
    * disable i18n message bundle control to speed up test (no need of a local derby instance startup)
    */
   @BeforeClass
   public static void setupClass() {
	I18nMessagesFactory.disableJpaControl();
   }

   /**
    * re-enable i18n message bundle control
    */
   @AfterClass
   public static void cleanupClass() {
	I18nMessagesFactory.enableJpaControl();
   }

   @Before
   public void setup() {
	cacheManager = CacheFactory.provides(getCacheImplementationCode(), getAvailableConfiguration(), getCacheManagerContext());
   }

   /**
    * clean all after each test case
    */
   @After
   public void destroyAll() {
	if (cacheManager != null) {
	   cacheManager.destroyAll();
	}
   }

   // ** tests *********************************************************************************************************

   @Test
   public void defaultConfiguration() {
	_testCacheFactory(CacheFactory.provides(getCacheImplementationCode(), null, getCacheManagerContext()));
   }

   @Test
   public void legalConfiguration() {
	_testCacheFactory(cacheManager);
   }

   @Test(expected = CacheConfigurationException.class)
   public void illegalConfiguration() {
	try {
	   CacheFactory.provides(getCacheImplementationCode(), "classpath:/cache/illegal-configuration.txt", getCacheManagerContext());
	} catch (final CacheConfigurationNotFoundException cnfe) {
	   fail("except CacheConfigurationException not CacheConfigurationNotFoundException");
	}
   }

   @Test(expected = CacheConfigurationException.class)
   public void invalidConfiguration() {
	try {
	   CacheFactory.provides(getCacheImplementationCode(), "classpath:/cache/invalid-configuration.xml", getCacheManagerContext());
	} catch (final CacheConfigurationNotFoundException cnfe) {
	   fail("except CacheConfigurationException not CacheConfigurationNotFoundException");
	}
   }

   @Test(expected = CacheConfigurationNotFoundException.class)
   public void configurationNotFoundException() {
	CacheFactory.provides(getCacheImplementationCode(), "classpath:/cache/unknown.xml", getCacheManagerContext());
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

	final Cache<Integer, Person> cache = cacheManager.getCache(Person.class);

	assertNotNull(cache);
	assertEquals(cache.getName(), Person.class.getName());
	assertSame(cache, cacheManager.getCache(Person.class));

	cacheManager.destroy(Person.class.getName());
	assertTrue(cache.hasBeenDestroy());
	assertNotSame(cache, cacheManager);
   }
}
