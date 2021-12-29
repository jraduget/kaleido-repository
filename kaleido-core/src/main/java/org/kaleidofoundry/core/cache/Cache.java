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

import static org.kaleidofoundry.core.cache.CacheConstants.CachePluginName;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * Cache common interface
 * 
 * @param <K> Type of cache keys
 * @param <V> Type of cache values
 * @see CacheFactory
 * @see CacheContextBuilder
 * @author jraduget
 */
@Declare(CachePluginName)
@Provider(value = CacheProvider.class)
public interface Cache<K extends Serializable, V extends Serializable> {

   /**
    * The cache name
    * 
    * @return the cache name
    */
   @NotNull
   String getName();

   /**
    * Get a value from cache
    * 
    * @param key
    * @return get entry mapping to the key parameter
    */
   V get(@NotNull K key);

   /**
    * Puts an entry in the cache (new or update)
    * 
    * @param key key of the entity to put in cache
    * @param entity entity to put in cache
    */
   void put(@NotNull K key, @NotNull V entity);

   /**
    * Remove an entry from the cache
    * 
    * @param key
    * @return <code>true</code> if found and removed, <code>false</code> otherwise
    */
   boolean remove(@NotNull K key);

   /**
    * Remove all entries from the cache
    */
   void clear();

   /**
    * Set of keys presents in the cache
    * 
    * @return all cache keys items
    */
   @NotNull
   Set<K> keys();

   /**
    * Is that the cache keys has this key value
    * 
    * @param key
    * @return <code>true|false</code>
    */
   boolean containsKey(@NotNull K key);

   /**
    * Collection of values presents in the cache.
    * <p>
    * <b>Be careful :</b> in a distributed caching environment, the usage of this method is not efficient.
    * Use the specifics caching provider features (distributed query, index, ...) to get or filter all the cache entries.
    * <br/>
    * If your cache is local and embedded to your application memory, there is less constraints to use this method.
    * </p> 
    * 
    * @return all cache items values
    */
   Collection<V> values();

   /**
    * Number of entries present in the cache
    * 
    * @return entries count of the cache
    */
   int size();

   /**
    * Does the cache have been destroyed by a stop processing
    * 
    * @return <code>true</code> if cache have been destroyed, <code>false</code> otherwise <br/>
    *         Can be useful when cache instance is stored in a class field...
    */
   boolean hasBeenDestroy();

   /**
    * The underlying cache provider implementation
    * 
    * @return Return the underlying provider object for the Cache
    */
   Object getDelegate();
}
