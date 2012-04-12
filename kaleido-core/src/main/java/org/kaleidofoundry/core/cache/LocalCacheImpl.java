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
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheProvidersEnum.local;

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
   private final LocalCacheManagerImpl cacheManager;

   /**
    * @param context
    */
   LocalCacheImpl(@NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(context.getString(CacheName), context);
   }

   /**
    * @param c
    * @param context
    */
   LocalCacheImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(c.getName(), context);
   }

   /**
    * @param name
    * @param context
    */
   LocalCacheImpl(final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(name, null, context);
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    */
   LocalCacheImpl(final String name, final LocalCacheManagerImpl cacheManager) {
	this(name, cacheManager, new RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>>());
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    * @param context
    */
   LocalCacheImpl(final String name, final LocalCacheManagerImpl cacheManager, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	super(name, context);

	if (cacheManager != null) {
	   this.cacheManager = cacheManager;
	} else {
	   this.cacheManager = (LocalCacheManagerImpl) CacheManagerFactory.provides(local.name(), new RuntimeContext<org.kaleidofoundry.core.cache.CacheManager>(
		   local.name(), org.kaleidofoundry.core.cache.CacheManager.class, context));
	}

	// registered it to cache manager (needed by spring or guice direct injection)
	this.cacheManager.cachesByName.put(name, this);

   }

   /**
    * @see AbstractCache#AbstractCache()
    */
   LocalCacheImpl() {
	this.cacheManager = null;
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
    * @see org.kaleidofoundry.core.cache.AbstractCache#containsKey(java.io.Serializable)
    */
   @Override
   public boolean containsKey(final K key) {
	return CacheableMap.containsKey(key);
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

   @Override
   void destroy() {
	CacheableMap.clear();
	cacheManager.cachesByName.remove(getName());
	super.destroy();
   }
}
