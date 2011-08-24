/*
 *  Copyright 2008-2011 the original author or authors.
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
package org.kaleidofoundry.core.context.jee6;

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ProviderCode;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheProvidersEnum;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.naming.NamingService;

/**
 * @author Jerome RADUGET
 */
@Stateless(mappedName = "ejb/MyServiceBean6")
public class MyServiceBean implements MyServiceRemoteBean {

   public MyServiceBean() {
	// the fields injection order is not guaranteed with CDI... something myCustomCacheManager is processed after myCustomCache
	CacheManagerFactory.provides(CacheProvidersEnum.infinispan4x.name(), new RuntimeContext<CacheManager>("myCustomCacheManager"));
   }

   @Inject
   @Context
   private RuntimeContext<?> myContext;

   @Inject
   @Context("namedCtx")
   private RuntimeContext<?> myNamedContext;

   @Inject
   @Context(parameters = { @Parameter(name = FileStoreUri, value = "classpath:/config/myConfig.properties") })
   private Configuration myConfig;

   @Inject
   @Context
   private CacheManager myDefaultCacheManager;

   @Inject
   @Context(parameters = { @Parameter(name = ProviderCode, value = "infinispan4x") })
   private CacheManager myCustomCacheManager;

   @Inject
   @Context
   private Cache<Integer, String> myDefaultCache;

   @Inject
   @Context(parameters = { @Parameter(name = CacheName, value = "myNamedCache"), @Parameter(name = CacheManagerRef, value = "myCustomCacheManager") })
   private Cache<Integer, String> myCustomCache;

   @Inject
   @Context
   private I18nMessages myDefaultMessages;

   @Inject
   @Context(parameters = { @Parameter(name = BaseName, value = "i18n/messages") })
   private I18nMessages myBaseMessages;

   @Inject
   @Context
   private NamingService myNamingService;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyContext()
    */
   @Override
   public RuntimeContext<?> getMyContext() {
	return myContext;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyNamedContext()
    */
   @Override
   public RuntimeContext<?> getMyNamedContext() {
	return myNamedContext;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyConfig()
    */
   @Override
   public Configuration getMyConfig() {
	return myConfig;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyDefaultCacheManager()
    */
   @Override
   public CacheManager getMyDefaultCacheManager() {
	return myDefaultCacheManager;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyCustomCacheManager()
    */
   @Override
   public CacheManager getMyCustomCacheManager() {
	return myCustomCacheManager;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyDefaultCache()
    */
   @Override
   public Cache<Integer, String> getMyDefaultCache() {
	return myDefaultCache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyCustomCache()
    */
   @Override
   public Cache<Integer, String> getMyCustomCache() {
	return myCustomCache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyDefaultMessages()
    */
   @Override
   public I18nMessages getMyDefaultMessages() {
	return myDefaultMessages;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyBaseMessages()
    */
   @Override
   public I18nMessages getMyBaseMessages() {
	return myBaseMessages;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyService#getMyNamingService()
    */
   @Override
   public NamingService getMyNamingService() {
	return myNamingService;
   }

}
