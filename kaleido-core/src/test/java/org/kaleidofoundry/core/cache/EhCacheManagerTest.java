/*  
 * Copyright 2008-2016 the original author or authors 
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

import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Test ehCache (c) Cache Manager
 * 
 * @author jraduget
 */

public class EhCacheManagerTest extends AbstractCacheManagerTest {

   @Override
   protected String getAvailableConfiguration() {
	return "classpath:/cache/ehcache-local.xml";
   }

   @Override
   protected String getCacheImplementationCode() {
	return CacheProvidersEnum.ehCache.name();
   }

   @Override
   protected RuntimeContext<CacheManager> getCacheManagerContext() {
	return new RuntimeContext<CacheManager>("ehCacheManager", CacheManager.class);
   }

   @Test(expected = CacheDefinitionNotFoundException.class)
   @Override
   public void defaultConfiguration() {
	super.defaultConfiguration();
   }

}
