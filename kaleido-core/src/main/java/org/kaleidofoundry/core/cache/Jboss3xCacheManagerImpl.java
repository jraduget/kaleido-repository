/*
 * Copyright 2008-2014 the original author or authors
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

import static org.kaleidofoundry.core.cache.CacheConstants.JbossCacheManagerPluginName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.config.ConfigurationException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Jboss cache manager provider
 * 
 * @see CacheFactory
 * @author jraduget
 */
@Declare(value = JbossCacheManagerPluginName)
public class Jboss3xCacheManagerImpl extends org.kaleidofoundry.core.cache.AbstractCacheManager {

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = "jboss.xml";

   // internal jboss cache manager
   @SuppressWarnings("rawtypes")
   final org.jboss.cache.CacheFactory cacheManager;

   /**
    * @param context
    */
   public Jboss3xCacheManagerImpl(final RuntimeContext<org.kaleidofoundry.core.cache.CacheManager> context) {
	this(context.getString(FileStoreUri), context);
   }

   /**
    * @param configuration override the context configuration file (if defined)
    * @param context
    */
   @SuppressWarnings("rawtypes")
   public Jboss3xCacheManagerImpl(final String configuration, final RuntimeContext<CacheManager> context) {
	super(configuration, context);

	// internal jboss cache factory
	cacheManager = new DefaultCacheFactory();
	// test if configuration is legal
	final String initTestCacheName = "__inittest__";
	// get and create the cache
	createCache(initTestCacheName);
	// all is ok, we detroy it
	destroy(initTestCacheName);
   }

   /**
    * @see AbstractCacheManager#AbstractCacheManager()
    */
   Jboss3xCacheManagerImpl() {
	super();
	cacheManager = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDefaultConfiguration()
    */
   @Override
   public String getDefaultConfiguration() {
	return DefaultCacheConfiguration;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getMetaInformations()
    */
   @Override
   public String getMetaInformations() {
	return "jboss-cache-3.x (3.0.x -> 3.2.x)";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.String, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @SuppressWarnings("unchecked")
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final String name, @NotNull final RuntimeContext<Cache<K, V>> context) {
	Cache<K, V> cache = cachesByName.get(name);
	if (cache == null) {
	   cache = new Jboss3xCacheImpl<K, V>(name, this, context);
	   cachesByName.put(name, cache);
	}
	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroy(java.lang.String)
    */
   @Override
   public synchronized void destroy(final String cacheName) {
	final Cache<?, ?> cache = cachesByName.get(cacheName);
	if (cache != null) {
	   ((Jboss3xCacheImpl<?, ?>) cache).destroy();
	   cachesByName.remove(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#destroyAll()
    */
   @Override
   public synchronized void destroyAll() {
	super.destroyAll();
	for (final String name : cachesByName.keySet()) {
	   LOGGER.info(CacheMessageBundle.getMessage("cachemanager.destroy.info", name));
	   destroy(name);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#clearStatistics(java.lang.String)
    */
   @Override
   @Task(labels = TaskLabel.ImplementIt)
   public void clearStatistics(final String cacheName) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#dumpStatistics(java.lang.String)
    */
   @Override
   @Task(comment = "http://www.redhat.com/docs/manuals/jboss/jboss-eap-4.3/doc/cache/Tree_Cache_Guide/Management_Information-JBoss_Cache_Statistics.html", labels = TaskLabel.ImplementIt)
   public Map<String, Object> dumpStatistics(final String cacheName) {
	return new LinkedHashMap<String, Object>();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	return cacheManager;
   }

   /**
    * @param name cache name
    * @param <K>
    * @param <V>
    * @return provider cache instance
    */
   @SuppressWarnings("unchecked")
   protected <K extends Serializable, V extends Serializable> org.jboss.cache.Cache<K, V> createCache(final String name) {

	try {
	   final org.jboss.cache.Cache<K, V> cache;

	   LOGGER.info(CacheMessageBundle.getMessage("cachemanager.create.default", name, getName()));

	   if (StringHelper.isEmpty(getCurrentConfiguration())) {
		cache = cacheManager.createCache(true);
	   } else {
		final InputStream inConf = getConfiguration(getCurrentConfiguration());
		if (inConf != null) {
		   cache = cacheManager.createCache(inConf, true);
		} else {
		   throw new CacheConfigurationNotFoundException("cache.configuration.notfound", JbossCacheManagerPluginName, getCurrentConfiguration());
		}
	   }
	   return cache;
	} catch (final ConfigurationException cfe) {
	   throw new CacheConfigurationException("cache.configuration.error", cfe, JbossCacheManagerPluginName, getCurrentConfiguration());
	}
   }
}
