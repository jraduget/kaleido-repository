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

import static org.kaleidofoundry.core.cache.CacheConstants.DefaultLocalCacheManagerPluginName;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * The default cache manager (local in memory), if no cache provider is specify
 * 
 * @author jraduget
 */
@Declare(value = DefaultLocalCacheManagerPluginName)
public class LocalCacheManagerImpl extends org.kaleidofoundry.core.cache.AbstractCacheManager {

   /**
    * @param context
    */
   public LocalCacheManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	super(context);
   }

   /**
    * @param configuration override the context configuration file (if defined)
    * @param context
    */
   public LocalCacheManagerImpl(final String configuration, final RuntimeContext<CacheManager> context) {
	super(configuration, context);
   }

   /**
    * @see AbstractCacheManager#AbstractCacheManager()
    */
   LocalCacheManagerImpl() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDefaultConfiguration()
    */
   @Override
   public String getDefaultConfiguration() {
	return "";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getMetaInformations()
    */
   @Override
   public String getMetaInformations() {
	return "java.util.concurrent.ConcurrentMap<K,V>";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.String, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @SuppressWarnings("unchecked")
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final String name, @NotNull final RuntimeContext<Cache<K, V>> context) {

	Cache<K, V> cache = cachesByName.get(name);

	if (cache == null) {
	   cache = createCache(name, getCurrentConfiguration(), context);
	   cachesByName.put(name, cache);
	}

	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @SuppressWarnings({ "rawtypes" })
   @Override
   public synchronized void destroy(final String cacheName) {
	final Cache<?, ?> cache = cachesByName.get(cacheName);
	if (cache != null) {
	   ((LocalCacheImpl) cache).destroy();
	   cachesByName.remove(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroyAll()
    */
   @Override
   public synchronized void destroyAll() {
	super.destroyAll();
	for (final String name : cachesByName.keySet()) {
	   LOGGER.info(CacheMessageBundle.getMessage("cachemanager.destroy.info", name));
	   destroy(name);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(final String cacheName) {
	return new LinkedHashMap<String, Object>();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#clearStatistics(java.lang.String)
    */
   @Override
   public void clearStatistics(final String cacheName) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return this;
   }

   /**
    * @param name
    * @param configurationUri
    * @return provider cache instance
    */
   protected <K extends Serializable, V extends Serializable> LocalCacheImpl<K, V> createCache(final String name, final String configurationUri,
	   final RuntimeContext<Cache<K, V>> context) {
	traceCacheCreation(name);
	return new LocalCacheImpl<K, V>(name, this, context);
   }
}
