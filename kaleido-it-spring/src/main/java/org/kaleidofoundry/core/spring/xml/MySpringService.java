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
package org.kaleidofoundry.core.spring.xml;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheException;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationException;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.naming.NamingServiceException;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Jerome RADUGET
 */
@Service
public class MySpringService {

   @Autowired
   @Qualifier("myStore")
   private FileStore store;

   @Autowired
   @Qualifier("myConfig")
   private Configuration configuration;

   @Autowired
   @Qualifier("myCacheManager")
   private CacheManager cacheManager;

   @Autowired
   @Qualifier("myCache")
   private Cache<String, String> cache;

   @Autowired
   @Qualifier("myOtherCacheManager")
   private CacheManager anotherCacheManager;

   @Autowired
   @Qualifier("myOtherCache")
   private Cache<String, String> myOtherCache;

   @Autowired
   @Qualifier("myNamingService")
   private NamingService namingService;

   /**
    * @param resource resource name (relative path)
    * @throws ResourceException
    */
   public String getStoreResource(final String resource) throws ResourceException {
	return store.get(resource).getText();
   }

   /**
    * @param property
    * @return the given configuration property
    * @throws ConfigurationException
    */
   public String getConfigurationProperty(final String property) throws ConfigurationException {
	return configuration.getString(property);
   }

   /**
    * @param name cache name identifier
    * @return the given cache instance
    * @throws CacheException
    */
   public Cache<String, String> getCache(final String name) throws CacheException {
	return cacheManager.getCache(name);
   }

   /**
    * @param key key of the search value
    * @return the given cache value
    * @throws CacheException
    */
   public String getCacheValue(final String key) throws CacheException {
	// init some values for testing
	cache.put("key1", "value1");
	cache.put("key2", "value2");
	return cache.get(key);
   }

   /**
    * @param key key of the search value
    * @return the given cache value
    * @throws CacheException
    */
   public String getOtherCacheValue(final String key) throws CacheException {
	// init some values for testing
	myOtherCache.put("key11", "value11");
	myOtherCache.put("key21", "value21");
	return myOtherCache.get(key);
   }

   /**
    * @param name cache name identifier
    * @return the given cache instance
    * @throws CacheException
    */
   public Cache<String, String> getOtherCache(final String name) throws CacheException {
	return anotherCacheManager.getCache(name);
   }

   /**
    * @param <R> type of the resource
    * @param resourceName name of the resource
    * @param ressourceClass type of the resource
    * @return the local or remote resource
    * @throws NamingServiceException
    */
   public <R> R getNamingResource(final String resourceName, final Class<R> ressourceClass) throws NamingServiceException {
	return namingService.locate(resourceName, ressourceClass);
   }
}
