/*  
 * Copyright 2008-2014 the original author or authors 
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

import static org.kaleidofoundry.core.store.FileStoreConstants.WebappStorePluginName;

import java.io.InputStream;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.web.ServletContextProvider;
import org.kaleidofoundry.core.web.StartupListener;

/**
 * Webapp classpath {@link FileStore} implementation
 * <br/>
 * It gave you access to WEB-INF/ classpath resources <br/>
 * <br/>
 * To use it, please check you have right declared servlet listener {@link StartupListener} in your web.xml
 * 
 * @author jraduget
 * @see FileStoreContextBuilder enum of context configuration properties available
 * @see StartupListener servlet listener to declare in your web.xml
 */
@Declare(WebappStorePluginName)
public class WebappFileStore extends AbstractFileStore implements FileStore {

   /**
    * @param context
    */
   public WebappFileStore(@NotNull final RuntimeContext<FileStore> context) {
	super(context);
   }

   /**
    * @param baseUri
    * @param context
    */
   public WebappFileStore(final String baseUri, final RuntimeContext<FileStore> context) {
	super(baseUri, context);
   }

   /**
    * @see AbstractFileStore#AbstractFileStore()
    */
   WebappFileStore() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#getStoreType()
    */
   @Override
   public FileStoreType[] getStoreType() {
	return new FileStoreType[] { FileStoreTypeEnum.webapp };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#isReadOnly()
    */
   @Override
   public boolean isReadOnly() {
	return true;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doGet(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws ResourceNotFoundException, ResourceException {
	final String localName = FileHelper.buildCustomPath(resourceUri.getPath(), FileHelper.WEBAPP_SEPARATOR);
	final InputStream input = ServletContextProvider.getServletContext().getResourceAsStream(localName);

	if (input == null) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} else {
	   return createResourceHandler(resourceUri.toString(), input);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws ResourceException {
	throw new ResourceException("store.readonly.illegal", context.getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {
	throw new ResourceException("store.readonly.illegal", context.getName());
   }

}