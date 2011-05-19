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

import static org.kaleidofoundry.core.store.FileStoreConstants.FtpStorePluginName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * ftp store implementation
 * 
 * @author Jerome RADUGET
 * @see FileStoreContextBuilder enum of context configuration properties available
 */
@Immutable
@Declare(FtpStorePluginName)
public class FtpStore extends AbstractFileStore implements FileStore {

   /**
    * @param context
    */
   public FtpStore(@NotNull final RuntimeContext<FileStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#getStoreType()
    */
   @Override
   public FileStoreType[] getStoreType() {
	return new FileStoreType[] { FileStoreTypeEnum.ftp };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doLoad(java.net.URI)
    */
   @Override
   protected FileHandler doGet(final URI resourceUri) throws StoreException {
	if (resourceUri.getHost() == null) { throw new IllegalStateException(InternalBundleHelper.StoreMessageBundle.getMessage("store.uri.ftp.illegal",
		resourceUri.toString())); }

	try {

	   /*
	    * # java env. variable to defined proxy globally
	    * # http://download.oracle.com/docs/cd/E17409_01/javase/6/docs/technotes/guides/net/properties.html
	    * http.proxyHost (default: <none>)
	    * http.proxyPort (default: 80 if http.proxyHost specified)
	    * http.nonProxyHosts (default: <none>)
	    */

	   final URL configUrl = resourceUri.toURL();
	   final URLConnection urlConnection;
	   Proxy httpProxy = null;

	   // if a proxy is set & active
	   if (!StringHelper.isEmpty(context.getString(FileStoreContextBuilder.ProxySet))) {
		if (context.getBoolean(FileStoreContextBuilder.ProxySet)) {

		   final String proxyHost = context.getString(FileStoreContextBuilder.ProxyHost);
		   final String proxyPort = context.getString(FileStoreContextBuilder.ProxyPort);

		   if (!StringHelper.isEmpty(proxyHost)) {
			httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, !StringHelper.isEmpty(proxyPort) ? Integer.parseInt(proxyPort) : 80));

			if (!StringHelper.isEmpty(context.getString(FileStoreContextBuilder.NonProxyHosts))) {
			   // :( global...
			   System.getProperties().put("ftp.nonProxyHosts", context.getProperty(FileStoreContextBuilder.NonProxyHosts));
			}

			if (!StringHelper.isEmpty(context.getString(FileStoreContextBuilder.ProxyUser))
				&& !StringHelper.isEmpty(context.getString(FileStoreContextBuilder.ProxyPassword))) {

			   // Authenticator is global... :(
			   // other way : urlConnection.setRequestProperty("Proxy-Authorization", Base64.encodeObject(username));
			   // http://en.wikipedia.org/wiki/Base64
			   Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
				   return new PasswordAuthentication(context.getString(FileStoreContextBuilder.ProxyUser), context.getString(
					   FileStoreContextBuilder.ProxyPassword).toCharArray());
				}
			   });

			}
		   }
		}
	   }

	   if (httpProxy == null) {
		// open connection with proxy settings
		urlConnection = configUrl.openConnection();
	   } else {
		// open connection with default proxy settings
		urlConnection = configUrl.openConnection(httpProxy);
	   }

	   // set commons connection settings
	   setUrlConnectionSettings(urlConnection);
	   // connection
	   urlConnection.connect();

	   try {
		return new FileHandlerBean(resourceUri.toString(), urlConnection.getInputStream());
	   } catch (final FileNotFoundException fnfe) {
		throw new ResourceNotFoundException(resourceUri.toString());
	   }

	} catch (final MalformedURLException mure) {
	   throw new IllegalStateException(InternalBundleHelper.StoreMessageBundle.getMessage("store.uri.malformed", resourceUri.toString()));
	} catch (final ConnectException ce) {
	   throw new StoreException("store.connection.error", ce, resourceUri.toString());
	} catch (final IOException ioe) {
	   throw new StoreException(ioe, resourceUri.toString());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   @NotYetImplemented
   @Task(comment = "ftp do remove", labels = TaskLabel.ImplementIt)
   protected void doRemove(final URI resourceUri) throws ResourceNotFoundException, StoreException {
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, org.kaleidofoundry.core.store.FileHandler)
    */
   @Override
   @NotYetImplemented
   @Task(comment = "ftp do store", labels = TaskLabel.ImplementIt)
   protected void doStore(final URI resourceUri, final FileHandler resource) throws StoreException {
	return; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

}
