package org.kaleidofoundry.core.cache;

import org.kaleidofoundry.core.cache.CacheEnum;

/**
 * Test Jboss Cache Factory
 * 
 * @author Jerome RADUGET
 */

public class InfinispanFactoryTest extends AbstractTestCacheFactory {

   @Override
   protected String getAvailableConfiguration() {
	return "cache/infinispan-local.xml";
   }

   @Override
   protected CacheEnum getCacheType() {
	return CacheEnum.INFINISPAN_4X;
   }
}
