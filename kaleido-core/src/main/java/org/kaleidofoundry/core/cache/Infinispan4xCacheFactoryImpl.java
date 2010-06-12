package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.infinispan.config.ConfigurationException;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.kaleidofoundry.core.util.Registry;

/**
 * Jboss3x factory implementation
 * 
 * @see CacheFactory
 * @author Jerome RADUGET
 */
class Infinispan4xCacheFactoryImpl<K extends Serializable, V extends Serializable> extends org.kaleidofoundry.core.cache.CacheFactory<K, V> {

   /** Local cache of infispan cache instances */
   private static final transient Registry<Cache<?, ?>> CACHE_REGISTRY = new Registry<Cache<?, ?>>();

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getImplementation()
    */
   @Override
   public CacheEnum getImplementation() {
	return CacheEnum.INFINISPAN_4X;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCache(java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Cache<K, V> getCache(final String name, final String configurationFile) {

	Cache<K, V> cache = (Cache<K, V>) CACHE_REGISTRY.get(name);

	if (cache == null) {
	   final org.infinispan.Cache<K, V> jbossCache = createCache(configurationFile, name);
	   cache = new Infinispan4xCacheImpl(name, jbossCache);
	   CACHE_REGISTRY.put(name, cache);
	}

	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public void destroy(final String cacheName) {
	final Cache<K, V> cache = (Cache<K, V>) CACHE_REGISTRY.get(cacheName);
	if (cache != null) {
	   ((Infinispan4xCacheImpl<K, V>) cache).getInfinispanCache().getCacheManager().stop();
	   ((Infinispan4xCacheImpl<K, V>) cache).hasBeenDestroy = true;
	   CACHE_REGISTRY.remove(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroyAll()
    */
   @Override
   public void destroyAll() {
	for (final String name : CACHE_REGISTRY.keySet()) {
	   LOGGER.info("Destroying '{}' cache '{}' ...", getImplementation().getCode(), name);
	   destroy(name);
	}
   }

   /**
    * @param configurationUri url to the configuration file of the cache.<br/>
    *           This file have to be in classpath or on extern file system. If null is specified, default / fail-safe
    *           cache configuration implementation will be loaded
    * @return
    */
   org.infinispan.Cache<K, V> createCache(final String configurationUri, final String name) {

	CacheManager cacheManager;
	org.infinispan.Cache<K, V> cache;

	try {

	   if (configurationUri == null) {
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", "infinispan", CacheEnum.INFINISPAN_4X.getDefaultConfiguration(), name));
		cacheManager = new DefaultCacheManager();
		cache = cacheManager.getCache(name);
	   } else {
		final InputStream inConf = getConfiguration(configurationUri);
		if (inConf != null) {
		   LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", "infinispan", configurationUri, name));
		   cacheManager = new DefaultCacheManager(inConf);
		   cache = cacheManager.getCache(name);
		} else {
		   throw new CacheConfigurationNotFoundException("cache.configuration.notfound", getImplementation().toString(), getImplementation()
			   .getDefaultConfiguration());
		}
	   }
	   return cache;
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, getImplementation().toString(), getImplementation().getDefaultConfiguration());
	} catch (IOException ioe) {
	   throw new CacheConfigurationException("cache.configuration.error", ioe, getImplementation().toString(), getImplementation().getDefaultConfiguration());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCacheNames()
    */
   @Override
   public Set<String> getCacheNames() {
	return CACHE_REGISTRY.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#clearStatistics(java.lang.String)
    */
   @Override
   public void clearStatistics(final String cacheName) {
	// TODO clearStatistics for infinispan
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(final String cacheName) {
	// TODO dumpStatistics for infinispan
	return new LinkedHashMap<String, Object>();
   }

}
