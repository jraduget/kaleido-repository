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
public class JbossCacheTest extends AbstractTestCache {

   private CacheFactory<Integer, Person> cacheFactory;

   @Before
   public void setup() {
	cacheFactory = CacheFactory.getCacheFactory(CacheEnum.JBOSS_3X);	
	cache = cacheFactory.getCache(Person.class.getName(), "cache/jboss-local.xml");
   }
   
   
   @After
   public void destroyAll() throws IOException {
	// print all current method test cache statistics	
	LOGGER.info(cacheFactory.printStatistics());
	// destroy all
	cacheFactory.destroyAll();
   }
}
