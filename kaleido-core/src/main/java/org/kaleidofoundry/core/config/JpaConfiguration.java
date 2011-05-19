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
package org.kaleidofoundry.core.config;

import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.store.FileHandler;
import org.kaleidofoundry.core.store.SingleFileStore;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class JpaConfiguration extends AbstractConfiguration {

   /** injected entity manager */
   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   EntityManager em;

   /**
    * @param context
    * @throws StoreException
    */
   public JpaConfiguration(final RuntimeContext<Configuration> context) throws StoreException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public JpaConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(name, resourceUri, context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.FileHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   @NotYetImplemented
   protected Cache<String, Serializable> loadProperties(final FileHandler resourceHandler, final Cache<String, Serializable> properties) throws StoreException,
	   ConfigurationException {
	return null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.cache.Cache,
    * org.kaleidofoundry.core.store.SingleFileStore)
    */
   @Override
   @NotYetImplemented
   protected Cache<String, Serializable> storeProperties(final Cache<String, Serializable> properties, final SingleFileStore fileStore) throws StoreException,
	   ConfigurationException {
	return null;
   }

}
