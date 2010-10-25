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

import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ProviderCode;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ResourceUri;
import static org.kaleidofoundry.core.cache.CacheManagerSample01.feedCache;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple cache manager usage</h3>
 * Inject {@link CacheManager} context and instance using {@link Context} annotation with parameters, and without external configuration
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class CacheManagerSample02 {

   @Context(value="myCacheManagerCtx",
	   parameters = {
	   @Parameter(name = ProviderCode, value = "ehCache1x"),
	   @Parameter(name = ResourceUri, value = "classpath:/cache/ehcache.xml")   }
   )
   private CacheManager myCacheManager;

   private final Cache<String, YourBean> myCache;

   public CacheManagerSample02() {

	// get your cache instance
	myCache = myCacheManager.getCache("CacheSample01");

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