package org.kaleidofoundry.core.cache;

import org.kaleidofoundry.core.cache.CacheEnum;

/**
 * Test Jboss Cache Factory
 * 
 * @author Jerome RADUGET
 */

public class JbossFactoryTest extends AbstractTestCacheFactory {

   @Override
   protected String getAvailableConfiguration() {
	return "cache/jboss-local.xml";
   }

   @Override
   protected CacheEnum getCacheType() {
	return CacheEnum.JBOSS_3X;
   }
}
