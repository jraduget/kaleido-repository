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
import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class ConfigurationManagerPersistentTest extends AbstractConfigurationManagerTest {

   public ConfigurationManagerPersistentTest() {
	super(new ConfigurationManagerBean());
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerPersistentTest.class);

   private EntityManager em;
   private EntityManagerFactory emf;

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

}
