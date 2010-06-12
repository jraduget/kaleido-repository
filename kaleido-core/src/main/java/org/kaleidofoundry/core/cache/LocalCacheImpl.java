package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.DefaultLocalPluginName;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * Local cache implementation<br/>
 * Implementation use internally a {@link ConcurrentHashMap} to store key / value
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@DeclarePlugin(DefaultLocalPluginName)
public class LocalCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

   private final ConcurrentMap<K, V> CacheableMap = new ConcurrentHashMap<K, V>();

   private final String name;

   /**
    * @param name cache name
    */
   public LocalCacheImpl(final String name) {
	super();
	this.name = name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @Override
   protected V doGet(final K key) {
	return CacheableMap.get(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   protected void doPut(final K key, final V entity) {
	CacheableMap.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   protected boolean doRemove(final K key) {
	boolean present = CacheableMap.get(key) != null;
	if (present) {
	   CacheableMap.remove(key);
	}
	return present;
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
   @Override
   public Set<K> keys() {
	return CacheableMap.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	CacheableMap.clear();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return CacheableMap.size();
   }

   /**
    * Stop and destroy cache instance
    */
   void destroy() {
	CacheableMap.clear();
	hasBeenDestroy = true;
   }
}
