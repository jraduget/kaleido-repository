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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * {@link FileStore} factory
 * 
 * @author Jerome RADUGET
 */
public abstract class FileStoreFactory {

   // file store provider used by the factory
   private static final FileStoreProvider FILE_STORE_PROVIDER = new FileStoreProvider(FileStore.class);

   /**
    * @return File store instances
    */
   public static FileStoreRegistry getRegistry() {
	return FileStoreProvider.FILE_STORE_REGISTRY;
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
   public static FileStore provides(final String baseUri) throws ProviderException {
	return FILE_STORE_PROVIDER.provides(baseUri);
   }

   /**
    * @param baseUri
    *           file store root path uri, looks like (path is optional) :
    *           <ul>
    *           <li><code>http://host/</code> <b>or</b> <code>http://host/path</code></li>
    *           <li><code>ftp://host/</code> <b>or</b> <code>ftp://host/path</code></li>
    *           <li><code>classpath:/</code> <b>or</b> <code>classpath:/path</code></li>
    *           <li><code>file:/</code> <b>or</b> <code>file:/path</code></li>
    *           <li><code>webapp:/</code> <b>or</b> <code>webapp:/path</code></li>
    *           <li><code>...</li>
    *           </ul>
    *           <b>uri schemes handled</b>: <code>http|https|ftp|file|classpath|webapp|...</code>
    * @param context
    * @return new file store instance, specific to the resource uri scheme
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public static FileStore provides(@NotNull final String baseUri, @NotNull final RuntimeContext<FileStore> context) throws ProviderException {
	return FILE_STORE_PROVIDER.provides(baseUri, context);
   }

   /**
    * @param context
    * @return new file store instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public static FileStore provides(@NotNull final RuntimeContext<FileStore> context) throws ProviderException {
	return FILE_STORE_PROVIDER.provides(context);
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
	   String currentPath = FileHelper.buildUnixAppPath(FileHelper.getCurrentPath());
	   currentPath = currentPath.endsWith("/") ? currentPath.substring(0, currentPath.length() - 1) : currentPath;
	   resourcePath = StringHelper.replaceAll(resourcePath, "${basedir}", (currentPath.startsWith("/") ? currentPath.substring(1) : currentPath));
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
