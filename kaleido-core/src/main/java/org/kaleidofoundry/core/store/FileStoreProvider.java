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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.StoreMessageBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileStore Provider
 * 
 * @author Jerome RADUGET
 */
public class FileStoreProvider extends AbstractProviderService<FileStore> {

   // application base directory (use to merge with ${basedir} variable)
   public static final String BASE_DIR;

   /**
    * files store internal registry
    */
   static final FileStoreRegistry FILE_STORE_REGISTRY = new FileStoreRegistry();

   private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreFactory.class);

   static {
	String currentPath = System.getProperty("kaleido.basedir");
	if (StringHelper.isEmpty(currentPath)) {
	   currentPath = FileHelper.buildUnixAppPath(FileHelper.getCurrentPath());
	}
	currentPath = currentPath.endsWith("/") ? currentPath.substring(0, currentPath.length() - 1) : currentPath;
	BASE_DIR = (currentPath.startsWith("/") ? currentPath.substring(1) : currentPath);

	LOGGER.info(StoreMessageBundle.getMessage("store.basedir.info", BASE_DIR));
   }

   /**
    * @param genericClass
    */
   public FileStoreProvider(final Class<FileStore> genericClass) {
	super(genericClass);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public FileStore _provides(@NotNull final RuntimeContext<FileStore> context) throws ProviderException {
	final String baseUri = context.getString(FileStoreContextBuilder.BaseUri);

	if (StringHelper.isEmpty(baseUri)) { throw new EmptyContextParameterException(FileStoreContextBuilder.BaseUri, context); }

	return provides(baseUri, context);
   }

   /**
    * @param baseUri
    *           file store root path uri, which looks like (path is optional) :
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
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public FileStore provides(final String baseUri) throws ProviderException {
	return provides(baseUri, new FileStoreContextBuilder().withBaseUri(baseUri).build());
   }

   /**
    * create a instance of {@link FileStore} analyzing a given {@link URI}<br/>
    * scheme of the uri is used to get the registered file store implementation.
    * 
    * @param pBaseUri <br/>
    *           file store root path uri, which looks like (path is optional) :
    *           <ul>
    *           <li><code>http://host/</code> <b>or</b> <code>http://host/path</code></li>
    *           <li><code>ftp://host/</code> <b>or</b> <code>ftp://host/path</code></li>
    *           <li><code>classpath:/</code> <b>or</b> <code>classpath:/path</code></li>
    *           <li><code>file:/</code> <b>or</b> <code>file:/path</code></li>
    *           <li><code>webapp:/</code> <b>or</b> <code>webapp:/path</code></li>
    *           <li><code>...</li>
    *           </ul>
    *           <b>uri schemes handled</b>: <code>http|https|ftp|file|classpath|webapp|...</code>
    * @param pContext store runtime context
    * @return new file store instance, specific to the resource uri scheme
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public FileStore provides(@NotNull final String pBaseUri, @NotNull final RuntimeContext<FileStore> pContext) throws ProviderException {

	final URI baseURI = createURI(pBaseUri);
	final FileStoreType fileStoreType = FileStoreTypeEnum.match(baseURI);

	if (fileStoreType != null) {

	   final Set<Plugin<FileStore>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(FileStore.class);
	   // scan each @Declare file store implementation, to get one which handle the uri scheme
	   for (final Plugin<FileStore> pi : pluginImpls) {
		final Class<? extends FileStore> impl = pi.getAnnotatedClass();
		try {
		   final Constructor<? extends FileStore> constructor = impl.getConstructor(String.class, pContext.getClass());
		   final FileStore fileStore = constructor.newInstance(pBaseUri, pContext);

		   try {
			if (fileStore.isUriManageable(pBaseUri.toString())) { return fileStore; }
		   } catch (final Throwable th) {
		   }

		   // unregister wrong file store
		   FileStoreFactory.getRegistry().get(fileStore.getBaseUri()).remove(fileStore);

		} catch (final NoSuchMethodException e) {
		   throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<FileStore> context");
		} catch (final InstantiationException e) {
		   throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
		} catch (final IllegalAccessException e) {
		   throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<FileStore> context");
		} catch (final InvocationTargetException e) {
		   if (e.getCause() instanceof ResourceException) {
			throw new ProviderException(e.getCause());
		   } else {
			throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(),
				"RuntimeContext<FileStore> context", e.getCause().getClass().getName(), e.getCause().getMessage());
		   }
		}
	   }

	   throw new ProviderException(new ResourceException("store.uri.custom.notmanaged", baseURI.getScheme(), pBaseUri.toString()));
	}

	throw new ProviderException(new ResourceException("store.uri.notmanaged", baseURI.getScheme(), pBaseUri.toString()));

   }

   // use to resolve uri although pUri contains only the uri scheme like ftp|http|classpath...
   private static URI createURI(@NotNull final String pUri) {

	// replace all ${param} by "", to avoid URISyntaxException
	String uri = pUri.replaceAll("\\$\\{.+\\}", "");

	if (!uri.contains(":") && !uri.contains("/")) {
	   return URI.create(uri + ":/");
	} else {
	   return URI.create(uri);
	}

   }

   /**
    * A {@link FileStore} resource uri, can contains some variables like ${basedir} ...<br.>
    * This class merge this variable if needed, in order to build a full valid {@link URI} for the filestore
    * 
    * @param resourcePath the resource uri to merge with the system variables
    * @return the merged resource uri
    */
   public static String buildFullResourceURi(String resourcePath) {

	if (resourcePath.contains("${basedir}")) {
	   resourcePath = StringHelper.replaceAll(resourcePath, "${basedir}", FileStoreProvider.BASE_DIR);
	}
	if (resourcePath.contains("file:/..")) {
	   String parentPath = FileHelper.buildUnixAppPath(FileHelper.getParentPath());
	   resourcePath = StringHelper.replaceAll(resourcePath, "file:/..", "file:/" + (parentPath.startsWith("/") ? parentPath.substring(1) : parentPath));
	} else if (resourcePath.startsWith("file:/.")) {
	   String currentPath = FileHelper.buildUnixAppPath(FileHelper.getCurrentPath());
	   resourcePath = StringHelper.replaceAll(resourcePath, "file:/.", "file:/" + (currentPath.startsWith("/") ? currentPath.substring(1) : currentPath));
	}
	return resourcePath;
   }
}
