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
   protected ResourceHandler doGet() throws ResourceException {
	resourceHandler = resourceStore.get(getResourceBinding());
	return resourceHandler;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doStore()
    */
   @Override
   protected ResourceHandler doStore() throws ResourceException {
	resourceStore.store(getResourceBinding(), resourceHandler);
	return resourceHandler;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doUnload()
    */
   @Override
   protected void doUnload() throws ResourceException {
	if (resourceHandler != null) {
	   resourceHandler.release();
	   resourceHandler = null;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#init(java.lang.Object)
    */
   @Override
   protected void init(final URI resourceBinding) {
   }

}
