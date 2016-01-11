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
package org.kaleidofoundry.spring.context.xml;

import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;

import javax.inject.Inject;
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

/**
 * Xml spring bean
 * 
 * @author jraduget
 */
public class MyXmlCdiSpringService implements MyService {

   @Inject
   private RuntimeContext<?> myContext;

   @Inject
   private RuntimeContext<?> myNamedContext;

   @Inject
   private FileStore myStore;

   @Inject
   private Configuration myConfig;

   @Inject
   private CacheManager myDefaultCacheManager;

   @Inject
   private CacheManager myCustomCacheManager;

   @Inject
   private Cache<Integer, String> myDefaultCache;

   @Inject
   private Cache<Integer, String> myCustomCache;

   // @Inject
   @Context
   private I18nMessages myDefaultMessages;

   // @Inject
   @Context(parameters = { @Parameter(name = BaseName, value = "i18n/messages") })
   private I18nMessages myBaseMessages;

   @Inject
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

}
