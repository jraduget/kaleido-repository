package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.ResourceStoreConstants.MemoryStorePluginName;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * Memory internal resource store. <b>Avoid using it</b>, or only for small binary data, without multi-threading<br/>
 * Be careful {@link ResourceHandler} is not thread safe. Internally, you can create {@link ResourceHandlerBean} giving an
 * {@link ByteArrayInputStream} in argument to use it. By this way, your binary data will be kept in memory
 * 
 * @author Jerome RADUGET
 */
@DeclarePlugin(MemoryStorePluginName)
public class MemoryResourceStore extends AbstractResourceStore {

   private final ConcurrentMap<URI, ResourceHandler> memoryResources = new ConcurrentHashMap<URI, ResourceHandler>();

   /**
    * @param context
    */
   public MemoryResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   @Override
   protected ResourceHandler doLoad(final URI resourceUri) throws StoreException {
	ResourceHandler rh = memoryResources.get(resourceUri);
	if (rh == null) {
	   rh = new ResourceHandlerBean(new ByteArrayInputStream(new byte[0]));
	   memoryResources.put(resourceUri, rh);
	}
	return rh;

   }

   @Override
   protected void doRemove(final URI resourceUri) throws StoreException {
	memoryResources.remove(resourceUri);
   }

   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws StoreException {
	memoryResources.put(resourceUri, resource);

   }

   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.memory };
   }

}
