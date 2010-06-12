package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.EhCachePluginName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * EhCache {@link org.kaleidofoundry.core.cache.Cache} implementation <br/>
 * Cache statistics are disabled in this version, due to performance reason (prior to 1.7.x) <br/>
 * With EhCache 2.x you can disable cache by configuration :)
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@DeclarePlugin(EhCachePluginName)
public class EhCache1xImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements org.kaleidofoundry.core.cache.Cache<K, V> {

   private final String name;
   private final Cache cache;

   /**
    * @param c class of the cache
    * @param cache
    */
   EhCache1xImpl(final Class<V> c, final Cache cache) {
	this(c != null ? c.getName() : null, cache);
   }

   /**
    * @param name name of the cache
    * @param cache
    */
   EhCache1xImpl(final String name, final Cache cache) {
	if (name == null) { throw new IllegalArgumentException("cache name argument is null"); }
	if (cache == null) { throw new IllegalArgumentException("cache argument is null"); }
	this.name = name;
	this.cache = cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @SuppressWarnings("unchecked")
   @Override
   public V doGet(final K id) {
	final Element elt = cache.getQuiet(id); // no stat, perf. decreaze :(
	return elt != null ? (V) elt.getObjectValue() : null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	cache.putQuiet(new Element(key, entity)); // no stat, perf. decreaze :(
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final Serializable id) {
	return cache.removeQuiet(id);
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
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Set<K> keys() {
	return new HashSet<K>(cache.getKeys());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	cache.removeAll();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return cache.getSize();
   }

   /**
    * Stop and destroy cache instance
    */
   void destroy() {
	hasBeenDestroy = true;
	// cache.dispose(); must be done by cacheManager
   }

   /**
    * @return the cache
    */
   protected Cache getCache() {
	return cache;
   }

}
