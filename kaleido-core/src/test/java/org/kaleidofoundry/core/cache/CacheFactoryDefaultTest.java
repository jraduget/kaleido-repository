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
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

/**
 * Testing getting cacheFactory and cache with java env. variable : <br/>
 * <ul>
 * <li>-Dcache.provider=local</li>
 * <li>-Dcache.provider=ehCache2x</li>
 * <li>-Dcache.provider=jbossCache3x</li>
 * <li>-Dcache.provider=coherence3x</li>
 * <li>-Dcache.provider=infinispan4x</li>
 * <li>-Dcache.provider=gigaspace7x</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class CacheFactoryDefaultTest extends Assert {

   @Before
   public void setup() {
	I18nMessagesFactory.disableJpaControl();
   }

   @After
   public void cleanup() {
	CacheManagerProvider.init(null);
	I18nMessagesFactory.enableJpaControl();
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
	testCacheFactory(CacheProvidersEnum.infinispan4x, Infinispan4xCacheManagerImpl.class, Infinispan4xCacheImpl.class);
   }

   @Test(expected = CacheDefinitionNotFoundException.class)
   public void ehCacheFactoryImplementation() {
	testCacheFactory(CacheProvidersEnum.ehCache2x, EhCache2xManagerImpl.class, EhCache2xImpl.class);
   }

   @Test
   public void jbossFactoryImplementation() {
	testCacheFactory(CacheProvidersEnum.jbossCache3x, Jboss32xCacheManagerImpl.class, Jboss32xCacheImpl.class);
   }

   @SuppressWarnings("rawtypes")
   void testCacheFactory(final CacheProvidersEnum provider, final Class<? extends CacheManager> cacheManagerClass, final Class<? extends Cache> cacheClass) {

	CacheManager cacheFactory = null;
	CacheManagerProvider.init(provider != null ? provider.name() : null);

	try {
	   cacheFactory = CacheManagerFactory.provides();
	   assertNotNull(cacheFactory);
	   assertEquals(cacheManagerClass.getName(), cacheFactory.getClass().getName());

	   final Cache<Integer, Person> cache = cacheFactory.getCache(Person.class);
	   assertEquals(cacheClass.getName(), cache.getClass().getName());
	} finally {
	   if (cacheFactory != null) {
		cacheFactory.destroyAll();
	   }
	}
   }
}
