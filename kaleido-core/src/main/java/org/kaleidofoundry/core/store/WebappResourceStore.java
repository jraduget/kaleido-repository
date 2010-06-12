package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.ResourceStoreConstants.WebappStorePluginName;

import java.io.InputStream;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.web.ServletContextProvider;

/**
 * Webapp resource store
 * 
 * @author Jerome RADUGET
 */
@DeclarePlugin(WebappStorePluginName)
public class WebappResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * @param context
    */
   public WebappResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.webapp };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doLoad(final URI resourceUri) throws StoreException {
	String localName = FileHelper.buildCustomPath(resourceUri.getPath(), FileHelper.WEBAPP_SEPARATOR);
	InputStream input = ServletContextProvider.getServletContext().getResourceAsStream(localName);

	if (input == null) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} else {
	   return new ResourceHandlerBean(input);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws StoreException {
	throw new IllegalStateException("Can't remove a resource from webapp classpath. WebappResourceStore is for a readonly use");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI,
    * org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws StoreException {
	throw new IllegalStateException("Can't store a resource from webapp classpath. WebappResourceStore is for a readonly use");
   }

}