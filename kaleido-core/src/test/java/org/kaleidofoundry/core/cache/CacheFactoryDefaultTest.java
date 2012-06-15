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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

/**
 * Testing getting cacheFactory and cache with java env. variable : <br/>
 * <ul>
 * <li>-Dkaleido.cacheprovider=local</li>
 * <li>-Dkaleido.cacheprovider=ehCache</li>
 * <li>-Dkaleido.cacheprovider=jbossCache3x</li>
 * <li>-Dkaleido.cacheprovider=coherence3x</li>
 * <li>-Dkaleido.cacheprovider=infinispan</li>
 * <li>-Dkaleido.cacheprovider=gigaspace7x</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class CacheFactoryDefaultTest extends Assert {

   @After
   public void cleanup() {
	// reset default to the cache provider
	CacheManagerProvider.init(null);
   }

   @Test
   public void defaultCacheFactoryImplementation() {
	testCacheFactory(null, LocalCacheManagerImpl.class, LocalCacheImpl.class);
   }

   @Test
   public void localCacheFactoryImplementation() {
	testCacheFactory(CacheProvidersEnum.local, LocalCacheManagerImpl.class, LocalCacheImpl.class);
   }

   @Test
   public void infinispanFactoryImplementation() {
	testCacheFactory(CacheProvidersEnum.infinispan, InfinispanCacheManagerImpl.class, InfinispanCacheImpl.class);
   }

   @Test(expected = CacheDefinitionNotFoundException.class)
   public void ehCacheFactoryImplementation() {
	testCacheFactory(CacheProvidersEnum.ehCache, EhCacheManagerImpl.class, EhCacheImpl.class);
   }

   @Test
   public void jbossFactoryImplementation() {
	testCacheFactory(CacheProvidersEnum.jbossCache3x, Jboss3xCacheManagerImpl.class, Jboss3xCacheImpl.class);
   }

   @SuppressWarnings("rawtypes")
   void testCacheFactory(final CacheProvidersEnum provider, final Class<? extends CacheManager> cacheManagerClass, final Class<? extends Cache> cacheClass) {

	CacheManager cacheManager = null;
	CacheManagerProvider.init(provider != null ? provider.name() : null);

	try {
	   cacheManager = CacheManagerFactory.provides();
	   assertNotNull(cacheManager);
	   assertEquals(cacheManagerClass.getName(), cacheManager.getClass().getName());

	   final Cache<Integer, Person> cache = cacheManager.getCache(Person.class);
	   assertEquals(cacheClass.getName(), cache.getClass().getName());
	} finally {
	   if (cacheManager != null) {
		cacheManager.destroyAll();
	   }
	}
   }
}
