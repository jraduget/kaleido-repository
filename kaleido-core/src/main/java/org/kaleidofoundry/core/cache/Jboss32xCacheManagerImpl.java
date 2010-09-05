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

import static org.kaleidofoundry.core.cache.CacheConstants.JbossCacheManagerPluginName;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.config.ConfigurationException;
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
 * @see CacheFactory
 * @author Jerome RADUGET
 */
@Declare(value = JbossCacheManagerPluginName)
class Jboss32xCacheManagerImpl extends org.kaleidofoundry.core.cache.AbstractCacheManager {

   /** internal logger */
   private static final Logger LOGGER = LoggerFactory.getLogger(Jboss32xCacheManagerImpl.class);

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = "jboss.xml";

   /**
    * @param context
    */
   public Jboss32xCacheManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	this(null, context);
   }

   /**
    * @param configuration
    * @param context
    */
   public Jboss32xCacheManagerImpl(final String configuration, final RuntimeContext<CacheManager> context) {
	super(configuration, context);

	// test if configuration is legal
	final String initTestCacheName = "__inittest__";
	createCache(initTestCacheName, configuration);
	destroy(initTestCacheName);
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
	return "jboss-cache-3.x - [3.0.x -> 3.2.x]";
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
	   final org.jboss.cache.Cache<K, V> jbossCache = createCache(name, getCurrentConfiguration());
	   cache = new Jboss32xCacheImpl<K, V>(name, jbossCache);
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
	   ((Jboss32xCacheImpl<?, ?>) cache).destroy();
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

	// unregister cache manager instance
	CacheManagerProvider.getRegistry()
		.remove(CacheManagerProvider.getCacheManagerId(DefaultCacheProviderEnum.jbossCache3x.name(), getCurrentConfiguration()));
   }

   /**
    * @param name cache name
    * @param configurationUri url to the configuration file of the cache.
    * @return provider cache instance
    */
   protected <K, V> org.jboss.cache.Cache<K, V> createCache(final String name, final String configurationUri) {

	org.jboss.cache.CacheFactory<K, V> cacheFactory;
	org.jboss.cache.Cache<K, V> cache;

	try {

	   cacheFactory = new DefaultCacheFactory<K, V>(); // generic type, don't create it in constructor

	   if (StringHelper.isEmpty(configurationUri)) {
		LOGGER.info(CacheMessageBundle.getMessage("cache.create.default", getMetaInformations(), getCurrentConfiguration(), name));
		cache = cacheFactory.createCache(true);
	   } else {
		final InputStream inConf = getConfiguration(configurationUri);

		if (inConf != null) {
		   LOGGER.info(CacheMessageBundle.getMessage("cache.create.default", getMetaInformations(), getCurrentConfiguration(), name));
		   cache = cacheFactory.createCache(inConf, true);
		} else {
		   throw new CacheConfigurationNotFoundException("cache.configuration.notfound", JbossCacheManagerPluginName, configurationUri);
		}
	   }
	   return cache;
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, JbossCacheManagerPluginName,
		   StringHelper.isEmpty(configurationUri) ? DefaultCacheConfiguration : configurationUri);
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
   @Review(comment = "http://www.redhat.com/docs/manuals/jboss/jboss-eap-4.3/doc/cache/Tree_Cache_Guide/Management_Information-JBoss_Cache_Statistics.html", category = ReviewCategoryEnum.Todo)
   public Map<String, Object> dumpStatistics(final String cacheName) {
	return new LinkedHashMap<String, Object>();
   }

}
