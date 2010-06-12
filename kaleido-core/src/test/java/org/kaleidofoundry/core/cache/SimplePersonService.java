package org.kaleidofoundry.core.cache;

import java.util.Set;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheFactory;

/**
 * Simple use case for caching for a java bean (distributed or not, depends on cache configuration)
 * 
 * @author Jerome RADUGET
 */
public class SimplePersonService {

   protected final transient Cache<Integer, Person> personCache;

   // protected final transient Cache<Person> personCache = CacheFactory.getCacheFactory().getCache(Person.class);

   public SimplePersonService() {

	// Get factory cache implementation
	// *********************************
	// EhCache implementation by default, to choose implementation use java env variable like :
	// -Dcache.implementation=ehcache-1.x
	// -Dcache.implementation=jboss-cache-3.x
	// -Dcache.implementation=coherence-3.x
	// or manually call factory with (hard coded...)
	// CacheFactory<Person> cacheFactory = CacheFactory.getCacheFactory(CacheEnum.JBOSS_3X);

	final CacheFactory<Integer, Person> cacheFactory = CacheFactory.getCacheFactory();

	// Instanciate your person cache :
	personCache = cacheFactory.getCache(Person.class);
   }

   /**
    * find a person by its id in cache layer
    * 
    * @param id
    * @return person instance mapped to the id
    */
   public Person findById(final Integer id) {
	return personCache.get(id);
   }

   /**
    * Persist your person to db, file,... and put it in person cache
    * 
    * @param bean
    */
   public void persist(final Person bean) {
	// persit with dao access
	// ...
	// all is ok, cache the entity
	personCache.put(bean.getId(), bean);
   }

   /**
    * @param bean remove a person from persistent layer, and cache layer
    */
   public void remove(final Person bean) {
	// remove your person with dao access
	// ...
	// all is ok, remove cache the entity
	personCache.remove(bean.getId());
   }

   /**
    * @return all keys of the cache instance
    */
   public Set<Integer> keys() {
	return personCache.keys();
   }

}
