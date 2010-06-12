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
    * load implementation to implements<br/>
    * no need to handle synchronized here, it is done done earlier
    */
   protected abstract R doLoad() throws StoreException;

   /**
    * unload implementation to implements<br/>
    * no need to handle synchronized here, it is done done earlier
    */
   protected abstract void doUnload() throws StoreException;

   /**
    * persists current resouce instance
    * 
    * @return current resource instance
    */
   protected abstract R doStore() throws StoreException;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Store#load()
    */
   @Override
   public synchronized R load() throws StoreException {
	R content = doLoad();
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
	R content = load();
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
