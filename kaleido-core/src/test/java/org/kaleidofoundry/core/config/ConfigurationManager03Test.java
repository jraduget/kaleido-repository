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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * Test {@link ConfigurationManagerBean} with a configuration that have been registered and whose meta model have not been persisted
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationManager03Test extends AbstractConfigurationManagerTest {

   public ConfigurationManager03Test() {
	super(new ConfigurationManagerBean());
   }

   protected EntityManager em;
   protected static EntityManagerFactory emf;

   @BeforeClass
   public static void init() {
	// memorize entity manager factory in order to clean it up at end of tests
	emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();

   }

   @AfterClass
   public static void destroy() {
	UnmanagedEntityManagerFactory.close(emf);
   }

   @Before
   public void setup() throws ResourceException {

	// register configuration
	ConfigurationFactory.provides(MyConfigurationName, MyConfigurationUri);

	// current entity manager
	em = UnmanagedEntityManagerFactory.currentEntityManager();

	// begin transaction
	em.getTransaction().begin();
   }

   @After
   public void cleanup() {
	try {
	   ConfigurationFactory.unregister(MyConfigurationName);
	} catch (ResourceException ste) {
	}

	try {
	   if (em != null) {
		em.getTransaction().commit();
		UnmanagedEntityManagerFactory.close(em);
	   }
	} finally {
	   // UnmanagedEntityManagerFactory.close(emf);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#getConfigurationModel()
    */
   @Override
   @Test
   public void getConfigurationModel() {
	super.getConfigurationModel();

	ConfigurationModel configModel = configurationManager.getModel(MyConfigurationName);
	assertNotNull(configModel);
	assertNull(configModel.getId());
	assertEquals(MyConfigurationName, configModel.getName());
	assertNull(configModel.getDescription());
	assertEquals(MyConfigurationUri, configModel.getUri());
	assertNotNull(configModel.getProperties());
	assertEquals(2, configModel.getProperties().size());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#getPropertyValue()
    */
   @Override
   public void getPropertyValue() {
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertEquals(String.class, property.getType());
	assertEquals("123.45", property.getValue());
   }

   @Override
   @Test
   public void removeConfigurationModel() {
	try {
	   configurationManager.removeModel(MyConfigurationName);
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}
   }
}
