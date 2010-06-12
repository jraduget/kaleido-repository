package org.kaleidofoundry.core.store;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;

/**
 * {@link ResourceStore} factory
 * 
 * @author Jerome RADUGET
 */
public abstract class ResourceStoreFactory {

   /**
    * create a instance of {@link ResourceStore} analyzing a given {@link URI}<br/>
    * scheme of the uri is used to get the registered resource store implementation.
    * 
    * @param resourceUri
    * @param context
    * @return new resource store instance, specific to the resource uri scheme
    * @throws StoreException
    */
   public static ResourceStore createResourceStore(@NotNull final URI resourceUri, @NotNull final RuntimeContext<ResourceStore> context) throws StoreException {

	ResourceStoreType rse = ResourceStoreTypeEnum.match(resourceUri);

	if (rse != null) {

	   Set<Plugin<ResourceStore>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(ResourceStore.class);
	   // scan each @DeclarePlugin resource store implementation, to get one which handle the uri scheme
	   for (Plugin<ResourceStore> pi : pluginImpls) {
		Class<? extends ResourceStore> impl = pi.getAnnotatedClass();
		try {
		   Constructor<? extends ResourceStore> constructor = impl.getConstructor(context.getClass());
		   ResourceStore resourceStore = constructor.newInstance(context);

		   try {
			if (resourceStore.isUriManageable(resourceUri)) { return resourceStore; }
		   } catch (Throwable th) {
		   }

		} catch (NoSuchMethodException e) {
		   throw new StoreException("store.resource.factory.create.NoSuchMethodException", impl.getName());
		} catch (InstantiationException e) {
		   throw new StoreException("store.resource.factory.create.InstantiationException", impl.getName(), e.getMessage());
		} catch (IllegalAccessException e) {
		   throw new StoreException("store.resource.factory.create.IllegalAccessException=ResourceStore", impl.getName());
		} catch (InvocationTargetException e) {
		   throw new StoreException("store.resource.factory.create.InvocationTargetException", impl.getName(), e.getMessage());
		}
	   }

	   throw new StoreException("store.resource.uri.custom.notmanaged", resourceUri.getScheme());
	}

	throw new StoreException("store.resource.uri.notmanaged", resourceUri.getScheme());

   }

   /**
    * @param resourceUri
    * @return new resource store instance
    * @throws StoreException
    */
   public static ResourceStore createResourceStore(final URI resourceUri) throws StoreException {
	return createResourceStore(resourceUri, new RuntimeContext<ResourceStore>());
   }

   /**
    * @param resourceUri
    * @param context
    * @return new resource store instance, specific to the resource uri scheme
    * @throws StoreException
    */
   public static ResourceStore createResourceStore(@NotNull final String resourceUri, @NotNull final RuntimeContext<ResourceStore> context)
	   throws StoreException {
	return createResourceStore(URI.create(resourceUri), context);
   }

   /**
    * @param resourceUri
    * @return new resource store instance
    * @throws StoreException
    */
   public static ResourceStore createResourceStore(final String resourceUri) throws StoreException {
	return createResourceStore(URI.create(resourceUri), new RuntimeContext<ResourceStore>());
   }

}
