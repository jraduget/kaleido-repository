package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.JbossCachePluginName;

import java.io.Serializable;
import java.util.Set;

import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JbossCache {@link org.kaleidofoundry.core.cache.Cache} implementation
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <V>
 */
@DeclarePlugin(JbossCachePluginName)
public class Jboss32xCacheImpl<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements org.kaleidofoundry.core.cache.Cache<K, V> {

   private static final Logger LOGGER = LoggerFactory.getLogger(Jboss32xCacheImpl.class);

   private final String name;
   private final Cache<K, V> cache;
   private final Node<K, V> root;

   /**
    * @param c class of the cache
    * @param cache jboss cache instanciate via factory
    */
   Jboss32xCacheImpl(final Class<V> c, final Cache<K, V> cache) {
	this(c != null ? c.getName() : null, cache);
   }

   /**
    * @param name name of the cache
    */
   Jboss32xCacheImpl(final String name, final Cache<K, V> cache) {

	if (name == null) { throw new IllegalArgumentException("cache name argument is null"); }
	if (cache == null) { throw new IllegalArgumentException("cache argument is null"); }

	String cacheName = "/" + name.replaceAll("[.]", "/");

	this.cache = cache;
	this.name = name;
	this.root = cache.getRoot().addChild(Fqn.fromString(cacheName));

	// create and start cache
	LOGGER.info("Starts JBoss Cache root name: {}", cacheName);

	cache.create();
	cache.start();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#getName()
    */
   @Override
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doGet(java.io.Serializable)
    */
   @Override
   public V doGet(final K id) {
	return root.get(id);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doPut(java.io.Serializable, java.io.Serializable)
    */
   @Override
   public void doPut(final K key, final V entity) {
	root.put(key, entity);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.AbstractCache#doRemove(java.io.Serializable)
    */
   @Override
   public boolean doRemove(final K id) {
	return root.remove(id) != null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#keys()
    */
   @Override
   public Set<K> keys() {
	return root.getKeys();
   }

   /**
    * Stop and destroy cache instance
    */
   void destroy() {
	cache.stop();
	cache.destroy();
	hasBeenDestroy = true;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#removeAll()
    */
   @Override
   public void removeAll() {
	root.clearData();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.Cache#size()
    */
   @Override
   public int size() {
	return root.dataSize();
   }

   /**
    * @return internal Jboss cache {@link Cache}
    */
   protected Cache<K, V> getJbossCache() {
	return cache;
   }
}
