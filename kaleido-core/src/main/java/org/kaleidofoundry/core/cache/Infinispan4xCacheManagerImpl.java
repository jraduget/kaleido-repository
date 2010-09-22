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

import static org.kaleidofoundry.core.cache.CacheConstants.InfinispanCacheManagerPluginName;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.infinispan.config.ConfigurationException;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jboss3x factory implementation
 * 
 * @see CacheManagerFactory
 * @author Jerome RADUGET
 */
@Declare(value = InfinispanCacheManagerPluginName)
class Infinispan4xCacheManagerImpl extends org.kaleidofoundry.core.cache.AbstractCacheManager {

   /** internal logger */
   private static final Logger LOGGER = LoggerFactory.getLogger(Infinispan4xCacheManagerImpl.class);

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = null;

   // internal cache manager
   private final CacheManager cacheManager;

   /**
    * @param context
    */
   public Infinispan4xCacheManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	this(null, context);
   }

   /**
    * @param configuration
    * @param context
    */
   public Infinispan4xCacheManagerImpl(final String configuration, final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {

	super(configuration, context);
	try {
	   final String configurationUri = getCurrentConfiguration();

	   if (StringHelper.isEmpty(configurationUri)) {
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", getMetaInformations(), DefaultCacheConfiguration));
		cacheManager = new DefaultCacheManager(true);
	   } else {
		final InputStream inConf = getConfiguration(configurationUri);
		if (inConf != null) {
		   LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", getMetaInformations(), configurationUri));
		   cacheManager = new DefaultCacheManager(inConf, true);
		} else {
		   throw new CacheConfigurationNotFoundException("cache.configuration.notfound", InfinispanCacheManagerPluginName, configurationUri);
		}
	   }
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, InfinispanCacheManagerPluginName, getCurrentConfiguration());
	} catch (final IOException ioe) {
	   throw new CacheConfigurationException("cache.configuration.error", ioe, InfinispanCacheManagerPluginName, getCurrentConfiguration());
	}
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
	return "infinispan-4.x - [4.0.x -> 4.0.x]";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCache(java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final String name) {

	Cache<K, V> cache = cachesByName.get(name);

	if (cache == null) {
	   final org.infinispan.Cache<?, ?> jbossCache = createCache(name);
	   cache = new Infinispan4xCacheImpl(name, jbossCache);
	   cachesByName.put(name, cache);
	}

	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @Override
   public void destroy(final String cacheName) {
	final Cache<?, ?> cache = cachesByName.get(cacheName);
	if (cache != null) {
	   ((Infinispan4xCacheImpl<?, ?>) cache).getInfinispanCache().getCacheManager().stop();
	   ((Infinispan4xCacheImpl<?, ?>) cache).hasBeenDestroy = true;
	   cachesByName.remove(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroyAll()
    */
   @Override
   public void destroyAll() {
	super.destroyAll();
	for (final String name : cachesByName.keySet()) {
	   LOGGER.info(CacheMessageBundle.getMessage("cache.destroy.info", getMetaInformations(), name));
	   destroy(name);
	}
	cacheManager.stop();

	// unregister cacheManager
	CacheManagerProvider.getRegistry()
		.remove(CacheManagerProvider.getCacheManagerId(DefaultCacheProviderEnum.infinispan4x.name(), getCurrentConfiguration()));
   }

   /**
    * @param name
    * @return provider cache instance
    */
   protected <K, V> org.infinispan.Cache<K, V> createCache(final String name) {
	try {
	   LOGGER.info(CacheMessageBundle.getMessage("cache.create.default", getMetaInformations(), getCurrentConfiguration(), name));
	   return cacheManager.getCache(name);
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, InfinispanCacheManagerPluginName, getCurrentConfiguration());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#clearStatistics(java.lang.String)
    */
   @Override
   @Review(category = ReviewCategoryEnum.Todo)
   public void clearStatistics(final String cacheName) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   @Review(category = ReviewCategoryEnum.Todo)
   public Map<String, Object> dumpStatistics(final String cacheName) {
	return new LinkedHashMap<String, Object>();
   }

}
