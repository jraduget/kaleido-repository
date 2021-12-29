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

import static org.kaleidofoundry.core.cache.CacheConstants.InfinispanCacheManagerPluginName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.infinispan.config.ConfigurationException;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Jboss infinispan cache manager provider
 * 
 * @see CacheManagerFactory
 * @author jraduget
 */
@Declare(value = InfinispanCacheManagerPluginName)
public class InfinispanCacheManagerImpl extends org.kaleidofoundry.core.cache.AbstractCacheManager {

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = null;

   // internal cache manager
   final CacheManager infiniSpanCacheManager;

   /**
    * @param context
    */
   public InfinispanCacheManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	this(context.getString(FileStoreUri), context);
   }

   /**
    * @param configuration override the context configuration file (if defined)
    * @param context
    */
   public InfinispanCacheManagerImpl(final String configuration, final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	super(configuration, context);

	try {
	   if (StringHelper.isEmpty(configuration)) {
		infiniSpanCacheManager = new DefaultCacheManager(true);
	   } else {
		final InputStream inConf = getConfiguration(configuration);
		if (inConf != null) {
		   infiniSpanCacheManager = new DefaultCacheManager(inConf, true);
		} else {
		   throw new CacheConfigurationNotFoundException("cache.configuration.notfound", InfinispanCacheManagerPluginName, configuration);
		}
	   }

	   infiniSpanCacheManager.start();

	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, InfinispanCacheManagerPluginName, getCurrentConfiguration());
	} catch (final IOException ioe) {
	   throw new CacheConfigurationException("cache.configuration.error", ioe, InfinispanCacheManagerPluginName, getCurrentConfiguration());
	}
   }

   /**
    * @see AbstractCacheManager#AbstractCacheManager()
    */
   InfinispanCacheManagerImpl() {
	super();
	infiniSpanCacheManager = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDefaultConfiguration()
    */
   @Override
   public String getDefaultConfiguration() {
	return DefaultCacheConfiguration;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getMetaInformations()
    */
   @Override
   public String getMetaInformations() {
	return "infinispan[4.0.x -> 5.1.x]";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.String, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final String name, @NotNull final RuntimeContext<Cache<K, V>> context) {
	Cache<K, V> cache = cachesByName.get(name);
	if (cache == null) {
	   cache = new InfinispanCacheImpl(name, this, context);
	   cachesByName.put(name, cache);
	}
	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @Override
   public synchronized void destroy(final String cacheName) {
	final Cache<?, ?> cache = cachesByName.get(cacheName);
	if (cache != null) {
	   cache.clear();
	   ((InfinispanCacheImpl<?, ?>) cache).destroy();
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
	infiniSpanCacheManager.stop();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#clearStatistics(java.lang.String)
    */
   @Override
   @Task(labels = TaskLabel.ImplementIt)
   public void clearStatistics(final String cacheName) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   @Task(labels = TaskLabel.ImplementIt)
   public Map<String, Object> dumpStatistics(final String cacheName) {
	return new LinkedHashMap<String, Object>();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return infiniSpanCacheManager;
   }

   /**
    * @param name
    * @return provider cache instance
    */
   protected <K, V> org.infinispan.Cache<K, V> createCache(final String name) {

	traceCacheCreation(name);
	
	try {
	   return infiniSpanCacheManager.getCache(name);
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, InfinispanCacheManagerPluginName, getCurrentConfiguration());
	}
   }
}
