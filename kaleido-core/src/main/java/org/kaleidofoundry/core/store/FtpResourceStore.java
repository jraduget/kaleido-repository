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
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * ftp & sftp resource store implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
@DeclarePlugin(FtpStorePluginName)
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
   protected ResourceHandler doLoad(final URI resourceUri) throws StoreException {
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
