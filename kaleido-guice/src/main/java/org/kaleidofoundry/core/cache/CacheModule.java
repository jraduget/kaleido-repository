/*  
 * Copyright 2008-2016 the original author or authors 
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

import org.kaleidofoundry.core.cache.annotation.EhCache;
import org.kaleidofoundry.core.cache.annotation.InfinispanCache;
import org.kaleidofoundry.core.cache.annotation.JbossCache;
import org.kaleidofoundry.core.context.AbstractModule;

/**
 * Guice cache factory
 * 
 * @author jraduget
 */
@SuppressWarnings("rawtypes")
public class CacheModule extends AbstractModule<Cache> {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.ioc.AbstractModule#getUnnamedImplementation()
    */
   @Override
   public Class<? extends Cache> getUnnamedImplementation() {
	return EhCacheImpl.class;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.ioc.AbstractModule#configure()
    */
   @Override
   protected void configure() {
	super.configure();

	// default implementation
	bind(CacheManager.class).to(EhCacheManagerImpl.class);

	// bind custom annotation
	bind(Cache.class).annotatedWith(EhCache.class).to(EhCacheImpl.class).in(scope(EhCacheImpl.class));
	bind(Cache.class).annotatedWith(JbossCache.class).to(Jboss3xCacheImpl.class).in(scope(Jboss3xCacheImpl.class));
	bind(Cache.class).annotatedWith(InfinispanCache.class).to(InfinispanCacheImpl.class).in(scope(InfinispanCacheImpl.class));	
   }
}
