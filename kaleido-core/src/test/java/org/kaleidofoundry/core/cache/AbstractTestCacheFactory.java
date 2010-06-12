package org.kaleidofoundry.core.cache;

import java.text.DateFormat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

/**
 * Test Cache Factory
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractTestCacheFactory extends Assert {

   /** @return available legal configuration file */
   protected abstract String getAvailableConfiguration();

   /** Cache type to test */
   protected abstract CacheEnum getCacheType();

   // ** inits & clean
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

   /**
    * clean all after each test case
    */
   @After
   public void destroyAll() {
	CacheFactory.getCacheFactory(getCacheType()).destroyAll();
   }

   // ** tests *********************************************************************************************************

   @Test
   public void legalConfiguration() {
	_testCacheFactory(getAvailableConfiguration());
   }

   @Test(expected = CacheConfigurationException.class)
   public void illegalConfiguration() {
	try {
	   _testCacheFactory("cache/illegal-configuration.txt");
	} catch (final CacheConfigurationNotFoundException cnfe) {
	   fail("except CacheConfigurationException not CacheConfigurationNotFoundException");
	}
   }

   @Test(expected = CacheConfigurationException.class)
   public void invalidConfiguration() {
	try {
	   _testCacheFactory("cache/invalid-configuration.xml");
	} catch (final CacheConfigurationNotFoundException cnfe) {
	   fail("except CacheConfigurationException not CacheConfigurationNotFoundException");
	}
   }

   @Test(expected = CacheConfigurationNotFoundException.class)
   public void configurationNotFoundException() {
	_testCacheFactory("cache/unknown.xml");
   }

   @Ignore
   @Test(expected = CacheDefinitionNotFoundException.class)
   public void cacheDefinitionNotFoundException() {
	final CacheFactory<Integer, DateFormat> cacheFactory = CacheFactory.getCacheFactory(getCacheType());
	cacheFactory.getCache(DateFormat.class, getAvailableConfiguration());
   }

   @Test
   public void defaultConfiguration() {
	_testCacheFactory(getAvailableConfiguration());
   }

   /**
    * @param configuration
    */
   void _testCacheFactory(final String configuration) {
	CacheFactory<Integer, Person> cacheFactory = null;
	cacheFactory = CacheFactory.getCacheFactory(getCacheType());
	assertNotNull(cacheFactory);

	Cache<Integer, Person> cache;
	if (configuration == null) {
	   cache = cacheFactory.getCache(Person.class);
	} else {
	   cache = cacheFactory.getCache(Person.class, configuration);
	}

	assertNotNull(cache);
	assertEquals(cache.getName(), Person.class.getName());
   }

}
