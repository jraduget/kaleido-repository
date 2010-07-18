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
package org.kaleidofoundry.core.store;

import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Abstract single store, with right synchronized method and i18n
 * 
 * @author Jerome RADUGET
 * @param <B>
 * @param <R>
 */
@Immutable
@ThreadSafe
public abstract class AbstractSingleStore<B, R> implements SingleStore<B, R> {

   // resource binding information
   private final B resourceBinding;

   // load has been called
   private boolean loaded;

   /**
    * try to get resource, but do not load it at this step
    * 
    * @param resourceBinding resource binding informations
    */
   protected AbstractSingleStore(@NotNull final B resourceBinding) {
	this.resourceBinding = resourceBinding;
	this.loaded = false;
	init(resourceBinding);
   }

   /**
    * Custom init method, call at end of constructor.<br/>
    * If error must be throw, throws {@link Throwable} like {@link IllegalStateException}, ...
    * 
    * @param resourceBinding
    */
   protected abstract void init(final B resourceBinding);

   /**
    * @return Resource binding information
    */
   @NotNull
   public B getResourceBinding() {
	return resourceBinding;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Store#isLoaded()
    */
   public boolean isLoaded() {
	return loaded;
   }

   /**
    * load it<br/>
    * no need to handle synchronized here, it is done done earlier
    */
   protected abstract R doGet() throws StoreException;

   /**
    * unload it<br/>
    * no need to handle synchronized here, it is done done earlier
    */
   protected abstract void doUnload() throws StoreException;

   /**
    * persists current resource instance
    * 
    * @return current resource instance
    */
   protected abstract R doStore() throws StoreException;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Store#load()
    */
   @Override
   public synchronized R get() throws StoreException {
	R content = doGet();
	loaded = true;
	return content;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Store#unload()
    */
   @Override
   public synchronized void unload() throws StoreException {
	doUnload();
	loaded = false;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Store#reload()
    */
   @Override
   public synchronized R reload() throws StoreException {
	unload();
	R content = get();
	return content;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.SingleStore#store()
    */
   @Override
   public synchronized R store() throws StoreException {
	return doStore();
   }

}
