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
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
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
 * @see CacheManagerFactory
 * @author Jerome RADUGET
 */
@Declare(value = EhCacheManagerPluginName)
public class EhCacheManagerImpl extends AbstractCacheManager {

   /** internal logger */
   private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheManagerImpl.class);

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = "ehcache.xml";

   // internal ehCache cache manager
   final net.sf.ehcache.CacheManager ehCacheManager;

   /**
    * @param context
    */
   public EhCacheManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	this(context.getString(FileStoreUri), context);
   }

   /**
    * @param configuration url to the configuration file of the cache.<br/>
    *           This file have to be in classpath or on external file system. If null is specified, default / fail-safe
    *           cache configuration implementation will be loaded<br/>
    *           override the context configuration file (if defined)
    * @param context
    */
   public EhCacheManagerImpl(final String configuration, final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	super(configuration, context);

	try {
	   if (StringHelper.isEmpty(configuration)) {
		LOGGER.info(CacheMessageBundle.getMessage("cachemanager.loading.default", getMetaInformations(), DefaultCacheConfiguration));
		ehCacheManager = new CacheManager();
		if (ehCacheManager == null) { throw newCacheException("cache.configuration.notfound", new String[] { EhCacheManagerPluginName,
			DefaultCacheConfiguration }); }
	   } else {
		LOGGER.info(CacheMessageBundle.getMessage("cachemanager.loading.custom", getMetaInformations(), configuration));
		final InputStream inConf = getConfiguration(configuration);
		if (inConf != null) {
		   ehCacheManager = new CacheManager(inConf);
		} else {
		   ehCacheManager = new CacheManager(configuration);
		}

		if (ehCacheManager == null) { throw newCacheException("cache.configuration.notfound", new String[] { EhCacheManagerPluginName, configuration }); }

	   }
	} catch (final net.sf.ehcache.CacheException ehce) {
	   throw newCacheConfigurationException("cache.configuration.error", ehce, EhCacheManagerPluginName, getCurrentConfiguration());
	}

   }

   /**
    * @see AbstractCacheManager#AbstractCacheManager()
    */
   EhCacheManagerImpl() {
	super();
	ehCacheManager = null;
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
	return "ehcache-2.x (1.2.x -> 2.4.x)";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCache(java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(final String name, @NotNull final RuntimeContext<Cache<K, V>> context) {
	Cache<K, V> cache = cachesByName.get(name);
	if (cache == null) {
	   cache = new EhCacheImpl<K, V>(name, this, context);
	   // registered it to cache manager
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
	   // custom ehcache destroy
	   ((EhCacheImpl<?, ?>) cache).destroy();
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

	// destroy all cache instances
	for (final String name : cachesByName.keySet()) {
	   LOGGER.info(CacheMessageBundle.getMessage("cachemanager.destroy.info", name));
	   destroy(name);
	}

	// ehcache cacheManager shutdown
	if (ehCacheManager != null) {
	   ehCacheManager.shutdown();
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
	   final net.sf.ehcache.Cache ehcache = ((EhCacheImpl<?, ?>) cache).getCache();
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
	   final net.sf.ehcache.Cache ehcache = ((EhCacheImpl<?, ?>) cache).getCache();
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

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return ehCacheManager;
   }

   /**
    * @param name
    * @return cache provider
    */
   protected net.sf.ehcache.Cache createCache(final String name) {

	LOGGER.info(CacheMessageBundle.getMessage("cachemanager.create.default", name, getName()));

	try {
	   // the internal ehcache instance that will be created
	   final net.sf.ehcache.Cache cache;

	   cache = ehCacheManager.getCache(name);

	   if (cache == null) {
		throw new CacheDefinitionNotFoundException("cache.configuration.notCachefound", name, getCurrentConfiguration());
	   } else {
		// for version <= 1.7.x : performance decrease a lot with statistics
		// for version >= 2.x, statistics can be change in xml configuration
		cache.setStatisticsEnabled(false);

		return cache;
	   }

	} catch (final net.sf.ehcache.CacheException ehce) {
	   throw newCacheConfigurationException("cache.configuration.error", ehce, EhCacheManagerPluginName, getCurrentConfiguration());
	}
   }
}
