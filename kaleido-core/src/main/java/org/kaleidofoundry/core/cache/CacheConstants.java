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

/**
 * Cache module constants
 * 
 * @author Jerome RADUGET
 */
public interface CacheConstants {

   /**
    * Default cache enum code.
    * 
    * @author Jerome RADUGET
    */
   static enum DefaultCacheProviderEnum {

	/* If you change an enum name, please changed below ;-) */

	ehCache1x,
	jbossCache3x,
	infinispan4x,
	coherence3x,
	gigaspace7x,
	local;
   }

   /** Java system environment variable to set default cache provider to use */
   String CACHE_PROVIDER_ENV = "cache.provider";

   /** interface cache manager declare plugin name */
   String CacheManagerPluginName = "cacheManager";

   /** interface cache declare plugin name */
   String CachePluginName = "cache";

   /** Default local cache manager implementation declare plugin name */
   String DefaultLocalCacheManagerPluginName = "cacheManager.local";

   /** Default local cache implementation declare plugin name */
   String DefaultLocalCachePluginName = "cache.local";

   /** EhCache cache manager implementation declare plugin name */
   String EhCacheManagerPluginName = "cacheManager.ehCache1x";

   /** EhCache implementation declare plugin name */
   String EhCachePluginName = "cache.ehCache1x";

   /** JbossCache implementation declare plugin name */
   String JbossCachePluginName = "cache.jbossCache3x";

   /** JbossCache manager implementation declare plugin name */
   String JbossCacheManagerPluginName = "cacheManager.jbossCache3x";

   /** Infinispan cache implementation declare plugin name */
   String InfinispanCachePluginName = "cache.infinispan4x";

   /** Infinispan cache manager implementation declare plugin name */
   String InfinispanCacheManagerPluginName = "cacheManager.infinispan4x";

   /** Coherence cache implementation declare plugin name */
   String CoherenceCachePluginName = "cache.coherence3x";

   /** Coherence cache manager implementation declare plugin name */
   String CoherenceCacheManagerPluginName = "cacheManager.coherence3x";

}
