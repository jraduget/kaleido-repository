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

import static org.kaleidofoundry.core.cache.CacheConstants.EhCacheManagerPluginName;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;

import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EhCache factory implementation <br/>
 * <br/>
 * http://ehcache.org/documentation/configuration.html <br/>
 * Cache statistics are disabled in this version for performance reason (prior to 1.7.x) <br/>
 * With EhCache 2.x you can disable statistics cache by configuration :)
 * 
 * @see CacheFactory
 * @author Jerome RADUGET
 */
@Declare(value = EhCacheManagerPluginName)
class EhCache1xManagerImpl extends AbstractCacheManager {

   /** internal logger */
   private static final Logger LOGGER = LoggerFactory.getLogger(EhCache1xManagerImpl.class);

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = "ehcache.xml";

   // internal cache manager
   private final net.sf.ehcache.CacheManager cacheManager;

   /**
    * @param context
    */
   public EhCache1xManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	this(null, context);
   }

   /**
    * @param configuration url to the configuration file of the cache.<br/>
    *           This file have to be in classpath or on external file system. If null is specified, default / fail-safe
    *           cache configuration implementation will be loaded
    * @param context
    */
   public EhCache1xManagerImpl(final String configuration, final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	super(configuration, context);

	final String configurationUri = getCurrentConfiguration();

	try {
	   if (StringHelper.isEmpty(configurationUri)) {
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", getMetaInformations(), DefaultCacheConfiguration));
		cacheManager = new CacheManager();
		if (cacheManager == null) { throw newCacheException("cache.configuration.notfound", new String[] { EhCacheManagerPluginName,
			DefaultCacheConfiguration }); }
	   } else {

		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", getMetaInformations(), configurationUri));
		final InputStream inConf = getConfiguration(configurationUri);
		if (inConf != null) {
		   cacheManager = new CacheManager(inConf);
		} else {
		   cacheManager = new CacheManager(configurationUri);
		}

		if (cacheManager == null) { throw newCacheException("cache.configuration.notfound", new String[] { EhCacheManagerPluginName, configurationUri }); }

	   }
	} catch (final net.sf.ehcache.CacheException ehce) {
	   throw newCacheConfigurationException("cache.configuration.error", ehce, EhCacheManagerPluginName, getCurrentConfiguration());
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
	return "ehcache-1.x - [1.2.x -> 2.1.x]";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCache(java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(final String name) {

	Cache<K, V> cache = cachesByName.get(name);

	if (cache == null) {
	   final net.sf.ehcache.Cache ehCache = createCache(name);
	   cache = new EhCache1xImpl<K, V>(name, ehCache);
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
	   // custom ehcache destroy
	   ((EhCache1xImpl<?, ?>) cache).destroy();
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

	// destroy all cache instances
	for (final String name : cachesByName.keySet()) {
	   LOGGER.info(CacheMessageBundle.getMessage("cache.destroy.info", getMetaInformations(), name));
	   destroy(name);
	}

	// ehcache cacheManager shutdown
	if (cacheManager != null) {
	   cacheManager.shutdown();
	}

	// unregister cacheManager
	CacheManagerProvider.getRegistry().remove(CacheManagerProvider.getCacheManagerId(DefaultCacheProviderEnum.ehCache1x.name(), getCurrentConfiguration()));
   }

   /**
    * @param name
    * @return cache provider
    */
   protected net.sf.ehcache.Cache createCache(final String name) {

	net.sf.ehcache.Cache cache;

	try {

	   LOGGER.info(CacheMessageBundle.getMessage("cache.create.default", getMetaInformations(), getCurrentConfiguration(), name));
	   cache = cacheManager.getCache(name);

	   if (cache == null) {
		throw new CacheDefinitionNotFoundException("cache.configuration.notCachefound", name);
	   } else {
		cache.setStatisticsEnabled(false); // performance decrease a lot with statistics (<= 1.7.x) :(
		return cache;
	   }

	} catch (final net.sf.ehcache.CacheException ehce) {
	   throw newCacheConfigurationException("cache.configuration.error", ehce, EhCacheManagerPluginName, getCurrentConfiguration());
	}
   }

   /**
    * @param code
    * @param args
    * @return cache provider exception converter
    */
   static CacheConfigurationException newCacheException(final String code, final String... args) {
	return newCacheConfigurationException(code, null, args);
   }

   /**
    * @param code
    * @param th
    * @param args
    * @return cache provider exception converter
    */
   static CacheConfigurationException newCacheConfigurationException(final String code, final Throwable th, final String... args) {
	if (th == null) {
	   return new CacheConfigurationException(code, args);
	} else {
	   final Throwable cause = th.getCause();
	   if (cause != null && cause instanceof FileNotFoundException) {
		return new CacheConfigurationNotFoundException(code, th, args);
	   } else {
		return new CacheConfigurationException(code, th, args);
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#clearStatistics(java.lang.String)
    */
   @Override
   public void clearStatistics(final String cacheName) {
	final Cache<?, ?> cache = getCache(cacheName);
	if (cache != null) {
	   final net.sf.ehcache.Cache ehcache = ((EhCache1xImpl<?, ?>) cache).getCache();
	   ehcache.clearStatistics();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(final String cacheName) {

	final Cache<?, ?> cache = getCache(cacheName);

	if (cache != null) {
	   final net.sf.ehcache.Cache ehcache = ((EhCache1xImpl<?, ?>) cache).getCache();
	   final Map<String, Object> lcacheStats = new LinkedHashMap<String, Object>();
	   lcacheStats.put("CacheSize", ehcache.getSize());
	   lcacheStats.put("MemoryStoreSize", ehcache.getStatistics().getMemoryStoreObjectCount());
	   lcacheStats.put("DiskStoreSize", ehcache.getStatistics().getDiskStoreObjectCount());
	   lcacheStats.put("MemoryHits", ehcache.getStatistics().getInMemoryHits());
	   lcacheStats.put("DiskHits", ehcache.getStatistics().getOnDiskHits());
	   return lcacheStats;
	} else {
	   return null;
	}
   }

}
