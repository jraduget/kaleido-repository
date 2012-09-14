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

import static org.kaleidofoundry.core.cache.CacheConstants.CacheManagerPluginName;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * A cache manager handle many {@link Cache} instances<br/>
 * <br/>
 * A cache manager reference a unique configuration file<br/>
 * <br/>
 * 
 * @author Jerome RADUGET
 * @see CacheManagerFactory
 * @see CacheManagerContextBuilder
 */
@Declare(CacheManagerPluginName)
@Provider(value = CacheManagerProvider.class, singletons = true)
public interface CacheManager {

   /**
    * Get a cache instance by name
    * 
    * @param <K> type of the cache keys
    * @param <V> type of the cache values
    * @param name name of the cache you want
    * @return cache instance, whose the name is the name in argument
    */
   <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final String name);

   /**
    * Get a cache instance by name, using a custom {@link RuntimeContext} to initialize the returned cache (the context is used only during
    * the first call)
    * 
    * @param <K> type of the cache keys
    * @param <V> type of the cache values
    * @param name name of the cache you want
    * @param context
    * @return cache instance, whose the name is the name in argument
    */
   <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final String name, @NotNull final RuntimeContext<Cache<K, V>> context);

   /**
    * Get a cache instance by name (the class name as argument)
    * 
    * @param <K> type of the cache keys
    * @param <V> type of the cache values
    * @param cl class
    * @return cache instance, whose the name is the name of the class in argument
    */
   <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final Class<V> cl);

   /**
    * Get a cache instance by name (the class name as argument), and using a custom {@link RuntimeContext} to initialize the returned cache
    * (the context is used only during the first call)
    * 
    * @param <K> type of the cache keys
    * @param <V> type of the cache values
    * @param cl class
    * @param context
    * @return cache instance, whose the name is the name of the class in argument
    */
   <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final Class<V> cl, @NotNull final RuntimeContext<Cache<K, V>> context);


   /**
    * The name of the cache manager
    * 
    * @return cache manager name
    */
   String getName();

   /**
    * The current configuration file cache provider
    * 
    * @return cache manager current cache configuration
    */
   String getCurrentConfiguration();

   /**
    * The default configuration file cache provider (if no configuration file is set)
    * 
    * @return cache manager default cache configuration
    */
   String getDefaultConfiguration();

   /**
    * Some text meta informations of the cache manager
    * 
    * @return cache manager implementation informations
    */
   String getMetaInformations();

   /**
    * Destroy of a cache using the name as an argument
    * 
    * @param cacheName cache name to free / destroy
    */
   void destroy(@NotNull final String cacheName);

   /**
    * Destroy of a cache using the name of the class as an argument
    * 
    * @param cl cache name to free / destroy
    */
   void destroy(@NotNull final Class<?> cl);

   /**
    * Destroy all registered cache
    * stop / destroy / shutdown all cache instances
    */
   void destroyAll();

   /**
    * All registered names cache
    * 
    * @return all cache names instantiate by the factory
    */
   Set<String> getCacheNames();

   /**
    * Statistic dump of a cache name
    * 
    * @param cacheName
    * @return statistic information of the given cache
    */
   Map<String, Object> dumpStatistics(@NotNull final String cacheName);

   /**
    * Clear cache statistics for given cache name
    * 
    * @param cacheName
    */
   void clearStatistics(@NotNull final String cacheName);

   /**
    * @return dump all cache statistics representation<br/>
    *         <ul>
    *         <li>key of the map is cache name
    *         <li>value of a key cache name, is a map of stat. key name / stat. key value
    *         </ul>
    */
   @NotNull
   Map<String, Map<String, Object>> dumpStatistics();

   /**
    * Clear all cache statistics
    */
   void clearStatistics();

   /**
    * Print text statistic information to the given {@link OutputStream}
    * 
    * @param out
    * @throws IOException
    */
   void printStatistics(@NotNull final OutputStream out) throws IOException;

   /**
    * Print text statistic information to the given {@link Writer}
    * 
    * @param writer
    * @throws IOException
    */
   void printStatistics(@NotNull final Writer writer) throws IOException;

   /**
    * Return some staticics as text
    * 
    * @return spring representation of the cache statistics
    * @throws IOException
    */
   @NotNull
   String printStatistics() throws IOException;

   /**
    * The underlying cache manager provider implementation
    * 
    * @return Return the underlying provider object
    */
   Object getDelegate();

}