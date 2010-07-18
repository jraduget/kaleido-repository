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

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Test Jboss Ininispan (c) Cache Manager
 * 
 * @author Jerome RADUGET
 */
public class InfinispanCacheTest extends AbstractTestCache {

   private CacheManager cacheManager;

   @Before
   public void setup() {
	cacheManager = CacheFactory.getCacheManager(DefaultCacheProviderEnum.infinispan4x.name(), "cache/infinispan-local.xml",
		new RuntimeContext<CacheManager>());
	cache = cacheManager.getCache(Person.class.getName());
   }

   @After
   public void destroyAll() throws IOException {
	if (cacheManager != null) {
	   // print all current method test cache statistics
	   LOGGER.info(cacheManager.printStatistics());
	   // destroy all
	   cacheManager.destroyAll();
	}
   }

}
