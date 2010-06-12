package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.CachePluginName;

import java.io.Serializable;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * Cache common interface
 * 
 * @author Jerome RADUGET
 * @param <K> Type of cache keys
 * @param <V> Type of cache values
 */
@DeclarePlugin(CachePluginName)
public interface Cache<K extends Serializable, V extends Serializable> {

   /**
    * @return cache name
    */
   @NotNull
   String getName();

   /**
    * @param key
    * @return get entry mapping to the key parameter
    */
   V get(@NotNull K key);

   /**
    * @param key key of the entity to put in cache
    * @param entity entity to put in cache
    */
   void put(@NotNull K key, @NotNull V entity);

   /**
    * @param key
    * @return <code>true</code> if found and removed, <code>false</code> otherwise
    */
   boolean remove(@NotNull K key);

   /**
    * remove all entries from cache
    */
   void removeAll();

   /**
    * @return all cache keys items
    */
   @NotNull
   Set<K> keys();

   /**
    * @return entries count of the cache
    */
   int size();

   /**
    * @return <code>true</code> if cache have been destroyed, <code>false</code> otherwise <br/>
    *         Can be useful when cache instance is stored in a class field...
    */
   boolean hasBeenDestroy();
}
