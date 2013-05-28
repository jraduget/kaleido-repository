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
package org.kaleidofoundry.spring.context.xml;

import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;

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

/**
 * Xml spring bean
 * 
 * @author Jerome RADUGET
 */
public class MyXmlSpringService implements MyService {

   @Autowired
   private RuntimeContext<?> myContext;

   @Autowired
   private RuntimeContext<?> myNamedContext;

   @Autowired
   private FileStore myStore;

   @Autowired
   private Configuration myConfig;

   @Autowired
   private CacheManager myDefaultCacheManager;

   @Autowired
   private CacheManager myCustomCacheManager;

   @Autowired
   private Cache<Integer, String> myDefaultCache;

   @Autowired
   private Cache<Integer, String> myCustomCache;

   // @Autowired
   @Context
   private I18nMessages myDefaultMessages;

   // @Autowired
   @Context(parameters = { @Parameter(name = BaseName, value = "i18n/messages") })
   private I18nMessages myBaseMessages;

   @Autowired
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
