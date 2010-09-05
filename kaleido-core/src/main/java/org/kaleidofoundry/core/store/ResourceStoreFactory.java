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
import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * {@link ResourceStore} factory
 * 
 * @author Jerome RADUGET
 */
public abstract class ResourceStoreFactory {

   // resource store provider used by the factory
   private static final ResourceStoreProvider RESOURCESTORE_PROVIDER = new ResourceStoreProvider(ResourceStore.class);

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
   public static ResourceStore provides(@NotNull final URI resourceUri, @NotNull final RuntimeContext<ResourceStore> context) throws ProviderException,
	   ResourceException {

	return RESOURCESTORE_PROVIDER.provides(resourceUri, context);
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
   public static ResourceStore provides(final URI resourceUri) throws ProviderException, ResourceException {
	return RESOURCESTORE_PROVIDER.provides(resourceUri);
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
   public static ResourceStore provides(@NotNull final String resourceUri, @NotNull final RuntimeContext<ResourceStore> context) throws ProviderException,
	   ResourceException {
	return RESOURCESTORE_PROVIDER.provides(resourceUri, context);
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
   public static ResourceStore provides(final String resourceUri) throws ProviderException, ResourceException {
	return RESOURCESTORE_PROVIDER.provides(resourceUri);
   }

   /**
    * @param context
    * @return new resource store instance
    * @throws ResourceException
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public static ResourceStore provides(@NotNull final RuntimeContext<ResourceStore> context) throws ProviderException, ResourceException {
	return RESOURCESTORE_PROVIDER.provides(context);
   }
}
