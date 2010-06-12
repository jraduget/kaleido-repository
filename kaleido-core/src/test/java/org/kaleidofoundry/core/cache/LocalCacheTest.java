package org.kaleidofoundry.core.cache;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

/**
 * Test EHCACHE Cache Factory
 * 
 * @author Jerome RADUGET
 */
public class LocalCacheTest extends AbstractTestCache {

   private CacheFactory<Integer, Person> cacheFactory;

   @Before
   public void setup() {
	cacheFactory = CacheFactory.getCacheFactory(CacheEnum.LOCAL);
	cache = cacheFactory.getCache(Person.class.getName(), "cache/kaleido-local.xml");
   }

   @After
   public void destroyAll() throws IOException {
	// print all current method test cache statistics
	LOGGER.info(cacheFactory.printStatistics());
	// destroy all
	cacheFactory.destroyAll();
   }
}
