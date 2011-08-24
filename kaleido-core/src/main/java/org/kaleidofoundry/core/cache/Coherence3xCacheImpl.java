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

import static org.kaleidofoundry.core.cache.CacheConstants.CoherenceCachePluginName;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheProvidersEnum.coherence3x;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;

/**
 * Coherence {@link org.kaleidofoundry.core.cache.Cache} implementation
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@Declare(CoherenceCachePluginName)
public class Coherence3xCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements Cache<K, V> {

   private final NamedCache namedCache;

   private final Coherence3xCacheManagerImpl cacheManager;

   /**
    * @param context
    */
   Coherence3xCacheImpl(@NotNull final RuntimeContext<Cache<K, V>> context) {
	this(context.getString(CacheName), null, context);
   }

   /**
    * @param c
    * @param context
    */
   Coherence3xCacheImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<Cache<K, V>> context) {
	this(c.getName(), context);
   }

   /**
    * @param name
    * @param context
    */
   Coherence3xCacheImpl(@NotNull final String name, @NotNull final RuntimeContext<Cache<K, V>> context) {
	this(name, null, context);
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    */
   Coherence3xCacheImpl(final String name, final Coherence3xCacheManagerImpl cacheManager) {
	this(name, cacheManager, new RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>>());
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    * @param context
    */
   Coherence3xCacheImpl(final String name, final Coherence3xCacheManagerImpl cacheManager, @NotNull final RuntimeContext<Cache<K, V>> context) {
	// check name argument in ancestor
	super(name, context);

	// the cacheManager to use
	if (cacheManager != null) {
	   this.cacheManager = cacheManager;
	} else {
	   this.cacheManager = (Coherence3xCacheManagerImpl) CacheManagerFactory.provides(coherence3x.name(), new RuntimeContext<CacheManager>(
		   coherence3x.name(), CacheManager.class, context));
	}

	// create internal cache provider
	this.namedCache = this.cacheManager.createCache(name);

	// registered it to cache manager (needed by spring or guice direct injection)
	this.cacheManager.cachesByName.put(name, this);
   }

   /**
    * @see AbstractCache#AbstractCache()
    */
   Coherence3xCacheImpl() {
	this.namedCache = null;
	this.cacheManager = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @SuppressWarnings("unchecked")
   @Override
   public V doGet(final K key) {
	return (V) namedCache.get(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	namedCache.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final K key) {
	final boolean result = namedCache.containsKey(key);
	namedCache.remove(key);
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Set<K> keys() {
	return namedCache.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#values()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Collection<V> values() {
	return namedCache.values();
   }

   /**
    * @param filter
    * @return keys set return by the filter
    */
   @SuppressWarnings("unchecked")
   public Set<K> keySet(final Filter filter) {
	return namedCache.keySet(filter);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	namedCache.clear();

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return namedCache.size();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return namedCache;
   }

   /**
    * Stop and destroy cache instance
    */
   @Override
   void destroy() {
	com.tangosol.net.CacheFactory.destroyCache(namedCache);
	cacheManager.cachesByName.remove(getName());
	super.destroy();
   }

   /**
    * @return internal coherence {@link NamedCache}
    */
   protected NamedCache getNamedCache() {
	return namedCache;
   }

}