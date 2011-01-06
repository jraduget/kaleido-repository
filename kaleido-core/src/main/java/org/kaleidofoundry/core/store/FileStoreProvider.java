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
 * FileStore Provider
 * 
 * @author Jerome RADUGET
 */
public class FileStoreProvider extends AbstractProviderService<FileStore> {

   /**
    * @param genericClass
    */
   public FileStoreProvider(final Class<FileStore> genericClass) {
	super(genericClass);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public FileStore provides(@NotNull final RuntimeContext<FileStore> context) throws ProviderException, StoreException {
	final String uriRootPath = context.getProperty(FileStoreContextBuilder.UriRootPath);

	if (StringHelper.isEmpty(uriRootPath)) { throw new RuntimeContextEmptyParameterException(FileStoreContextBuilder.UriRootPath, context); }

	return provides(uriRootPath, context);
   }

   /**
    * @param uriRootPath
    *           file store uri root path, looks like (path is optional) :
    *           <ul>
    *           <li><code>http://host/</code> <b>or</b> <code>http://host/path</code></li>
    *           <li><code>ftp://host/</code> <b>or</b> <code>ftp://host/path</code></li>
    *           <li><code>classpath:/</code> <b>or</b> <code>classpath:/path</code></li>
    *           <li><code>file:/</code> <b>or</b> <code>file:/path</code></li>
    *           <li><code>webapp:/</code> <b>or</b> <code>webapp:/path</code></li>
    *           <li><code>...</li>
    *           </ul>
    *           <b>uri schemes handled</b>: <code>http|https|ftp|file|classpath|webapp|...</code>
    * @return new file store instance
    * @throws StoreException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public FileStore provides(final String uriRootPath) throws ProviderException, StoreException {
	return provides(uriRootPath, new RuntimeContext<FileStore>(FileStore.class));
   }

   /**
    * create a instance of {@link FileStore} analyzing a given {@link URI}<br/>
    * scheme of the uri is used to get the registered file store implementation.
    * 
    * @param uriRootPath <br/>
    *           file store uri root path, looks like (path is optional) :
    *           <ul>
    *           <li><code>http://host/</code> <b>or</b> <code>http://host/path</code></li>
    *           <li><code>ftp://host/</code> <b>or</b> <code>ftp://host/path</code></li>
    *           <li><code>classpath:/</code> <b>or</b> <code>classpath:/path</code></li>
    *           <li><code>file:/</code> <b>or</b> <code>file:/path</code></li>
    *           <li><code>webapp:/</code> <b>or</b> <code>webapp:/path</code></li>
    *           <li><code>...</li>
    *           </ul>
    *           <b>uri schemes handled</b>: <code>http|https|ftp|file|classpath|webapp|...</code>
    * @param context store runtime context
    * @return new file store instance, specific to the resource uri scheme
    * @throws StoreException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public FileStore provides(@NotNull final String uriRootPath, @NotNull final RuntimeContext<FileStore> context) throws ProviderException, StoreException {

	final URI resourceUri = createURI(uriRootPath);
	final FileStoreType rse = FileStoreTypeEnum.match(resourceUri);

	if (rse != null) {

	   final Set<Plugin<FileStore>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(FileStore.class);
	   // scan each @Declare file store implementation, to get one which handle the uri scheme
	   for (final Plugin<FileStore> pi : pluginImpls) {
		final Class<? extends FileStore> impl = pi.getAnnotatedClass();
		try {
		   final Constructor<? extends FileStore> constructor = impl.getConstructor(context.getClass());
		   final FileStore fileStore = constructor.newInstance(context);

		   try {
			if (fileStore.isUriManageable(resourceUri.toString())) { return fileStore; }
		   } catch (final Throwable th) {
		   }

		} catch (final NoSuchMethodException e) {
		   throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<FileStore> context");
		} catch (final InstantiationException e) {
		   throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
		} catch (final IllegalAccessException e) {
		   throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<FileStore> context");
		} catch (final InvocationTargetException e) {
		   if (e.getCause() instanceof StoreException) {
			throw (StoreException) e.getCause();
		   } else {
			throw new ProviderException("context.provider.error.InvocationTargetException", impl.getName(), "RuntimeContext<FileStore> context", e
				.getCause().getClass().getName(), e.getMessage());
		   }
		}
	   }

	   throw new StoreException("store.uri.custom.notmanaged", resourceUri.getScheme(), resourceUri.toString());
	}

	throw new StoreException("store.uri.notmanaged", resourceUri.getScheme(), resourceUri.toString());

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
