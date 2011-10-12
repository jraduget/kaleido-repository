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

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheManagerSample01.feedCache;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple cache usage</h3> Inject {@link Cache} context and instance using {@link Context} annotation mixing the use of parameters and
 * external configuration (Parameters have priority to the external configuration)
 * </p>
 * <br/>
 * <b>Precondition :</b> The following java env. variable have been set
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/cache/myContext.properties
 * </pre>
 * 
 * Resource file : "classpath:/cache/myContext.properties" contains :
 * 
 * <pre>
 * caches.myCache.cacheName=CacheSample01
 * caches.myCache.cacheManagerRef=myCacheManager
 * 
 * cacheManagers.myCacheManager.providerCode=ehCache2x
 * cacheManagers.myCacheManager.fileStoreUri=classpath:/cache/ehcache.xml
 * 
 * # sample if your cache configuration is accessible from an external file store
 * #cacheManagers.myCacheManager.fileStoreUri=http://localhost:8380/kaleido-it/cache/ehcache.xml
 * #cacheManagers.myCacheManager.fileStoreRef=myHttpCtx
 * 
 * # sample if you need proxy settings, uncomment and configure followings :
 * #fileStores.myHttp.proxySet=true
 * #fileStores.myHttp.proxyHost=yourProxyHost
 * #fileStores.myHttp.proxyUser=yourProxyUser
 * #fileStores.myHttp.proxyPassword=proxyUserPassword
 * 
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class CacheSample03 {

   @Context(value = "myCache", parameters = { @Parameter(name = CacheName, value = "CacheSample03") })
   private Cache<String, YourBean> myCache;

   public CacheSample03() {
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