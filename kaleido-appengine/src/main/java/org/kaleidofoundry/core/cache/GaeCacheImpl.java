/*  
 * Copyright 2008-2016 the original author or authors 
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

import static org.kaleidofoundry.core.cache.CacheConstants.GaeCachePluginName;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheProvidersEnum.gae;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * Google application engine cache
 * 
 * @author jraduget
 */
@Declare(value = GaeCachePluginName)
public class GaeCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

   // internal gae instance
   private final com.google.appengine.api.memcache.jsr107cache.GCache cache;
   // instance of the cacheManager to use
   private final GaeCacheManagerImpl cacheManager;

   /**
    * @param context
    */
   GaeCacheImpl(@NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(context.getString(CacheName), context);
   }

   /**
    * @param c
    * @param context
    */
   GaeCacheImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(c.getName(), context);
   }

   /**
    * @param name
    * @param context
    */
   GaeCacheImpl(final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(name, null, context);
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    */
   GaeCacheImpl(final String name, final GaeCacheManagerImpl cacheManager) {
	this(name, cacheManager, new RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>>());
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    * @param context
    */
   GaeCacheImpl(final String name, final GaeCacheManagerImpl cacheManager, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	// check name argument in ancestor
	super(name, context);

	// the cacheManager to use
	if (cacheManager != null) {
	   this.cacheManager = cacheManager;
	} else {
	   this.cacheManager = (GaeCacheManagerImpl) CacheManagerFactory.provides(gae.name(),
		   new RuntimeContext<org.kaleidofoundry.core.cache.CacheManager>(gae.name(), org.kaleidofoundry.core.cache.CacheManager.class, context));
	}

	// create internal cache provider
	cache = this.cacheManager.createCache(name);

	// registered it to cache manager (needed by spring or guice direct injection)
	this.cacheManager.cachesByName.put(name, this);
   }

   /**
    * @see AbstractCache#AbstractCache()
    */
   GaeCacheImpl() {
	this.cache = null;
	this.cacheManager = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @SuppressWarnings("unchecked")
   @Override
   public V doGet(final K id) {
	final Object elt = cache.get(id);
	return elt != null ? (V) elt : null;
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
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final Serializable id) {
	return cache.remove(id) != null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @SuppressWarnings("unchecked")
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

   @SuppressWarnings("unchecked")
   @Override
   public Collection<V> values() {
	return cache.values();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void clear() {
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

   @Override
   void destroy() {
	cache.clear();
	cacheManager.cachesByName.remove(getName());
	super.destroy();
   }

   /**
    * @return the cache
    */
   protected net.sf.jsr107cache.Cache getCache() {
	return cache;
   }
}
