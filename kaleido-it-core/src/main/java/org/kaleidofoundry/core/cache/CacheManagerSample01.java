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

import java.util.GregorianCalendar;

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple cache manager usage</h3> Inject {@link CacheManager} context and instance using {@link Context} annotation without parameters,
 * but using external configuration
 * </p>
 * <br/>
 * <b>Precondition :</b> The following java env. variable have been set
 * 
 * <pre>
 * -Dkaleido.configurations=myConfig=classpath:/cache/myContext.properties
 * </pre>
 * 
 * Resource file : "classpath:/cache/myContext.properties" contains :
 * 
 * <pre>
 * # cache provider code, and cache configuration file uri
 * cacheManagers.myCacheManager.providerCode=ehCache2x
 * cacheManagers.myCacheManager.fileStoreUri=classpath:/cache/ehcache.xml
 * 
 * # sample if your cache configuration is accessible from an external file store
 * #cacheManagers.myCacheManager.fileStoreUri=http://localhost:8080/kaleido-it/cache/ehcache.xml
 * #cacheManagers.myCacheManager.fileStoreRef=myHttpCtx
 * 
 * # sample if you need proxy settings, uncomment and configure followings :
 * #fileStores.myHttpCtx.proxySet=true
 * #fileStores.myHttpCtx.proxyHost=yourProxyHost
 * #fileStores.myHttpCtx.proxyUser=yourProxyUser
 * #fileStores.myHttpCtx.proxyPassword=proxyUserPassword
 * 
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class CacheManagerSample01 {

   @Context
   private CacheManager myCacheManager;

   private final Cache<String, YourBean> myCache;

   public CacheManagerSample01() {

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
    * Feed cache with some datas
    * 
    * @param myCache
    */
   static void feedCache(final Cache<String, YourBean> myCache) {
	myCache.put("bean1", new YourBean("name1", GregorianCalendar.getInstance(), true, 2));
	myCache.put("bean2", new YourBean("name2", GregorianCalendar.getInstance(), false, 15));
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