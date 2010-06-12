/*
 * $License$
 */
package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.InfinispanCachePluginName;

import java.io.Serializable;
import java.util.Set;

import org.infinispan.Cache;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@DeclarePlugin(InfinispanCachePluginName)
public class Infinispan4xCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements
	org.kaleidofoundry.core.cache.Cache<K, V> {

   private final String name;
   private final Cache<K, V> cache;

   /**
    * @param c class of the cache
    * @param cache infinispan cache instanciate via factory
    */
   Infinispan4xCacheImpl(final Class<V> c, final Cache<K, V> cache) {
	this(c != null ? c.getName() : null, cache);
   }

   /**
    * @param name name of the cache
    */
   Infinispan4xCacheImpl(final String name, final Cache<K, V> cache) {

	if (name == null) throw new IllegalArgumentException("cache name argument is null");
	if (cache == null) throw new IllegalArgumentException("cache argument is null");

	this.cache = cache;
	this.name = name;

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getName()
    */
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @Override
   public V doGet(K key) {
	return cache.get(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(K key, V entity) {
	cache.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#remove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(K key) {
	return cache.remove(key, cache.get(key));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @Override
   public Set<K> keys() {
	return cache.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	cache.clear();

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return cache.size();
   }

   /**
    * @return infinispan internal cache
    */
   protected Cache<K, V> getInfinispanCache() {
	return cache;
   }

}
