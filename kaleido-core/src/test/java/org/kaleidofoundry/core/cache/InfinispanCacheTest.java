package org.kaleidofoundry.core.cache;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import org.kaleidofoundry.core.cache.CacheEnum;
import org.kaleidofoundry.core.cache.CacheFactory;

/**
 * Test Jboss Cache Factory
 * 
 * @author Jerome RADUGET
 */
public class InfinispanCacheTest extends AbstractTestCache {

   private CacheFactory<Integer, Person> cacheFactory;

   @Before
   public void setup() {
	cacheFactory = CacheFactory.getCacheFactory(CacheEnum.INFINISPAN_4X);
	cache = cacheFactory.getCache(Person.class.getName(), "cache/infinispan-local.xml");
   }
   
   
   @After
   public void destroyAll() throws IOException {
	// print all current method test cache statistics	
	LOGGER.info(cacheFactory.printStatistics());
	// destroy all
	cacheFactory.destroyAll();
   }   

}
