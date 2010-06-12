package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.config.ConfigurationException;
import org.kaleidofoundry.core.util.Registry;

/**
 * Jboss3x factory implementation
 * 
 * @see CacheFactory
 * @author Jerome RADUGET
 */
class Jboss32xCacheFactoryImpl<K extends Serializable, V extends Serializable> extends org.kaleidofoundry.core.cache.CacheFactory<K, V> {

   /** Local cache of jboss cache instances */
   private static final transient Registry<Cache<?, ?>> CACHE_REGISTRY = new Registry<Cache<?, ?>>();

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getImplementation()
    */
   @Override
   public CacheEnum getImplementation() {
	return CacheEnum.JBOSS_3X;
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
	   final org.jboss.cache.Cache<K, V> jbossCache = createCache(name, configurationFile);
	   cache = new Jboss32xCacheImpl<K, V>(name, jbossCache);
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
	   ((Jboss32xCacheImpl<K, V>) cache).destroy();
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
    * @param cache name
    * @param configurationUri url to the configuration file of the cache.<br/>
    *           This file have to be in classpath or on extern file system. If null is specified, default / fail-safe
    *           cache configuration implementation will be loaded
    * @return
    */
   org.jboss.cache.Cache<K, V> createCache(final String name, final String configurationUri) {

	org.jboss.cache.CacheFactory<K, V> cacheFactory;
	org.jboss.cache.Cache<K, V> cache;

	try {

	   cacheFactory = new DefaultCacheFactory<K, V>();

	   if (configurationUri == null) {
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", "jboss", CacheEnum.JBOSS_3X.getDefaultConfiguration(), name));
		cache = cacheFactory.createCache(true);
	   } else {
		final InputStream inConf = getConfiguration(configurationUri);

		if (inConf != null) {
		   LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", "coherence", configurationUri, name));
		   cache = cacheFactory.createCache(inConf, true);
		} else {

		   throw new CacheConfigurationNotFoundException("cache.configuration.notfound", getImplementation().toString(), getImplementation()
			   .getDefaultConfiguration());
		}
	   }
	   return cache;
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, getImplementation().toString(), getImplementation().getDefaultConfiguration());
	}
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
	// TODO dumpStatistics for jboss
	// http://www.redhat.com/docs/manuals/jboss/jboss-eap-4.3/doc/cache/Tree_Cache_Guide/Management_Information-JBoss_Cache_Statistics.html
	return new LinkedHashMap<String, Object>();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCacheNames()
    */
   @Override
   public Set<String> getCacheNames() {
	return CACHE_REGISTRY.keySet();
   }

}
