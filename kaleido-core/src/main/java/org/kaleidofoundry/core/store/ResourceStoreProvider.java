/*
 *  Copyright 2008-2010 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.store;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.RuntimeContextEmptyParameterException;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * ResourceStore Provider
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreProvider extends AbstractProviderService<ResourceStore> {

   /**
    * @param genericClass
    */
   public ResourceStoreProvider(final Class<ResourceStore> genericClass) {
	super(genericClass);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public ResourceStore provides(@NotNull final RuntimeContext<ResourceStore> context) throws ProviderException, ResourceException {
	final String uriScheme = context.getProperty(ResourceContextBuilder.UriScheme);

	if (StringHelper.isEmpty(uriScheme)) { throw new RuntimeContextEmptyParameterException(ResourceContextBuilder.UriScheme, context); }

	return provides(uriScheme, new RuntimeContext<ResourceStore>(ResourceStore.class));
   }

   /**
    * @param resourceUri <br/>
    *           uri scheme, like: <code>http|https|ftp|file|classpath|webapp|...</code>, <br/>
    *           or uri scheme start, like: <code>http://|https://|ftp://|file:/|classpath:/|webapp:/|...</code><br/>
    *           or uri template, like : <code>http://host/path|classpath:/localpath</code
    * @return new resource store instance
    * @throws ResourceException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public ResourceStore provides(@NotNull final URI resourceUri) throws ProviderException, ResourceException {
	return provides(resourceUri, new RuntimeContext<ResourceStore>(ResourceStore.class));
   }

   /**
    * @param resourceUri <br/>
    *           uri scheme, like: <code>http|https|ftp|file|classpath|webapp|...</code>, <br/>
    *           or uri scheme start, like: <code>http://|https://|ftp://|file:/|classpath:/|webapp:/|...</code><br/>
    *           or uri template, like : <code>http://host/path|classpath:/localpath</code
    * @param context
    * @return new resource store instance, specific to the resource uri scheme
    * @throws ResourceException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public ResourceStore provides(@NotNull final String resourceUri, @NotNull final RuntimeContext<ResourceStore> context) throws ProviderException,
	   ResourceException {
	return provides(createURI(resourceUri), context);
   }

   /**
    * @param resourceUri <br/>
    *           uri scheme, like: <code>http|https|ftp|file|classpath|webapp|...</code>, <br/>
    *           or uri scheme start, like: <code>http://|https://|ftp://|file:/|classpath:/|webapp:/|...</code><br/>
    *           or uri template, like : <code>http://host/path|classpath:/localpath</code
    * @return new resource store instance
    * @throws ResourceException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public ResourceStore provides(final String resourceUri) throws ProviderException, ResourceException {
	return provides(createURI(resourceUri), new RuntimeContext<ResourceStore>(ResourceStore.class));
   }

   /**
    * create a instance of {@link ResourceStore} analyzing a given {@link URI}<br/>
    * scheme of the uri is used to get the registered resource store implementation.
    * 
    * @param resourceUri <br/>
    *           uri scheme, like: <code>http|https|ftp|file|classpath|webapp|...</code>, <br/>
    *           or uri scheme start, like: <code>http://|https://|ftp://|file:/|classpath:/|webapp:/|...</code><br/>
    *           or uri template, like : <code>http://host/path|classpath:/localpath</code
    * @param context store runtime context
    * @return new resource store instance, specific to the resource uri scheme
    * @throws ResourceException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public ResourceStore provides(@NotNull final URI resourceUri, @NotNull final RuntimeContext<ResourceStore> context) throws ProviderException,
	   ResourceException {

	final ResourceStoreType rse = ResourceStoreTypeEnum.match(resourceUri);

	if (rse != null) {

	   final Set<Plugin<ResourceStore>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(ResourceStore.class);
	   // scan each @Declare resource store implementation, to get one which handle the uri scheme
	   for (final Plugin<ResourceStore> pi : pluginImpls) {
		final Class<? extends ResourceStore> impl = pi.getAnnotatedClass();
		try {
		   final Constructor<? extends ResourceStore> constructor = impl.getConstructor(context.getClass());
		   final ResourceStore resourceStore = constructor.newInstance(context);

		   try {
			if (resourceStore.isUriManageable(resourceUri)) { return resourceStore; }
		   } catch (final Throwable th) {
		   }

		} catch (final NoSuchMethodException e) {
		   throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<ResourceStore> context");
		} catch (final InstantiationException e) {
		   throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
		} catch (final IllegalAccessException e) {
		   throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<ResourceStore> context");
		} catch (final InvocationTargetException e) {
		   if (e.getCause() instanceof ResourceException) {
			throw (ResourceException) e.getCause();
		   } else {
			throw new ProviderException("context.provider.error.InvocationTargetException", impl.getName(), "RuntimeContext<ResourceStore> context", e
				.getCause().getClass().getName(), e.getMessage());
		   }
		}
	   }

	   throw new ResourceException("store.resource.uri.custom.notmanaged", resourceUri.getScheme(), resourceUri.toString());
	}

	throw new ResourceException("store.resource.uri.notmanaged", resourceUri.getScheme(), resourceUri.toString());

   }

   // use to resolve uri although pUri contains only the uri scheme like ftp|http|classpath...
   private static URI createURI(@NotNull final String pUri) {

	if (!pUri.contains(":") && !pUri.contains("/")) {
	   return URI.create(pUri + ":/");
	} else {
	   return URI.create(pUri);
	}

   }

}
