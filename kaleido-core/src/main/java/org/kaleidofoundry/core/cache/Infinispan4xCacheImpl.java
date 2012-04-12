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
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheProvidersEnum.infinispan4x;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.infinispan.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
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

   // internal infinspan cache instance
   private final Cache<K, V> cache;
   // instance of the cacheManager to use
   private final Infinispan4xCacheManagerImpl cacheManager;

   /**
    * @param context
    */
   Infinispan4xCacheImpl(@NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(context.getString(CacheName), context);
   }

   /**
    * @param c
    * @param context
    */
   Infinispan4xCacheImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(c.getName(), context);
   }

   /**
    * @param name
    * @param context
    */
   Infinispan4xCacheImpl(final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(name, null, context);
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    */
   Infinispan4xCacheImpl(final String name, final Infinispan4xCacheManagerImpl cacheManager) {
	this(name, cacheManager, new RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>>());
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    * @param context
    */
   Infinispan4xCacheImpl(final String name, final Infinispan4xCacheManagerImpl cacheManager,
	   @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	super(name, context);

	// the cacheManager to use
	if (cacheManager != null) {
	   this.cacheManager = cacheManager;
	} else {
	   this.cacheManager = (Infinispan4xCacheManagerImpl) CacheManagerFactory.provides(infinispan4x.name(),
		   new RuntimeContext<org.kaleidofoundry.core.cache.CacheManager>(infinispan4x.name(), org.kaleidofoundry.core.cache.CacheManager.class, context));
	}

	// create internal cache provider
	this.cache = this.cacheManager.createCache(name);

	// registered it to cache manager (needed by spring or guice direct injection)
	this.cacheManager.cachesByName.put(name, this);
   }

   /**
    * @see AbstractCache#AbstractCache()
    */
   Infinispan4xCacheImpl() {
	this.cache = null;
	this.cacheManager = null;
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
    * @see org.kaleidofoundry.core.cache.AbstractCache#containsKey(java.io.Serializable)
    */
   @Override
   public boolean containsKey(final K key) {
	return cache.containsKey(key);
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

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#destroy()
    */
   @Override
   void destroy() {
	cache.clear();
	cache.stop();
	cacheManager.cachesByName.remove(getName());
	super.destroy();
   }


}
