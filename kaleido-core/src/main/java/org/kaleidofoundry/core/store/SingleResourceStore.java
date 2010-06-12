package org.kaleidofoundry.core.store;

import java.net.URI;

import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * @author Jerome RADUGET
 */
@Immutable
@ThreadSafe
public class SingleResourceStore extends AbstractSingleStore<URI, ResourceHandler> {

   private final ResourceStore resourceStore;
   private ResourceHandler resourceHandler;

   /**
    * @param resourceUri
    * @param resourceStore
    */
   public SingleResourceStore(@NotNull final URI resourceUri, @NotNull final ResourceStore resourceStore) {
	super(resourceUri);
	// check that uri, match to resourceStore type
	resourceStore.isUriManageable(resourceUri);
	// bind the store to the single instance
	this.resourceStore = resourceStore;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doLoad()
    */
   @Override
   protected ResourceHandler doLoad() throws StoreException {
	resourceHandler = resourceStore.load(getResourceBinding());
	return resourceHandler;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doStore()
    */
   @Override
   protected ResourceHandler doStore() throws StoreException {
	resourceStore.store(getResourceBinding(), resourceHandler);
	return resourceHandler;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doUnload()
    */
   @Override
   protected void doUnload() throws StoreException {
	resourceHandler.release();
	resourceHandler = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#init(java.lang.Object)
    */
   @Override
   protected void init(final URI resourceBinding) {
   }

}
