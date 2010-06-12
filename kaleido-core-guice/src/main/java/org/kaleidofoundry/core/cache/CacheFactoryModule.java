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
public class CacheFactoryModule extends AbstractModule<CacheFactory> {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.ioc.AbstractModule#getUnnamedImplementation()
    */
   @Override
   public Class<? extends CacheFactory> getUnnamedImplementation() {
	return EhCache1xFactoryImpl.class;
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
	bind(CacheFactory.class).annotatedWith(EhCache.class).to(EhCache1xFactoryImpl.class).in(scope(EhCache1xFactoryImpl.class));
	bind(CacheFactory.class).annotatedWith(JbossCache.class).to(Jboss32xCacheFactoryImpl.class).in(scope(EhCache1xFactoryImpl.class));
	bind(CacheFactory.class).annotatedWith(InfinispanCache.class).to(Infinispan4xCacheFactoryImpl.class).in(
		scope(EhCache1xFactoryImpl.class));
	bind(CacheFactory.class).annotatedWith(CoherenceCache.class).to(Coherence35xCacheFactoryImpl.class).in(
		scope(EhCache1xFactoryImpl.class));
   }
}
