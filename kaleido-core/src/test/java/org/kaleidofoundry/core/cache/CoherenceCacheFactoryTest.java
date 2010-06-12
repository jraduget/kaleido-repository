package org.kaleidofoundry.core.cache;

import org.kaleidofoundry.core.cache.CacheEnum;

/**
 * Test Coherence Cache Factory
 * 
 * @author Jerome RADUGET
 */
public class CoherenceCacheFactoryTest extends AbstractTestCacheFactory {

   @Override
   protected String getAvailableConfiguration() {
	return "cache/coherence-cache-config.xml";
   }

   @Override
   protected CacheEnum getCacheType() {
	return CacheEnum.COHERENCE_3X;
   }

}
