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

import java.util.Set;

/**
 * Simple use case for caching for a java bean (distributed or not, depends on cache configuration)
 * 
 * @author Jerome RADUGET
 */
public class SimplePersonService {

   protected final transient Cache<Integer, Person> personCache;

   // protected final transient Cache<Person> personCache = CacheFactory.getCacheFactory().getCache(Person.class);

   public SimplePersonService() {

	// Get factory cache implementation
	// *********************************
	// EhCache implementation by default, to choose implementation use java env variable like :
	// -Dcache.provider=localCache
	// -Dcache.provider=ehCache1x
	// -Dcache.provider=jbossCache3x
	// -Dcache.provider=coherence3x
	// -Dcache.provider=infinispan4x
	// or manually call factory with (hard coded...)
	// CacheManager cacheManager = CacheFactory.getCacheManager("ehCache1x");
	final CacheManager cacheManager = CacheFactory.provides();

	// instantiate your person cache :
	personCache = cacheManager.getCache(Person.class);
   }

   /**
    * find a person by its id in cache layer
    * 
    * @param id
    * @return person instance mapped to the id
    */
   public Person findById(final Integer id) {
	return personCache.get(id);
   }

   /**
    * Persist your person to db, file,... and put it in person cache
    * 
    * @param bean
    */
   public void persist(final Person bean) {
	// persit with dao access
	// ...
	// all is ok, cache the entity
	personCache.put(bean.getId(), bean);
   }

   /**
    * @param bean remove a person from persistent layer, and cache layer
    */
   public void remove(final Person bean) {
	// remove your person with dao access
	// ...
	// all is ok, remove cache the entity
	personCache.remove(bean.getId());
   }

   /**
    * @return all keys of the cache instance
    */
   public Set<Integer> keys() {
	return personCache.keys();
   }

}
