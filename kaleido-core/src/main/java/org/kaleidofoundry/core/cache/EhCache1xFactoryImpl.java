package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;

import org.kaleidofoundry.core.util.Registry;

/**
 * EhCache factory implementation <br/>
 * <br/> {@link}http://ehcache.org/documentation/configuration.html <br/>
 * Cache statistics are disabled in this version for performance reason (prior to 1.7.x) <br/>
 * With EhCache 2.x you can disable statistics cache by configuration :)
 * 
 * @see CacheFactory
 * @author Jerome RADUGET
 */
class EhCache1xFactoryImpl<K extends Serializable, V extends Serializable> extends CacheFactory<K, V> {

   /** Local registry of ehcache instances */
   private static final transient Registry<Cache<?, ?>> CACHE_REGISTRY = new Registry<Cache<?, ?>>();

   /** Local registry of ehcache cacheManagers instances */
   private static final transient Registry<CacheManager> CACHEMANAGER_REGISTRY = new Registry<CacheManager>();

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getImplementation()
    */
   @Override
   public CacheEnum getImplementation() {
	return CacheEnum.EHCACHE_1X;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCache(java.lang.String, java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Cache<K, V> getCache(final String name, final String configurationFile) {

	Cache cache = CACHE_REGISTRY.get(name);

	if (cache == null) {
	   final net.sf.ehcache.Cache ehCache = createCache(name, configurationFile);
	   cache = new EhCache1xImpl<K, V>(name, ehCache);
	   CACHE_REGISTRY.put(name, cache);
	}

	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public void destroy(final String cacheName) {
	final Cache<K, V> cache = (Cache<K, V>) CACHE_REGISTRY.get(cacheName);
	if (cache != null) {
	   // custom ehcache destroy
	   ((EhCache1xImpl<K, V>) cache).destroy();
	   CACHE_REGISTRY.remove(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroyAll()
    */
   @Override
   public void destroyAll() {

	// destroy all cache instances
	for (final String name : CACHE_REGISTRY.keySet()) {
	   LOGGER.info("Destroying '{}' cache '{}' ...", getImplementation().getCode(), name);
	   destroy(name);
	}

	// ehcache cacheManager shutdown
	for (final CacheManager cacheManager : CACHEMANAGER_REGISTRY.values()) {
	   cacheManager.shutdown();
	}
	CACHEMANAGER_REGISTRY.clear();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCacheNames()
    */
   @Override
   public Set<String> getCacheNames() {
	return CACHE_REGISTRY.keySet();
   }

   /**
    * @param name
    * @param configurationUri url to the configuration file of the cache.<br/>
    *           This file have to be in classpath or on extern file system. If null is specified, default / fail-safe
    *           cache configuration implementation will be loaded
    * @return
    */
   net.sf.ehcache.Cache createCache(final String name, final String configurationUri) {

	net.sf.ehcache.CacheManager cacheManager;
	net.sf.ehcache.Cache cache;

	try {
	   if (configurationUri == null) {
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", "ehcache", CacheEnum.EHCACHE_1X.getDefaultConfiguration(), name));
		cacheManager = CacheManager.getInstance();
		CACHEMANAGER_REGISTRY.put("__defaultInstance__", cacheManager);
	   } else {
		cacheManager = CACHEMANAGER_REGISTRY.get(configurationUri);

		if (cacheManager == null) {
		   LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", "ehcache", configurationUri, name));
		   final InputStream inConf = getConfiguration(configurationUri);
		   if (inConf != null) {
			cacheManager = CacheManager.create(inConf);
		   } else {
			cacheManager = CacheManager.create(configurationUri);
		   }
		   CACHEMANAGER_REGISTRY.put(configurationUri, cacheManager);
		}
	   }

	   if (cacheManager == null) { throw newCacheException("cache.configuration.notfound", new String[] { getImplementation().toString(),
		   getImplementation().getDefaultConfiguration() }); }

	   cache = cacheManager.getCache(name);

	   if (cache == null) {
		throw new CacheDefinitionNotFoundException("cache.configuration.notCachefound", name);
	   } else {
		cache.setStatisticsEnabled(false); // perf with statistics :(
		return cache;
	   }

	} catch (final net.sf.ehcache.CacheException ehce) {
	   throw newCacheConfigurationException("cache.configuration.error", ehce, getImplementation().toString(), getImplementation().getDefaultConfiguration());
	}
   }

   /**
    * @param code
    * @param args
    * @return
    */
   CacheConfigurationException newCacheException(final String code, final String... args) {
	return newCacheConfigurationException(code, null, args);
   }

   /**
    * @param code
    * @param th
    * @param args
    * @return
    */
   CacheConfigurationException newCacheConfigurationException(final String code, final Throwable th, final String... args) {
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
	Cache<?, ?> cache = getCache(cacheName);
	if (cache != null) {
	   net.sf.ehcache.Cache ehcache = ((EhCache1xImpl<?, ?>) cache).getCache();
	   ehcache.clearStatistics();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(final String cacheName) {

	Cache<?, ?> cache = getCache(cacheName);

	if (cache != null) {
	   net.sf.ehcache.Cache ehcache = ((EhCache1xImpl<?, ?>) cache).getCache();
	   Map<String, Object> lcacheStats = new LinkedHashMap<String, Object>();
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
