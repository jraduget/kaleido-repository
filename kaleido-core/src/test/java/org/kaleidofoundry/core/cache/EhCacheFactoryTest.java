package org.kaleidofoundry.core.cache;

import org.kaleidofoundry.core.cache.CacheEnum;

/**
 * Test EHCACHE Cache Factory
 * 
 * @author Jerome RADUGET
 */

public class EhCacheFactoryTest extends AbstractTestCacheFactory {

   @Override
   protected String getAvailableConfiguration() {
	return "cache/ehcache-local.xml";
   }

   @Override
   protected CacheEnum getCacheType() {
	return CacheEnum.EHCACHE_1X;
   }

}
