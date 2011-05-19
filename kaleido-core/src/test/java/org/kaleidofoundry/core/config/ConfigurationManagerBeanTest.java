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

import java.io.Serializable;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class ConfigurationManagerBeanTest extends Assert {

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagerBeanTest.class);

   private static EntityManagerFactory emf;

   private static final String MyConfigurationName = "myNamedConfig";
   private static final String MyConfigurationUri = "classpath:/config/myNamedConfig.properties";

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

	   et.commit();

	   // register configuration
	   // ConfigurationFactory.provides(MyConfigurationName, MyConfigurationUri);

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
   public void getConfigurationModel() {
	try {
	   configurationManager.getConfigurationModel("unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	ConfigurationModel configModel = configurationManager.getConfigurationModel(MyConfigurationName);
	assertNotNull(configModel);
	assertNotNull(configModel.getId());
	assertEquals(MyConfigurationName, configModel.getName());
	assertEquals("description", configModel.getDescription());
	assertEquals(MyConfigurationUri, configModel.getUri());
	assertNotNull(configModel.getProperties());
	assertEquals(2, configModel.getProperties().size());
   }

   @Test
   public void getProperty() {
	try {
	   configurationManager.getPropertyValue(MyConfigurationName, "//unknown");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}

	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key01");
	assertNotNull(property);
	assertNotNull(property.getId());
	assertEquals("//key01", property.getName());
	assertEquals("descr01", property.getDescription());
	assertEquals(String.class, property.getType());
	assertEquals("value01", property.getValue());
	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());
   }

   @Test
   public void getPropertyValue() {
	try {
	   configurationManager.getPropertyValue(MyConfigurationName, "unknown");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertNotNull(property);
	assertNotNull(property.getId());
	assertEquals("//key02", property.getName());
	assertEquals("descr02", property.getDescription());
	assertEquals(Float.class, property.getType());
	assertEquals(123.45f, property.getValue());
	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());
   }

   @Test
   public void setPropertyValue() {
	try {
	   configurationManager.setPropertyValue(MyConfigurationName, "//unknown", "");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}
	Serializable oldValue = configurationManager.setPropertyValue(MyConfigurationName, "//key02", 678.9f);
	assertNotNull(oldValue);
	assertEquals(123.45f, oldValue);
	assertEquals(678.9f, configurationManager.getPropertyValue(MyConfigurationName, "//key02"));

	// store have not been called, check that property value don't change in the persistence layer
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertNotNull(property);
	assertEquals(123.45f, property.getValue());
   }

   @Test
   public void putProperty() {

	try {
	   configurationManager.getProperty(MyConfigurationName, "//newKey");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}

	ConfigurationProperty property = new ConfigurationProperty("//newKey", "newValue", String.class, "newDescription");
	assertNull(property.getId());
	configurationManager.putProperty(MyConfigurationName, property);

	property = configurationManager.getProperty(MyConfigurationName, "//newKey");
	assertNotNull(property);
	assertNotNull(property.getId());
	assertEquals("//newKey", property.getName());
	assertEquals("newDescription", property.getDescription());
	assertEquals(String.class, property.getType());
	assertEquals("newValue", property.getValue());
	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());

	// clean
	configurationManager.removeProperty(MyConfigurationName, "//newKey");

   }

   @Test
   public void removeProperty() {

	try {
	   configurationManager.removeProperty(MyConfigurationName, "//unknown");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}

	assertFalse(configurationManager.keys(MyConfigurationName).contains("//newKey"));

	ConfigurationProperty property = new ConfigurationProperty("//newKey", "newValue", String.class, "newDescription");
	assertNull(property.getId());
	configurationManager.putProperty(MyConfigurationName, property);

	assertTrue(configurationManager.keys(MyConfigurationName).contains("//newKey"));
	assertEquals(3, configurationManager.keys(MyConfigurationName));

	configurationManager.removeProperty(MyConfigurationName, "//newKey");

	assertFalse(configurationManager.keys(MyConfigurationName).contains("//newKey"));
	assertEquals(2, configurationManager.keys(MyConfigurationName));
   }

   @Test
   public void keys() {

	try {
	   configurationManager.keys("//unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	assertNotNull(configurationManager.keys(MyConfigurationName));
	assertTrue(configurationManager.keys(MyConfigurationName).contains("//key01"));
	assertTrue(configurationManager.keys(MyConfigurationName).contains("//key02"));
	assertEquals(2, configurationManager.keys(MyConfigurationName).size());
   }

   @Test
   public void containsKey() {
	assertTrue(configurationManager.containsKey(MyConfigurationName, "//key01"));
	assertTrue(configurationManager.containsKey(MyConfigurationName, "//key02"));
	assertFalse(configurationManager.containsKey(MyConfigurationName, "//key03"));
   }

   @Test
   public void store() {
	fail("TODO");
   }

}
