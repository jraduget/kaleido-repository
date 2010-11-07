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
package org.kaleidofoundry.core.cache;

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Abstract cache factory.
 * 
 * @author Jerome RADUGET
 */
public class CacheFactory {

   private static final CacheProvider CACHE_PROVIDER = new CacheProvider(Cache.class);

   /**
    * @param context
    * @return cache instance
    * @throws ProviderException
    * @see org.kaleidofoundry.core.cache.CacheProvider#provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @SuppressWarnings("rawtypes")
   public static Cache provides(final RuntimeContext<Cache> context) throws ProviderException {
	return CACHE_PROVIDER.provides(context);
   }

   /**
    * @param cacheName
    * @param context
    * @return cache instance
    * @throws ProviderException
    * @see org.kaleidofoundry.core.cache.CacheProvider#provides(java.lang.String, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @SuppressWarnings("rawtypes")
   public static Cache provides(final String cacheName, final RuntimeContext<Cache> context) throws ProviderException {
	return CACHE_PROVIDER.provides(cacheName, context);
   }

}
