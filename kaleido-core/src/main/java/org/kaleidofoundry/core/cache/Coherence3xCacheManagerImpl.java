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

import static org.kaleidofoundry.core.cache.CacheConstants.CoherenceCacheManagerPluginName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.Classloader;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.WrapperException;

/**
 * Coherence factory implementation <br/>
 * http://coherence.oracle.com/display/COH35UG/Sample+Cache+Configurations
 * -Dtangosol.coherence.cacheconfig=coherence-cache-config.xml
 * 
 * @see CacheFactory
 * @author Jerome RADUGET
 */
@Declare(value = CoherenceCacheManagerPluginName)
public class Coherence3xCacheManagerImpl extends AbstractCacheManager {

   /** internal logger */
   private static final Logger LOGGER = LoggerFactory.getLogger(Coherence3xCacheManagerImpl.class);

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = "coherence-cache-config.xml";

   // internal cache factory configuration
   private final ConfigurableCacheFactory configurableCacheFactory;

   /**
    * @param context
    */
   public Coherence3xCacheManagerImpl(final RuntimeContext<CacheManager> context) {
	this(context.getString(FileStoreUri), context);
   }

   /**
    * @param configuration override the context configuration file (if defined)
    * @param context
    * @throws IllegalArgumentException if configuration is an invalid uri
    */
   public Coherence3xCacheManagerImpl(final String configuration, final RuntimeContext<CacheManager> context) {
	super(configuration, context);

	if (StringHelper.isEmpty(configuration)) {
	   LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", getMetaInformations(), DefaultCacheConfiguration));
	   configurableCacheFactory = null;
	} else {
	   try {
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", getMetaInformations(), configuration));
		final URI resourceUri = URI.create(singleFileStore.getResourceBinding());
		configurableCacheFactory = new DefaultConfigurableCacheFactory(resourceUri.getPath());
		com.tangosol.net.CacheFactory.setConfigurableCacheFactory(configurableCacheFactory);
	   } catch (final WrapperException wre) {

		if (wre.getCause() != null && wre.getCause() instanceof IOException) {
		   final String wrappedMsg = wre.getCause().getMessage();
		   if (wrappedMsg.contains("onfiguration is missing")) { throw new CacheConfigurationNotFoundException("cache.configuration.notfound",
			   CoherenceCacheManagerPluginName, configuration); }
		}
		if (wre.getCause().getCause() != null && wre.getCause().getCause() instanceof IOException) {
		   final String wrappedMsg = wre.getCause().getCause().getMessage();
		   if (wrappedMsg.contains("onfiguration is missing")) { throw new CacheConfigurationNotFoundException("cache.configuration.notfound",
			   CoherenceCacheManagerPluginName, configuration); }
		}

		throw new CacheConfigurationException("cache.configuration.error", wre, CoherenceCacheManagerPluginName, configuration);
	   }
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
    * @see org.kaleidofoundry.core.cache.AbstractCacheManager#getMetaInformations()
    */
   @Override
   public String getMetaInformations() {
	return "coherence-3.x - [3.0.x -> 3.6.x]";
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
	   // create cache instance
	   cache = new Coherence3xCacheImpl(name, this, context);
	   // registered it to cache manager
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
	final Coherence3xCacheImpl<?, ?> cache = (Coherence3xCacheImpl) cachesByName.get(cacheName);
	if (cache != null) {
	   cache.destroy();
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
	   LOGGER.info(CacheMessageBundle.getMessage("cache.destroy.info", getMetaInformations(), name));
	   destroy(name);
	}
	com.tangosol.net.CacheFactory.shutdown();

	// unregister cacheManager
	CacheManagerProvider.getRegistry().remove(CacheManagerProvider.getCacheManagerId(DefaultCacheProviderEnum.coherence3x.name(), getCurrentConfiguration()));
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

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return configurableCacheFactory;
   }

   /**
    * create internal provider cache, using cache manager configuration
    * 
    * @param name
    * @return cache provider
    */
   protected NamedCache createCache(final String name) {

	LOGGER.info(CacheMessageBundle.getMessage("cache.create.default", getMetaInformations(), getCurrentConfiguration(), name));

	try {

	   if (!StringHelper.isEmpty(context.getString(Classloader))) {
		try {
		   return CacheFactory.getCache(name, Class.forName(Classloader).getClassLoader());
		} catch (final ClassNotFoundException cnfe) {
		   throw new CacheConfigurationException("cache.classloader.notfound", cnfe, context.getString(Classloader));
		}
	   } else {
		return com.tangosol.net.CacheFactory.getCache(name);
	   }

	} catch (final WrapperException wre) {
	   throw new CacheConfigurationException("cache.configuration.error", wre, CoherenceCacheManagerPluginName, getCurrentConfiguration());
	}
   }
}
