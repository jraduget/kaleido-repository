/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.cache;

import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Test Kaleido local cache
 * 
 * @author Jerome RADUGET
 */

public class LocalCacheManagerTest extends AbstractTestCacheManager {

   @Override
   protected String getAvailableConfiguration() {
	return "classpath:/noneed";
   }

   @Override
   protected String getCacheImplementationCode() {
	return DefaultCacheProviderEnum.local.name();
   }

   @Override
   protected RuntimeContext<CacheManager> getCacheManagerContext() {
	return new RuntimeContext<CacheManager>(CacheManager.class);
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
