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
public class CacheModule extends AbstractModule<Cache> {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.ioc.AbstractModule#getUnnamedImplementation()
    */
   @Override
   public Class<? extends Cache> getUnnamedImplementation() {
	return EhCache1xImpl.class;
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
	bind(Cache.class).annotatedWith(EhCache.class).to(EhCache1xImpl.class).in(scope(EhCache1xImpl.class));
	bind(Cache.class).annotatedWith(JbossCache.class).to(Jboss32xCacheImpl.class).in(scope(Jboss32xCacheImpl.class));
	bind(Cache.class).annotatedWith(InfinispanCache.class).to(Infinispan4xCacheImpl.class).in(scope(Infinispan4xCacheImpl.class));
	bind(Cache.class).annotatedWith(CoherenceCache.class).to(Coherence35xCacheImpl.class).in(scope(Coherence35xCacheImpl.class));
   }
}
