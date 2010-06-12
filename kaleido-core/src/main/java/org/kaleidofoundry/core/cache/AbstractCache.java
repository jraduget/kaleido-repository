package org.kaleidofoundry.core.cache;

import java.io.Serializable;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * Abstract cache implementation
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
public abstract class AbstractCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

   /**
    * <code>true</code> if cache have been destroyed, <code>false</code> otherwise <br/>
    * Can be useful when cache instance is stored in a class field...
    */
   boolean hasBeenDestroy = false;

   /**
    * consistency checking of arguments is done retrospectively
    * 
    * @param key
    * @return
    */
   protected abstract V doGet(@NotNull K key);

   /**
    * consistency checking of arguments is done retrospectively
    * 
    * @param key
    */
   protected abstract void doPut(@NotNull K key, @NotNull V entity);

   /**
    * consistency checking of arguments is done retrospectively
    * 
    * @param key
    * @return <code>true</code> if found and removed, <code>false</code> otherwise
    */
   protected abstract boolean doRemove(@NotNull K key);

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#get(java.io.Serializable)
    */
   @Override
   public final V get(@NotNull final K key) {
	checkCacheState();
	return doGet(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#put(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public final void put(@NotNull final K key, @NotNull final V entity) {
	checkCacheState();
	doPut(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#remove(java.io.Serializable)
    */
   @Override
   public final boolean remove(@NotNull final K key) {
	checkCacheState();
	return doRemove(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#hasBeenDestroy()
    */
   public boolean hasBeenDestroy() {
	return hasBeenDestroy;
   }

   /**
    * @throws IllegalStateException
    */
   void checkCacheState() throws IllegalStateException {
	if (hasBeenDestroy) { throw new IllegalStateException("This cache was destroyed, you can not access it"); }
   }
}
