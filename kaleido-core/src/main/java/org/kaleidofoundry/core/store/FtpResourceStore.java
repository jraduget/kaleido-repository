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

import static org.kaleidofoundry.core.store.ResourceStoreConstants.FtpStorePluginName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * ftp & sftp resource store implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
@Declare(FtpStorePluginName)
public class FtpResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * enumeration of local context property name
    */
   public static enum ContextProperty {
	/** the proxy adress - proxy type {@link Proxy.Type} will be HTTP for ftp, Direct for sftp */
	proxyAdress;
   }

   /**
    * @param context
    */
   public FtpResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.ftp };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws StoreException {
	if (resourceUri.getHost() == null) { throw new IllegalStateException(resourceUri.toString() + " is not an ftp:// valid uri. No host is specified..."); }
	try {
	   final URL configUrl = resourceUri.toURL();
	   final URLConnection urlConnection = configUrl.openConnection();
	   urlConnection.connect();
	   try {
		return new ResourceHandlerBean(urlConnection.getInputStream());
	   } catch (FileNotFoundException fnfe) {
		throw new ResourceNotFoundException(resourceUri.toString());
	   }
	} catch (MalformedURLException mure) {
	   throw new IllegalStateException(resourceUri.toString() + " is not an ftp:// uri");
	} catch (IOException ioe) {
	   throw new StoreException(ioe);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   @NotYetImplemented
   protected void doRemove(final URI resourceUri) throws StoreException {
	// TODO ftp do remove
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI,
    * org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   @NotYetImplemented
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws StoreException {
	// TODO ftp do store
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

}
