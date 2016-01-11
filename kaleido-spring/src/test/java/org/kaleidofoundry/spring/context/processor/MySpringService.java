/*
 *  Copyright 2008-2016 the original author or authors.
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
package org.kaleidofoundry.spring.context.processor;

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ProviderCode;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.BaseUri;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.MyService;
import org.kaleidofoundry.core.context.Parameter;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.store.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jraduget
 */
@Service
public class MySpringService implements MyService {

   //
   // @PersistenceContext(unitName = "kaleido")
   // private EntityManager entityManager;
   //
   // @PersistenceUnit(unitName = "kaleido")
   // private EntityManagerFactory entityManagerFactory;

   @Context
   private RuntimeContext<?> myContext;

   @Context("namedCtx")
   private RuntimeContext<?> myNamedContext;

   @Context("namedCtx")
   // @Context must be ignored by spring post processor, because classic @Autowired is used
   @Autowired
   private RuntimeContext<?> mySpringContext;

   @Context(value = "myStoreCtx", parameters = { @Parameter(name = BaseUri, value = "classpath:/store") })
   private FileStore myStore;

   @Context(parameters = { @Parameter(name = FileStoreUri, value = "classpath:/config/myConfig.properties") })
   private Configuration myConfig;

   @Context
   private CacheManager myDefaultCacheManager;

   @Context(parameters = { @Parameter(name = ProviderCode, value = "infinispan") })
   private CacheManager myCustomCacheManager;

   @Context
   private Cache<Integer, String> myDefaultCache;

   @Context(parameters = { @Parameter(name = CacheName, value = "myNamedCache"), @Parameter(name = CacheManagerRef, value = "myCustomCacheManager") })
   private Cache<Integer, String> myCustomCache;

   @Context
   private I18nMessages myDefaultMessages;

   @Context(parameters = { @Parameter(name = BaseName, value = "i18n/messages") })
   private I18nMessages myBaseMessages;

   @Context
   private NamingService myNamingService;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyContext()
    */
   @Override
   public RuntimeContext<?> getMyContext() {
	return myContext;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyNamedContext()
    */
   @Override
   public RuntimeContext<?> getMyNamedContext() {
	return myNamedContext;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyStore()
    */
   @Override
   public FileStore getMyStore() {
	return myStore;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyConfig()
    */
   @Override
   public Configuration getMyConfig() {
	return myConfig;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyDefaultCacheManager()
    */
   @Override
   public CacheManager getMyDefaultCacheManager() {
	return myDefaultCacheManager;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyCustomCacheManager()
    */
   @Override
   public CacheManager getMyCustomCacheManager() {
	return myCustomCacheManager;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyDefaultCache()
    */
   @Override
   public Cache<Integer, String> getMyDefaultCache() {
	return myDefaultCache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyCustomCache()
    */
   @Override
   public Cache<Integer, String> getMyCustomCache() {
	return myCustomCache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyDefaultMessages()
    */
   @Override
   public I18nMessages getMyDefaultMessages() {
	return myDefaultMessages;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyBaseMessages()
    */
   @Override
   public I18nMessages getMyBaseMessages() {
	return myBaseMessages;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getMyNamingService()
    */
   @Override
   public NamingService getMyNamingService() {
	return myNamingService;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getEntityManagerFactory()
    */
   @Override
   public EntityManagerFactory getEntityManagerFactory() {
	return null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyService#getEntityManager()
    */
   @Override
   public EntityManager getEntityManager() {
	return null;
   }

   /**
    * @return the mySpringContext
    */
   public RuntimeContext<?> getMySpringContext() {
	return mySpringContext;
   }

}
