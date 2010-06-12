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
	bind(CacheFactory.class).to(EhCache1xFactoryImpl.class);

	// bind custom annotation
	bind(Cache.class).annotatedWith(EhCache.class).to(EhCache1xImpl.class).in(scope(EhCache1xImpl.class));
	bind(Cache.class).annotatedWith(JbossCache.class).to(Jboss32xCacheImpl.class).in(scope(Jboss32xCacheImpl.class));
	bind(Cache.class).annotatedWith(InfinispanCache.class).to(Infinispan4xCacheImpl.class).in(scope(Infinispan4xCacheImpl.class));
	bind(Cache.class).annotatedWith(CoherenceCache.class).to(Coherence35xCacheImpl.class).in(scope(Coherence35xCacheImpl.class));
   }
}
