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

import static org.kaleidofoundry.core.cache.CacheConstants.DefaultLocalCachePluginName;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * Local cache implementation<br/>
 * Implementation use internally a {@link ConcurrentHashMap} to store key / value
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@Declare(DefaultLocalCachePluginName)
public class LocalCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

   private final ConcurrentMap<K, V> CacheableMap = new ConcurrentHashMap<K, V>();

   /**
    * @param name
    * @param context
    */
   public LocalCacheImpl(@NotNull final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	super(name, context);
   }

   /**
    * @param cl
    * @param context
    */
   public LocalCacheImpl(final Class<?> cl, final RuntimeContext<Cache<K, V>> context) {
	super(cl, context);
   }

   /**
    * @param context
    */
   public LocalCacheImpl(final RuntimeContext<Cache<K, V>> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @Override
   protected V doGet(final K key) {
	return CacheableMap.get(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   protected void doPut(final K key, final V entity) {
	CacheableMap.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   protected boolean doRemove(final K key) {
	final boolean present = CacheableMap.get(key) != null;
	if (present) {
	   CacheableMap.remove(key);
	}
	return present;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @Override
   public Set<K> keys() {
	return CacheableMap.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#values()
    */
   @Override
   public Collection<V> values() {
	return CacheableMap.values();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	CacheableMap.clear();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return CacheableMap.size();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return CacheableMap;
   }

   /**
    * Stop and destroy cache instance
    */
   void destroy() {
	CacheableMap.clear();
	hasBeenDestroy = true;
   }
}
