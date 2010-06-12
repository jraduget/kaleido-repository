package org.kaleidofoundry.core.cache;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.util.Registry;

/**
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
public class LocalCacheFactoryImpl<K extends Serializable, V extends Serializable> extends CacheFactory<K, V> {

   /** Local cache of jboss cache instances */
   private static final transient Registry<Cache<?, ?>> CACHE_REGISTRY = new Registry<Cache<?, ?>>();

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getImplementation()
    */
   @Override
   public CacheEnum getImplementation() {
	return CacheEnum.LOCAL;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCache(java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Cache<K, V> getCache(final String name, final String configurationUri) {

	LocalCacheImpl<K, V> cache = (LocalCacheImpl<K, V>) CACHE_REGISTRY.get(name);

	if (cache == null) {
	   cache = createCache(name, configurationUri);
	   CACHE_REGISTRY.put(name, cache);
	}

	return cache;
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
	// TODO clearStatistics for local usage
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public void destroy(final String cacheName) {
	final Cache<K, V> cache = (LocalCacheImpl<K, V>) CACHE_REGISTRY.get(cacheName);
	if (cache != null) {
	   ((LocalCacheImpl<K, V>) cache).destroy();
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

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(final String cacheName) {
	// TODO dumpStatistics for local usage
	return new LinkedHashMap<String, Object>();
   }

   /**
    * @param name
    * @param configurationUri
    * @return
    */
   LocalCacheImpl<K, V> createCache(final String name, final String configurationUri) {
	return new LocalCacheImpl<K, V>(name);
   }
}
