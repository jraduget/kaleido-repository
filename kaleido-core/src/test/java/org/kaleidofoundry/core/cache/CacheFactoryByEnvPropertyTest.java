/*
 * Copyright 2008-2014 the original author or authors
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

import org.junit.Before;
import org.junit.Test;

/**
 * Testing getting cacheFactory and cache with java env variable : <br/>
 * <ul>
 * <li>-Dkaleido.cacheprovider=local</li>
 * <li>-Dkaleido.cacheprovider=ehCache</li>
 * <li>-Dkaleido.cacheprovider=jbossCache3x</li>
 * <li>-Dkaleido.cacheprovider=coherence3x</li>
 * <li>-Dkaleido.cacheprovider=infinispan</li>
 * </ul>
 * 
 * @author jraduget
 */
// !! Only use it and de-comment it, for local testing !! sides effect...
public class CacheFactoryByEnvPropertyTest extends Assert {

   @Before
   public void setup() {
	// System.setProperty(CacheConstants.CACHE_IMPLEMENTATION_ENV, CacheEnum.JBOSS_3X.getCode());
   }

   @Test
   public void askCustomCacheFactoryByEnvProperty() {

	// final CacheFactory<Integer, Person> cacheFactory = CacheFactory.getCacheFactory();
	// assertNotNull(cacheFactory);
	// assertTrue(cacheFactory instanceof Jboss32xCacheFactoryImpl<?, ?>);
	//
	// final Cache<Integer, Person> cache = cacheFactory.getCache(Person.class, "classpath:/cache/jboss-local.xml");
	// assertTrue(cache instanceof Jboss32xCacheImpl<?, ?>);
	//
	// cache.put(Person.newMockInstance().getId(), Person.newMockInstance());
	// assertNotNull(cache.get(Person.newMockInstance().getId()));
   }

}
