package org.kaleidofoundry.core.cache;

import junit.framework.Assert;

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
public class CacheFactoryDefaultTest extends Assert {

   @Test
   public void defaultCacheFactoryImplementation() {

	final CacheFactory<Integer, Person> cacheFactory = CacheFactory.getCacheFactory();
	assertNotNull(cacheFactory);
	assertTrue(cacheFactory instanceof LocalCacheFactoryImpl<?, ?>);

	final Cache<Integer, Person> cache = cacheFactory.getCache(Person.class, "cache/kaleido-local.xml");
	assertTrue(cache instanceof LocalCacheImpl<?, ?>);

   }

}
