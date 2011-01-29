/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.cache;

import java.io.Serializable;

import org.kaleidofoundry.core.context.ContextEmptyParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Abstract cache implementation
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
public abstract class AbstractCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

   /** cache runtime context */
   protected final RuntimeContext<Cache<K, V>> context;

   /** cache name */
   protected final String name;

   /**
    * <code>true</code> if cache have been destroyed, <code>false</code> otherwise <br/>
    * Can be useful when cache instance is stored in a class field...
    */
   boolean hasBeenDestroy = false;

   /**
    * @param context
    * @param name
    */
   public AbstractCache(@NotNull final String name, @NotNull final RuntimeContext<Cache<K, V>> context) {
	this.name = name;
	this.context = context;
   }

   /**
    * @param cl
    * @param context
    */
   public AbstractCache(@NotNull final Class<?> cl, @NotNull final RuntimeContext<Cache<K, V>> context) {
	this(cl.getName(), context);
   }

   /**
    * @param context
    */
   public AbstractCache(@NotNull final RuntimeContext<Cache<K, V>> context) {

	this.name = context.getString(CacheContextBuilder.CacheName);
	this.context = context;

	if (StringHelper.isEmpty(name)) { throw new ContextEmptyParameterException(CacheContextBuilder.CacheName, context); }
   }

   /**
    * consistency checking of arguments is done retrospectively
    * 
    * @param key
    * @return cache value
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
    * @see org.kaleidofoundry.core.cache.Cache#getName()
    */
   @Override
   public String getName() {
	return name;
   }

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
