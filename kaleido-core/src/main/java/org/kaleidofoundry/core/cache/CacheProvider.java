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

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * @author Jerome RADUGET
 */
@SuppressWarnings("rawtypes")
public class CacheProvider extends AbstractProviderService<Cache> {

   /**
    * @param genericClassInterface
    */
   public CacheProvider(final Class<Cache> genericClassInterface) {
	super(genericClassInterface);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.ProviderService#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public Cache _provides(@NotNull final RuntimeContext<Cache> context) throws ProviderException {
	String cacheName = context.getString(CacheName);
	if (StringHelper.isEmpty(cacheName)) {
	   cacheName = context.getName();
	}
	if (StringHelper.isEmpty(cacheName)) { throw new EmptyContextParameterException(CacheName, context); }
	return provides(cacheName, context);
   }

   /**
    * registry is handle by cache manager
    */
   @Override
   protected Registry<String, Cache> getRegistry() {
	return null;
   }

   /**
    * @param cacheName
    * @param context
    * @return cache current instance
    * @throws ProviderException
    */
   public Cache provides(@NotNull final String cacheName, @NotNull final RuntimeContext<Cache> context) throws ProviderException {
	final String cacheManagerRef = context.getString(CacheManagerRef);
	final RuntimeContext<CacheManager> cacheManagerContext;

	if (StringHelper.isEmpty(cacheManagerRef)) {
	   cacheManagerContext = new RuntimeContext<CacheManager>(CacheManagerProvider.DEFAULT_CACHE_PROVIDER);
	} else {
	   cacheManagerContext = new RuntimeContext<CacheManager>(cacheManagerRef, CacheManager.class, context);
	}

	return CacheManagerFactory.provides(cacheManagerContext).getCache(cacheName);
   }

}
