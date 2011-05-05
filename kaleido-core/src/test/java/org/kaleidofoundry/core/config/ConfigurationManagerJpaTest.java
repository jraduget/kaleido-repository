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
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.entity.ConfigurationEntity;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class ConfigurationManagerJpaTest extends Assert {

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerJpaTest.class);

   private static EntityManagerFactory emf;

   /**
    * create a database with mocked data
    */
   @BeforeClass
   public static void setupStatic() {

	EntityManager em = null;
	EntityTransaction et = null;

	try {

	   // memorize entity manager factory in order to clean it up at end of tests
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();

	   // current entity manager
	   em = UnmanagedEntityManagerFactory.currentEntityManager();
	   et = em.getTransaction();

	   et.begin();

	   ConfigurationEntity entity = new ConfigurationEntity();
	   entity.setUri("file:/host/path");
	   entity.setName("myNamedConfig");
	   entity.setDescription("description");
	   entity.setProperties(new HashSet<ConfigurationProperty>());

	   entity.getProperties().add(new ConfigurationProperty("key01", "value01", String.class, "descr01"));
	   entity.getProperties().add(new ConfigurationProperty("key02", "123.45", Float.class, "descr02"));

	   em.merge(entity);

	   et.commit();

	} catch (final RuntimeException re) {
	   LOGGER.error("static setup", re);
	   throw re;
	} finally {
	   if (em != null) {
		UnmanagedEntityManagerFactory.close(em);
	   }
	}
   }

   @AfterClass
   public static void cleanupStatic() {
	if (emf != null) {
	   UnmanagedEntityManagerFactory.close(emf);
	}
   }

   private ConfigurationManagerBean configurationManager = null;

   @Before
   public void setup() {
	configurationManager = new ConfigurationManagerBean();
   }

   @Test
   public void getProperty() throws ClassNotFoundException {
	ConfigurationProperty property = configurationManager.getProperty("myNamedConfig", "key02");
	assertNotNull(property);
	assertNotNull(property.getId());
	assertEquals("key02", property.getName());
	assertEquals("descr02", property.getDescription());
	assertEquals(Float.class, property.getType());
	assertEquals("123.45", property.getValue());

	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());

   }
}
