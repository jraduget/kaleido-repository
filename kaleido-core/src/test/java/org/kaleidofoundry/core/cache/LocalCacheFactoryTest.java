package org.kaleidofoundry.core.cache;

import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.cache.CacheEnum;

/**
 * Test EHCACHE Cache Factory
 * 
 * @author Jerome RADUGET
 */

public class LocalCacheFactoryTest extends AbstractTestCacheFactory {

   @Override
   protected String getAvailableConfiguration() {
	return "cache/kaleido-local.xml";
   }

   @Override
   protected CacheEnum getCacheType() {
	return CacheEnum.LOCAL;
   }

   @Ignore
   @Test
   @Override
   public void cacheDefinitionNotFoundException() {
	super.cacheDefinitionNotFoundException();
   }

   @Ignore
   @Test
   @Override
   public void configurationNotFoundException() {
	super.configurationNotFoundException();
   }

   @Ignore
   @Test
   @Override
   public void illegalConfiguration() {
	super.illegalConfiguration();
   }

   @Ignore
   @Test
   @Override
   public void invalidConfiguration() {
	super.invalidConfiguration();
   }

}
