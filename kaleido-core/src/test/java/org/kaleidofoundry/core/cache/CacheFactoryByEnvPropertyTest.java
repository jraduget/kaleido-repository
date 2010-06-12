package org.kaleidofoundry.core.cache;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Testing getting cacheFactory and cache with java env variable : <br/>
 * <ul>
 * <li>-Dcache.implementation=local</li>
 * <li>-Dcache.implementation=ehcache-1.x</li>
 * <li>-Dcache.implementation=jboss-cache-3.x</li>
 * <li>-Dcache.implementation=coherence-3.x</li>
 * <li>-Dcache.implementation=infinispan-4.x</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
// !! Only use it and de-comment it, for local testing !! sides effect...
public class CacheFactoryByEnvPropertyTest extends Assert {

   @Before
   public void setup() {
	// System.setProperty(CacheConstants.CACHE_IMPLEMENTATION_ENV, CacheEnum.JBOSS_3X.getCode());
   }

   @Test
   public void askCustomCacheFactoryByEnvProperty() {

	// final CacheFactory<Integer, Person> cacheFactory = CacheFactory.getCacheFactory();
	// assertNotNull(cacheFactory);
	// assertTrue(cacheFactory instanceof Jboss32xCacheFactoryImpl<?, ?>);
	//
	// final Cache<Integer, Person> cache = cacheFactory.getCache(Person.class, "cache/jboss-local.xml");
	// assertTrue(cache instanceof Jboss32xCacheImpl<?, ?>);
	//
	// cache.put(Person.newMockInstance().getId(), Person.newMockInstance());
	// assertNotNull(cache.get(Person.newMockInstance().getId()));
   }

}
