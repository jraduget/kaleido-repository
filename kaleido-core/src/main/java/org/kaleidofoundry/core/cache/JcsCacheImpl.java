/*  
 * Copyright 2008-2013 the original author or authors 
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

import static org.kaleidofoundry.core.cache.CacheConstants.JcsCachePluginName;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheProvidersEnum.jcs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.jcs.JCS;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * @author Jerome RADUGET
 */
@Declare(value = JcsCachePluginName)
public class JcsCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

   private static final String DEFAULT_JCS_GROUP = "__DEFAULT_JCS_GROUP__";

   private final JcsCacheManagerImpl cacheManager;
   private final JCS cache;
   private final String group;

   /**
    * @param context
    */
   JcsCacheImpl(@NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(context.getString(CacheName), context);
   }

   /**
    * @param c
    * @param context
    */
   JcsCacheImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(c.getName(), context);
   }

   /**
    * @param name
    * @param context
    */
   JcsCacheImpl(final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(name, null, context);
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    */
   JcsCacheImpl(final String name, final JcsCacheManagerImpl cacheManager) {
	this(name, cacheManager, new RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>>());
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    * @param context
    */
   JcsCacheImpl(final String name, final JcsCacheManagerImpl cacheManager, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	// check name argument in ancestor
	super(name, context);

	// apache jcp region
	group = context.getString(CacheContextBuilder.JcsGroup, DEFAULT_JCS_GROUP);
	
	// the cacheManager to use
	if (cacheManager != null) {
	   this.cacheManager = cacheManager;
	} else {
	   this.cacheManager = (JcsCacheManagerImpl) CacheManagerFactory.provides(jcs.name(),
		   new RuntimeContext<org.kaleidofoundry.core.cache.CacheManager>(jcs.name(), org.kaleidofoundry.core.cache.CacheManager.class, context));
	}

	// create internal cache provider
	cache = cacheManager.createCache(name);

	// registered it to cache manager (needed by spring or guice direct injection)
	this.cacheManager.cachesByName.put(name, this);
   }

   /**
    * @see AbstractCache#AbstractCache()
    */
   JcsCacheImpl() {
	this.cache = null;
	this.cacheManager = null;
	this.group = DEFAULT_JCS_GROUP;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#clear()
    */
   @Override
   public void clear() {
	try {
	   cache.clear();
	} catch (org.apache.jcs.access.exception.CacheException e) {
	   throw new CacheException("cache.clear.error", e);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @SuppressWarnings("unchecked")
   @Override
   @NotNull
   public Set<K> keys() {
	return cache.getGroupKeys(group);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#values()
    */
   @Override
   public Collection<V> values() {
	final Set<K> keys = keys();
	final Collection<V> result = new ArrayList<V>(keys.size());
	V value;
	for (final K key : keys) {
	   value = get(key);
	   if (value != null) {
		result.add(value);
	   }
	}
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return keys().size();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @SuppressWarnings("unchecked")
   @Override
   protected V doGet(K key) {
	return (V) cache.getFromGroup(key, group);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   protected void doPut(K key, V value) {
	try {
	   cache.putInGroup(key, group, value);
	} catch (org.apache.jcs.access.exception.CacheException e) {
	   throw new CacheException("cache.put.error", e);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   protected boolean doRemove(K key) {
	boolean exists = keys().contains(key);
	cache.remove(key, group);
	return exists;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#destroy()
    */
   @Override
   void destroy() {
	cache.dispose();
	super.destroy();
   }

}
