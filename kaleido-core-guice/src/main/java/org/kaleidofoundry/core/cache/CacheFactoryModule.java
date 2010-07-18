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
package org.kaleidofoundry.core.cache;

import org.kaleidofoundry.core.cache.annotation.CoherenceCache;
import org.kaleidofoundry.core.cache.annotation.EhCache;
import org.kaleidofoundry.core.cache.annotation.InfinispanCache;
import org.kaleidofoundry.core.cache.annotation.JbossCache;
import org.kaleidofoundry.core.ioc.AbstractModule;

/**
 * Guice cache factory
 * 
 * @author Jerome RADUGET
 */
@SuppressWarnings("unchecked")
public class CacheFactoryModule extends AbstractModule<CacheManager> {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.ioc.AbstractModule#getUnnamedImplementation()
    */
   @Override
   public Class<? extends CacheManager> getUnnamedImplementation() {
	return EhCache1xManagerImpl.class;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.ioc.AbstractModule#configure()
    */
   @Override
   protected void configure() {
	super.configure();

	// default implementation
	bind(CacheManager.class).to(EhCache1xManagerImpl.class);

	// bind custom annotation
	bind(CacheManager.class).annotatedWith(EhCache.class).to(EhCache1xManagerImpl.class).in(scope(EhCache1xManagerImpl.class));
	bind(CacheManager.class).annotatedWith(JbossCache.class).to(Jboss32xCacheManagerImpl.class).in(scope(EhCache1xManagerImpl.class));
	bind(CacheManager.class).annotatedWith(InfinispanCache.class).to(Infinispan4xCacheManagerImpl.class).in(scope(EhCache1xManagerImpl.class));
	bind(CacheManager.class).annotatedWith(CoherenceCache.class).to(Coherence35xCacheManagerImpl.class).in(scope(EhCache1xManagerImpl.class));
   }
}
