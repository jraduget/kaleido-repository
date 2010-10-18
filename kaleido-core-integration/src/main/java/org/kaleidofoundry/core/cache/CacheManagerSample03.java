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

import static org.kaleidofoundry.core.cache.CacheManagerSample01.feedCache;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;
/**
 * <p>
 * <h3>Simple cache manager usage</h3> Inject {@link CacheManager} context and instance using {@link Context} annotation mixing the
 * use of parameters and external configuration <br/>
 * Parameters have priority to the external configuration
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
 * cacheManager.myCacheManager.providerCode=ehCache1x
 * cacheManager.myCacheManager.resourceUri=classpath:/cache/ehcache.xml
 * 
 * # sample if your cache configuration is accessible from an external resource store
 * #cacheManager.myCacheManager.resourceUri=http://localhost/kaleidofoundry/it/cache/ehcache.xml
 * #cacheManager.myCacheManager.resourceStoreRef=myHttpCtx
 * 
 * # sample if you need proxy settings, uncomment and configure followings :
 * #resourceStore.myHttp.proxySet=true
 * #resourceStore.myHttp.proxyHost=yourProxyHost
 * #resourceStore.myHttp.proxyUser=yourProxyUser
 * #resourceStore.myHttp.proxyPassword=proxyUserPassword
 * 
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class CacheManagerSample03 {

   @Context(value = "myCacheManagerCtx",
	   parameters = {
	   @Parameter(name = CacheManagerContextBuilder.ProviderCode, value = "local"),
	   @Parameter(name = CacheManagerContextBuilder.ResourceUri, value = "")
   })
   private CacheManager myCacheManager;

   private final Cache<String, YourBean> myCache;

   public CacheManagerSample03() {

	myCache = myCacheManager.getCache(YourBean.class);

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