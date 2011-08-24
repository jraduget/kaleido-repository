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

import static org.kaleidofoundry.core.cache.CacheConstants.JbossCachePluginName;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheProvidersEnum.jbossCache3x;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JbossCache {@link org.kaleidofoundry.core.cache.Cache} implementation
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@Declare(JbossCachePluginName)
public class Jboss32xCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements org.kaleidofoundry.core.cache.Cache<K, V> {

   private static final Logger LOGGER = LoggerFactory.getLogger(Jboss32xCacheImpl.class);

   private final Cache<K, V> cache;
   private final Node<K, V> root;
   private final Jboss32xCacheManagerImpl cacheManager;

   /**
    * @param context
    */
   Jboss32xCacheImpl(@NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(context.getString(CacheName), context);
   }

   /**
    * @param c
    * @param context
    */
   Jboss32xCacheImpl(@NotNull final Class<V> c, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(c.getName(), context);
   }

   /**
    * @param name
    * @param context
    */
   Jboss32xCacheImpl(final String name, @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	this(name, null, context);
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    */
   Jboss32xCacheImpl(final String name, final Jboss32xCacheManagerImpl cacheManager) {
	this(name, cacheManager, new RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>>());
   }

   /**
    * constructor used by direct ioc injection like spring / guice ...
    * 
    * @param name
    * @param cacheManager
    * @param context
    */
   Jboss32xCacheImpl(final String name, final Jboss32xCacheManagerImpl cacheManager,
	   @NotNull final RuntimeContext<org.kaleidofoundry.core.cache.Cache<K, V>> context) {
	// check name argument in ancestor
	super(name, context);

	// the cacheManager to use
	if (cacheManager != null) {
	   this.cacheManager = cacheManager;
	} else {
	   this.cacheManager = (Jboss32xCacheManagerImpl) CacheManagerFactory.provides(jbossCache3x.name(),
		   new RuntimeContext<org.kaleidofoundry.core.cache.CacheManager>(jbossCache3x.name(), org.kaleidofoundry.core.cache.CacheManager.class, context));
	}
	// create internal cache provider
	this.cache = this.cacheManager.createCache(context.getString(CacheName));
	// create jboss cache root and start it
	this.root = createAndStartIt();
	// registered it to cache manager (needed by spring or guice direct injection)
	this.cacheManager.cachesByName.put(name, this);
   }

   /**
    * @see AbstractCache#AbstractCache()
    */
   Jboss32xCacheImpl() {
	this.cache = null;
	this.root = null;
	this.cacheManager = null;
   }

   /**
    * @return new jboss started cache instance
    */
   protected Node<K, V> createAndStartIt() {
	final String cacheName = "/" + name.replaceAll("[.]", "/");
	final Node<K, V> root = cache.getRoot().addChild(Fqn.fromString(cacheName));

	// create and start cache
	LOGGER.info("Starts JBoss Cache root name: {}", cacheName);

	cache.create();
	cache.start();

	return root;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @Override
   public V doGet(final K id) {
	return root.get(id);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	root.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final K id) {
	return root.remove(id) != null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @Override
   public Set<K> keys() {
	return root.getKeys();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#values()
    */
   @Override
   public Collection<V> values() {
	return root.getData().values();
   }

   /**
    * Stop and destroy cache instance
    */
   @Override
   void destroy() {
	cache.stop();
	cache.destroy();
	cacheManager.cachesByName.remove(getName());
	super.destroy();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	root.clearData();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return root.dataSize();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return root;
   }

}
