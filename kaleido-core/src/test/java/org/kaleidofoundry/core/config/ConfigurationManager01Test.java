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

import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test {@link ConfigurationManagerBean} with a configuration model that have been persisted, but not registered
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationManager01Test extends AbstractConfigurationManagerTest {

   public ConfigurationManager01Test() {
	super(new ConfigurationManagerBean());
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager01Test.class);

   protected EntityManager em;
   protected EntityManagerFactory emf;

   /**
    * create a database with mocked data
    */
   @Before
   public void setup() {

	try {

	   // memorize entity manager factory in order to clean it up at end of tests
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();

	   // current entity manager
	   em = UnmanagedEntityManagerFactory.currentEntityManager();

	   // begin transaction
	   em.getTransaction().begin();

	   // create configuration meta model
	   ConfigurationModel configurationModel = new ConfigurationModel();
	   configurationModel.setUri(MyConfigurationUri);
	   configurationModel.setName(MyConfigurationName);
	   configurationModel.setDescription("description");
	   configurationModel.setProperties(new HashSet<ConfigurationProperty>());

	   ConfigurationProperty property;

	   property = new ConfigurationProperty("//key01", "value01", String.class, "descr01");
	   property.getConfigurations().add(configurationModel);
	   configurationModel.getProperties().add(property);

	   property = new ConfigurationProperty("//key02", 123.45f, Float.class, "descr02");
	   property.getConfigurations().add(configurationModel);
	   configurationModel.getProperties().add(property);

	   em.persist(configurationModel);

	   em.flush();

	} catch (final RuntimeException re) {
	   LOGGER.error("static setup", re);
	   throw re;
	}
   }

   @After
   public void cleanup() {

	try {
	   if (em != null) {
		em.getTransaction().commit();
		UnmanagedEntityManagerFactory.close(em);
	   }
	} finally {
	   if (emf != null) {
		UnmanagedEntityManagerFactory.close(emf);
	   }
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
	assertNotNull(configModel.getId());
	assertEquals(MyConfigurationName, configModel.getName());
	assertEquals("description", configModel.getDescription());
	assertEquals(MyConfigurationUri, configModel.getUri());
	assertNotNull(configModel.getProperties());
	assertEquals(2, configModel.getProperties().size());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#getProperty()
    */
   @Override
   public void getProperty() {
	super.getProperty();
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key01");
	assertNotNull(property.getId());
	assertEquals("descr01", property.getDescription());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#getPropertyValue()
    */
   @Override
   public void getPropertyValue() {
	super.getPropertyValue();
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertNotNull(property.getId());
	assertEquals("descr02", property.getDescription());
	assertEquals(Float.class, property.getType());
	assertEquals(123.45f, property.getValue());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#store()
    */
   @Override
   @Test
   public void store() {
	// nothing to do
   }

}
