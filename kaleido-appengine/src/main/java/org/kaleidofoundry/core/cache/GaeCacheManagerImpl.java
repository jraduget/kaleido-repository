/*  
 * Copyright 2008-2016 the original author or authors 
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

import static org.kaleidofoundry.core.cache.CacheConstants.GaeCacheManagerPluginName;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.GaeCacheExpiration;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.GaeCacheExpirationDelta;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.GaeThrowOnPutFailure;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheStatistics;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

/**
 * Google application engine caching manager
 * 
 * https://developers.google.com/appengine/docs/java/memcache/overview
 * 
 * @author jraduget
 */
@Declare(value = GaeCacheManagerPluginName)
public class GaeCacheManagerImpl extends AbstractCacheManager {

   final CacheFactory cacheFactory;

   /**
    * @param context
    */
   public GaeCacheManagerImpl(@NotNull RuntimeContext<CacheManager> context) {
	super(context);
	try {
	   cacheFactory = net.sf.jsr107cache.CacheManager.getInstance().getCacheFactory();
	} catch (CacheException cae) {
	   throw new CacheConfigurationException("cache.configuration.error", cae, getMetaInformations(), getCurrentConfiguration());
	}
   }

   /**
    * @param configuration
    * @param context
    */
   public GaeCacheManagerImpl(String configuration, @NotNull RuntimeContext<CacheManager> context) {
	super(configuration, context);
	try {
	   cacheFactory = net.sf.jsr107cache.CacheManager.getInstance().getCacheFactory();
	} catch (CacheException cae) {
	   throw new CacheConfigurationException("cache.configuration.error", cae, getMetaInformations(), getCurrentConfiguration());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.String, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @SuppressWarnings("unchecked")
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull String name, @NotNull RuntimeContext<Cache<K, V>> context) {
	Cache<K, V> cache = cachesByName.get(name);
	if (cache == null) {
	   cache = new GaeCacheImpl<K, V>(name, this, context);
	   // registered it to cache manager
	   cachesByName.put(name, cache);
	}
	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDefaultConfiguration()
    */
   @Override
   public String getDefaultConfiguration() {	
	return "container configuration";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getMetaInformations()
    */
   @Override
   public String getMetaInformations() {	
	return "google app engine - jcache / memcache";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#destroy(java.lang.String)
    */
   @Override
   public void destroy(@NotNull String cacheName) {
	final Cache<?, ?> cache = cachesByName.get(cacheName);
	if (cache != null) {
	   ((GaeCacheImpl<?, ?>) cache).destroy();
	   cachesByName.remove(cacheName);
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(@NotNull String cacheName) {
	
	final Cache<?, ?> cache = getCache(cacheName);

	if (cache != null) {
	   final net.sf.jsr107cache.Cache jcache = ((GaeCacheImpl<?, ?>) cache).getCache();
	   final CacheStatistics cacheStats = jcache.getCacheStatistics();
	   	   
	   final Map<String, Object> lcacheStats = new LinkedHashMap<String, Object>();
	   lcacheStats.put("CacheSize", cacheStats.getObjectCount());
	   lcacheStats.put("MemoryHits", cacheStats.getCacheHits());
	   lcacheStats.put("CacheMisses", cacheStats.getCacheMisses());
	   lcacheStats.put("StatisticAccuracy", cacheStats.getStatisticsAccuracy());
	   return lcacheStats;
	} else {
	   return null;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#clearStatistics(java.lang.String)
    */
   @Override
   public void clearStatistics(@NotNull String cacheName) {	
	final Cache<?, ?> cache = getCache(cacheName);
	if (cache != null) {
	   final net.sf.jsr107cache.Cache jcache = ((GaeCacheImpl<?, ?>) cache).getCache();
	   final CacheStatistics cacheStats = jcache.getCacheStatistics();
	   cacheStats.clearStatistics();
	} 
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return net.sf.jsr107cache.CacheManager.getInstance();
   }
   
   
   /**
    * @param name
    * @return cache provider
    */
   protected com.google.appengine.api.memcache.jsr107cache.GCache createCache(final String name) {

	traceCacheCreation(name);
	
	try {
	   // the internal  instance that will be created
	   final com.google.appengine.api.memcache.jsr107cache.GCache cache;

	   Map<String, Object> cacheParam = new HashMap<String, Object>();
	   if (context.getProperty(GaeCacheExpiration) != null) {
		cacheParam.put(GCacheFactory.EXPIRATION, context.getDate(GaeCacheExpiration));		
	   }
	   if (context.getProperty(GaeCacheExpirationDelta) != null) {
		cacheParam.put(GCacheFactory.EXPIRATION_DELTA_MILLIS, context.getInteger(GaeCacheExpirationDelta));
	   }
	   if (context.getProperty(GaeThrowOnPutFailure) != null) {
		cacheParam.put(GCacheFactory.THROW_ON_PUT_FAILURE, context.getBoolean(GaeThrowOnPutFailure));
	   }
	   
	   
	   cache = (com.google.appengine.api.memcache.jsr107cache.GCache) cacheFactory.createCache(cacheParam);

	   if (cache == null) {
		throw new CacheDefinitionNotFoundException("cache.configuration.notCachefound", name, getCurrentConfiguration());
	   } else {
		return cache;
	   }

	} catch (final CacheException gaece) {
	   throw new CacheConfigurationException("cache.configuration.error", gaece, GaeCacheManagerPluginName, getCurrentConfiguration());
	}
   }

}
