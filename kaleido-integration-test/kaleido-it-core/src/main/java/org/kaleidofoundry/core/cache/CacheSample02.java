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

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheManagerSample01.feedCache;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple cache usage</h3> Inject {@link Cache} context and instance using {@link Context} annotation with parameters, and without
 * external configuration
 * </p>
 * <p>
 * Default cache manager will be used in this example: see {@link CacheManagerFactory}<br/>
 * <br/>
 * If you want to use another {@link CacheManager} context, you can use context parameter {@link CacheContextBuilder#CacheName}, like :
 * 
 * <pre>
 * &#064;Context(value = &quot;myCache02&quot;, parameters = { @Parameter(name = CacheContextBuilder.CacheName, value = &quot;org.kaleidofoundry.core.cache.YourBean&quot;),
 * 	&#064;Parameter(name = CacheContextBuilder.CacheManagerRef, value = &quot;myCacheManager&quot;) })
 * private Cache&lt;String, YourBean&gt; myCache;
 * </pre>
 * 
 * </p>
 * 
 * @author jraduget
 */
public class CacheSample02 {

   @Context(value = "myCache02", parameters = { @Parameter(name = CacheName, value = "CacheSample02") })
   private Cache<String, YourBean> myCache;

   public CacheSample02() {
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