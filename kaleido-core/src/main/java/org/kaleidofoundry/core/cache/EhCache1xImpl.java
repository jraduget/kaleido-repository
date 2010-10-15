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

import static org.kaleidofoundry.core.cache.CacheConstants.EhCachePluginName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * EhCache {@link org.kaleidofoundry.core.cache.Cache} implementation <br/>
 * Cache statistics are disabled in this version, due to performance reason (prior to 1.7.x) <br/>
 * With EhCache 2.x you can disable cache by configuration :)
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@Declare(EhCachePluginName)
public class EhCache1xImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements org.kaleidofoundry.core.cache.Cache<K, V> {

   private final Cache cache;

   /**
    * @param context
    * @param cache
    */
   EhCache1xImpl(@NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context, @NotNull final Cache cache) {
	super(context);
	this.cache = cache;
   }

   /**
    * @param c
    * @param context
    * @param cache
    */
   EhCache1xImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context, @NotNull final Cache cache) {
	this(c.getName(), context, cache);
   }

   /**
    * @param name
    * @context
    * @param cache
    */
   EhCache1xImpl(@NotNull final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context, @NotNull final Cache cache) {
	super(name, context);
	this.cache = cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @SuppressWarnings("unchecked")
   @Override
   public V doGet(final K id) {
	final Element elt = cache.getQuiet(id); // no stat, perf. decreaze :(
	return elt != null ? (V) elt.getObjectValue() : null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	cache.putQuiet(new Element(key, entity)); // no stat, perf. decreaze :(
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final Serializable id) {
	return cache.removeQuiet(id);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Set<K> keys() {
	return new HashSet<K>(cache.getKeys());
   }

   /**
    * ehcache don't provide direct values access in the api. Values are encapsulated into {@link Element}<br/>
    * so for performance reason, this code is no very efficient if cache contains a lot of items. <br/>
    */
   @SuppressWarnings("unchecked")
   @Override
   public Collection<V> values() {

	final Collection<V> result = new ArrayList<V>();
	V value;
	for (final Object key : cache.getKeys()) {
	   value = get((K) key);
	   if (value != null) {
		result.add(value);
	   }
	}
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	cache.removeAll();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return cache.getSize();
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
    * Stop and destroy cache instance
    */
   void destroy() {
	hasBeenDestroy = true;
	// cache.dispose(); must be done by cacheManager
   }

   /**
    * @return the cache
    */
   protected Cache getCache() {
	return cache;
   }

}
