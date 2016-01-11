/*
 * Copyright 2008-2016 the original author or authors
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

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Abstract cache implementation
 * 
 * @author jraduget
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
   private boolean hasBeenDestroy = false;

   /**
    * @param context
    */
   public AbstractCache(@NotNull final RuntimeContext<Cache<K, V>> context) {
	this(context.getString(CacheContextBuilder.CacheName), context);
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
    * @param name
    */
   public AbstractCache(String name, @NotNull final RuntimeContext<Cache<K, V>> context) {
	if (StringHelper.isEmpty(name)) {
	   name = context.getName();
	}
	if (StringHelper.isEmpty(name)) { throw new EmptyContextParameterException(CacheContextBuilder.CacheName, context); }
	this.name = name;
	this.context = context;
   }

   /**
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   AbstractCache() {
	this.context = null;
	this.name = null;
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
    * @see org.kaleidofoundry.core.cache.Cache#containsKey(java.io.Serializable)
    */
   @Override
   public boolean containsKey(final K key) {
	return keys().contains(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#hasBeenDestroy()
    */
   @Override
   public boolean hasBeenDestroy() {
	return hasBeenDestroy;
   }

   /**
    * Stop and destroy cache instance
    */
   void destroy() {
	hasBeenDestroy = true;
   }

   /**
    * @throws IllegalStateException
    */
   void checkCacheState() throws IllegalStateException {
	if (hasBeenDestroy) { throw new IllegalStateException(InternalBundleHelper.CacheMessageBundle.getMessage("cache.destroy.access", getName())); }
   }

}
