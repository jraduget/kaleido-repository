package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.util.Registry;

import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.WrapperException;

/**
 * Coherence factory implementation
 * 
 * @see CacheFactory
 * @author Jerome RADUGET
 */
class Coherence35xCacheFactoryImpl<K extends Serializable, V extends Serializable> extends CacheFactory<K, V> {

   /** Local registry of coherence cache instances */
   private static final transient Registry<Cache<?, ?>> CACHE_REGISTRY = new Registry<Cache<?, ?>>();

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getImplementation()
    */
   @Override
   public CacheEnum getImplementation() {
	return CacheEnum.COHERENCE_3X;
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
	   final NamedCache coherenceCache = createCache(name, configurationFile);
	   cache = new Coherence35xCacheImpl<K, V>(name, coherenceCache);
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
	final Coherence35xCacheImpl<K, V> cache = (Coherence35xCacheImpl<K, V>) CACHE_REGISTRY.get(cacheName);
	if (cache != null) {
	   cache.destroy();
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
	com.tangosol.net.CacheFactory.shutdown();
   }

   /**
    * @param name
    * @param configuration
    * @return
    */
   NamedCache createCache(final String name, final String configuration) {

	// http://coherence.oracle.com/display/COH35UG/Sample+Cache+Configurations
	// -Dtangosol.coherence.cacheconfig=coherence-cache-config.xml

	if (configuration == null || "".equals(configuration.trim())) {
	   LOGGER.info(CacheMessageBundle.getMessage("cache.loading.default", "coherence", CacheEnum.COHERENCE_3X.getDefaultConfiguration(), name));
	   return com.tangosol.net.CacheFactory.getCache(name);
	} else {

	   try {
		ConfigurableCacheFactory configurableCacheFactory = new DefaultConfigurableCacheFactory(configuration);
		com.tangosol.net.CacheFactory.setConfigurableCacheFactory(configurableCacheFactory);
		LOGGER.info(CacheMessageBundle.getMessage("cache.loading.custom", "coherence", configuration, name));
		return com.tangosol.net.CacheFactory.getCache(name);
	   } catch (WrapperException wre) {

		if (wre.getCause() != null && wre.getCause() instanceof IOException) {
		   String wrappedMsg = wre.getCause().getMessage();

		   if (wrappedMsg.contains("Configuration is missing")) { throw new CacheConfigurationNotFoundException("cache.configuration.notfound",
			   getImplementation().toString(), getImplementation().getDefaultConfiguration()); }
		}

		if (wre.getCause().getCause() != null && wre.getCause().getCause() instanceof IOException) {
		   String wrappedMsg = wre.getCause().getCause().getMessage();
		   if (wrappedMsg.contains("Configuration is missing")) { throw new CacheConfigurationNotFoundException("cache.configuration.notfound",
			   getImplementation().toString(), getImplementation().getDefaultConfiguration()); }
		}

		throw new CacheConfigurationException("cache.configuration.error", wre, getImplementation().toString(), getImplementation()
			.getDefaultConfiguration());

	   }
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
	// TODO clearStatistics for coherence
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(final String cacheName) {
	// TODO dumpStatistics for coherence
	return new LinkedHashMap<String, Object>();
   }
}
