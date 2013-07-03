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

   /** interface cache manager declare plugin name */
   String CacheManagerPluginName = "cacheManagers";

   /** interface cache declare plugin name */
   String CachePluginName = "caches";

   /** Default local cache manager implementation declare plugin name */
   String DefaultLocalCacheManagerPluginName = "cacheManagers.local";

   /** Default local cache implementation declare plugin name */
   String DefaultLocalCachePluginName = "caches.local";

   /** EhCache cache manager implementation declare plugin name */
   String EhCacheManagerPluginName = "cacheManagers.ehCache";

   /** EhCache implementation declare plugin name */
   String EhCachePluginName = "caches.ehCache";

   /** Apache JCS cache manager implementation declare plugin name */
   String JcsCacheManagerPluginName = "cacheManagers.jcs";

   /** Apache JCS implementation declare plugin name */
   String JcsCachePluginName = "caches.jcs";

   /** JbossCache implementation declare plugin name */
   String JbossCachePluginName = "caches.jbossCache3x";

   /** JbossCache manager implementation declare plugin name */
   String JbossCacheManagerPluginName = "cacheManagers.jbossCache3x";

   /** Jboss Infinispan cache implementation declare plugin name */
   String InfinispanCachePluginName = "caches.infinispan";

   /** Jboss Infinispan cache manager implementation declare plugin name */
   String InfinispanCacheManagerPluginName = "cacheManagers.infinispan";

   /** Oracle Coherence cache implementation declare plugin name */
   String CoherenceCachePluginName = "caches.coherence3x";

   /** Oracle Coherence cache manager implementation declare plugin name */
   String CoherenceCacheManagerPluginName = "cacheManagers.coherence3x";

   /** IBM Websphere cache implementation declare plugin name */
   String WebsphereCachePluginName = "caches.websphere";

   /** IBM Websphere cache manager implementation declare plugin name */
   String WebsphereCacheManagerPluginName = "cacheManagers.websphere";

   /** Google application engine cache implementation declare plugin name */
   String GaeCachePluginName = "caches.gae";

   /** Google application engine cache manager implementation declare plugin name */
   String GaeCacheManagerPluginName = "cacheManagers.gae";

}
