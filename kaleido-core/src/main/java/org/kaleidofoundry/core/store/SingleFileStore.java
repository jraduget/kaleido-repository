/*  
 * Copyright 2008-2021 the original author or authors 
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * @author jraduget
 */
public class SingleFileStore extends AbstractSingleStore<String, ResourceHandler> {

   private final FileStore fileStore;
   private ResourceHandler resourceHandler;

   /**
    * @param resourceUri
    * @param fileStore
    */
   public SingleFileStore(@NotNull final String resourceUri, @NotNull final FileStore fileStore) {
	super(resourceUri);
	// check that uri, match to fileStore type
	fileStore.isUriManageable(resourceUri);
	// bind the store to the single instance
	this.fileStore = fileStore;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doLoad()
    */
   @Override
   protected ResourceHandler doGet() throws ResourceException {
	resourceHandler = fileStore.get(getResourceBinding());
	return resourceHandler;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doStore()
    */
   @Override
   protected ResourceHandler doStore(final ResourceHandler r) throws ResourceException {
	if (fileStore.isReadOnly()) { throw new ResourceException("store.readonly.illegal", getResourceBinding()); }
	fileStore.store(r);
	this.resourceHandler = r;
	return get();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#doUnload()
    */
   @Override
   protected void doUnload() throws ResourceException {
	if (resourceHandler != null) {
	   resourceHandler.close();
	   resourceHandler = null;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractSingleStore#init(java.lang.Object)
    */
   @Override
   protected void init(final String resourceBinding) {
   }

   /*
    * @see org.kaleidofoundry.core.store.FileStore#createResourceHandler(java.lang.String, java.io.InputStream)
    */
   public ResourceHandler createResourceHandler(final String resourceUri, final InputStream input) {
	return fileStore.createResourceHandler(resourceUri, input);
   }

   /*
    * @see org.kaleidofoundry.core.store.FileStore#createResourceHandler(java.lang.String, java.lang.String)
    */
   public ResourceHandler createResourceHandler(final String resourceUri, final String content) throws UnsupportedEncodingException {
	return fileStore.createResourceHandler(resourceUri, content);
   }

}
