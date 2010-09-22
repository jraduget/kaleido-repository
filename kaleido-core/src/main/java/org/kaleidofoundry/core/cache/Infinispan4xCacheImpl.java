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

import static org.kaleidofoundry.core.cache.CacheConstants.InfinispanCachePluginName;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.infinispan.Cache;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@Declare(InfinispanCachePluginName)
public class Infinispan4xCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements
	org.kaleidofoundry.core.cache.Cache<K, V> {

   private final String name;
   private final Cache<K, V> cache;

   /**
    * @param c class of the cache
    * @param cache infinispan cache instantiate via factory
    */
   Infinispan4xCacheImpl(@NotNull final Class<V> c, @NotNull final Cache<K, V> cache) {
	this(c.getName(), cache);
   }

   /**
    * @param name name of the cache
    */
   Infinispan4xCacheImpl(@NotNull final String name, @NotNull final Cache<K, V> cache) {
	this.cache = cache;
	this.name = name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getName()
    */
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @Override
   public V doGet(final K key) {
	return cache.get(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	cache.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#remove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final K key) {
	return cache.remove(key, cache.get(key));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @Override
   public Set<K> keys() {
	return cache.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#values()
    */
   @Override
   public Collection<V> values() {
	return cache.values();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	cache.clear();

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return cache.size();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return cache;
   }

   /**
    * @return infinispan internal cache
    */
   protected Cache<K, V> getInfinispanCache() {
	return cache;
   }

}
