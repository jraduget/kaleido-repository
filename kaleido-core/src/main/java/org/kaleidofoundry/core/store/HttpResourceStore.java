package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.ResourceStoreConstants.HttpStorePluginName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotImplemented;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * Simple http & https resource store implementation<br/>
 * This implementation is only for read only use<br/>
 * You can extends it and override {@link #doRemove(URI)} and {@link #doStore(URI, ResourceHandler)} to your need, and {@link DeclarePlugin}
 * your implementation to use it
 * 
 * @author Jerome RADUGET
 */
@DeclarePlugin(HttpStorePluginName)
public class HttpResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * enumeration of local context property name
    */
   public static enum ContextProperty {
	/** current user */
	user,
	/** GET, POST method */
	method,
	/** proxy adress to use - proxy type {@link Proxy.Type} will be HTTP */
	proxyAdress,
	/** mime type */
	contentType,
	/** connection timeout */
	connectTimeout,
	/** read timeout */
	readTimeout,
	/** retry count for connection */
	connectionRetryCount,
	/** retry count for read */
	readRetryCount;
   }

   // **** instance datas **********************************************************************************************

   /**
    * @param context
    */
   public HttpResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.http, ResourceStoreTypeEnum.https };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doLoad(final URI resourceUri) throws StoreException {
	if (resourceUri.getHost() == null) { throw new IllegalStateException(resourceUri.toString()
		+ " is not an http:// or https:// valid uri. No host is specified..."); }
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
	   throw new IllegalStateException(resourceUri.toString() + " is not an http:// or https:// uri");
	} catch (IOException ioe) {
	   throw new StoreException(ioe);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   @NotImplemented("remove method is not implemented in HttpResourceStore. Please consult java api doc")
   protected void doRemove(final URI resourceBinding) throws StoreException {
	return; // !! exception will be throws due to @NotImplemented !!
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI, java.io.InputStream)
    */
   @Override
   @NotImplemented("store method is not implemented in HttpResourceStore. Please consult java api doc")
   protected void doStore(final URI resourceBinding, final ResourceHandler resource) throws StoreException {
	// http://java.sun.com/docs/books/tutorial/networking/urls/readingWriting.html

	// OutputStream out = null;
	// try {
	// final URL configUrl = resourceBinding.toURL();
	// final URLConnection urlConnection = configUrl.openConnection();
	// urlConnection.connect();
	// out = urlConnection.getOutputStream();
	// int b;
	// while ((b = resource.getInputStream().read()) != -1) {
	// out.write(b);
	// }
	// } catch (MalformedURLException mure) {
	// throw new IllegalStateException(resourceBinding.toString() + " is not an http:// or https:// uri");
	// } catch (IOException ioe) {
	// throw new StoreException(ioe);
	// } finally {
	// if (out != null) {
	// try {
	// out.close();
	// } catch (IOException closeioe) {
	// throw new StoreException(closeioe);
	// }
	// }
	// }

	return; // !! exception will be throws due to @NotImplemented !!
   }

}