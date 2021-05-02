/*
 * Copyright 2008-2021 the original author or authors
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

import static org.kaleidofoundry.core.cache.CacheManagerSample01.feedCache;

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * <p>
 * <h3>Simple cache manager usage</h3> Build {@link CacheManager} context and instance manually by coding, using context builder
 * </p>
 * <br/>
 * 
 * @author jraduget
 */
public class CacheManagerSample04 {

   // no automatic context injection here
   private final CacheManager myCacheManager;

   private final Cache<String, YourBean> myCache;

   public CacheManagerSample04() {

	RuntimeContext<CacheManager> context = new CacheManagerContextBuilder("myCacheManager", CacheManager.class).withProviderCode("ehCache")
		.withFileStoreUri("classpath:/cache/ehcache.xml").build();

	myCacheManager = CacheManagerFactory.provides(context);

	// get your cache instance
	myCache = myCacheManager.getCache("CacheSample04");

	// feed cache with some bean entries
	feedCache(myCache);
   }

   /**
    * method example that use the injected cache
    */
   public void echo() {
	System.out.printf("cache name: %s\n", myCache.getName());
	System.out.printf("cache size: %s\n", myCache.size());
	System.out.printf("cache entry[%s]: %s\n", "bean1", myCache.get("bean1").toString());
	System.out.printf("cache entry[%s]: %s\n", "bean2", myCache.get("bean2").toString());
   }

   /**
    * used only for junit assertions
    * 
    * @return current cache instance
    */
   Cache<String, YourBean> getMyCache() {
	return myCache;
   }

}