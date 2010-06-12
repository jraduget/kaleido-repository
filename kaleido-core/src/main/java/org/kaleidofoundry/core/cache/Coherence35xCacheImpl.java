package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.CoherenceCachePluginName;

import java.io.Serializable;
import java.util.Set;

import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;

/**
 * Coherence {@link org.kaleidofoundry.core.cache.Cache} implementation
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@DeclarePlugin(CoherenceCachePluginName)
public class Coherence35xCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements Cache<K, V> {

   private final String name;
   private final NamedCache namedCache;

   /**
    * @param c class of the cache
    * @param coherence cache instanciate via factory
    */
   Coherence35xCacheImpl(final Class<V> c, final NamedCache cache) {
	this(c != null ? c.getName() : null, cache);
   }

   /**
    * @param name name of the cache
    * @param coherence cache instanciate via factory
    */
   Coherence35xCacheImpl(final String name, final NamedCache cache) {

	if (name == null) { throw new IllegalArgumentException("cache name argument is null"); }
	if (cache == null) { throw new IllegalArgumentException("cache argument is null"); }

	this.namedCache = cache;
	this.name = name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getName()
    */
   @Override
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @SuppressWarnings("unchecked")
   @Override
   public V doGet(final K key) {
	return (V) namedCache.get(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	namedCache.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final K key) {
	final boolean result = namedCache.containsKey(key);
	namedCache.remove(key);
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Set<K> keys() {
	return namedCache.keySet();
   }

   /**
    * @param filter
    * @return keys set return by the filter
    */
   @SuppressWarnings("unchecked")
   public Set<K> keySet(final Filter filter) {
	return namedCache.keySet(filter);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	namedCache.clear();

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return namedCache.size();
   }

   /**
    * Stop and destroy cache instance
    */
   void destroy() {
	com.tangosol.net.CacheFactory.destroyCache(namedCache);
	hasBeenDestroy = true;
   }

   /**
    * @return internal coherence {@link NamedCache}
    */
   protected NamedCache getNamedCache() {
	return namedCache;
   }

}