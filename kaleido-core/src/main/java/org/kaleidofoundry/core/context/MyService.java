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
package org.kaleidofoundry.core.context;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.store.FileStore;

/**
 * A service sample interface only use for integration tests (ejb, cdi, spring, guice...)
 * 
 * @author Jerome RADUGET
 */
public interface MyService {

   /**
    * @return kaleido default entity manager
    */
   EntityManager getEntityManager();

   /**
    * @return kaleido default entity manager factory
    */
   EntityManagerFactory getEntityManagerFactory();

   /**
    * @return the myContext
    */
   RuntimeContext<?> getMyContext();

   /**
    * @return the myNamedContext
    */
   RuntimeContext<?> getMyNamedContext();

   /**
    * @return the myStore
    */
   FileStore getMyStore();

   /**
    * @return the myConfig
    */
   Configuration getMyConfig();

   /**
    * @return the myDefaultCacheManager
    */
   CacheManager getMyDefaultCacheManager();

   /**
    * @return the myCustomCacheManager
    */
   CacheManager getMyCustomCacheManager();

   /**
    * @return the myDefaultCache
    */
   Cache<Integer, String> getMyDefaultCache();

   /**
    * @return the myCustomCache
    */
   Cache<Integer, String> getMyCustomCache();

   /**
    * @return the myDefaultMessages
    */
   I18nMessages getMyDefaultMessages();

   /**
    * @return the myBaseMessages
    */
   I18nMessages getMyBaseMessages();

   /**
    * @return the myNamingService
    */
   NamingService getMyNamingService();

}