package org.kaleidofoundry.core.cache;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import org.kaleidofoundry.core.cache.CacheEnum;
import org.kaleidofoundry.core.cache.CacheFactory;

/**
 * Test Coherence Cache Factory
 * 
 * @author Jerome RADUGET
 */
public class CoherenceCacheTest extends AbstractTestCache {

   private CacheFactory<Integer, Person> cacheFactory;

   @Before
   public void setup() {
	cacheFactory = CacheFactory.getCacheFactory(CacheEnum.COHERENCE_3X);
	cache = cacheFactory.getCache(Person.class.getName(), "cache/coherence-cache-config.xml");;
   }
   
   @After
   public void destroyAll() throws IOException {
	// print all current method test cache statistics	
	LOGGER.info(cacheFactory.printStatistics());
	// destroy all
	cacheFactory.destroyAll();
   }


}
