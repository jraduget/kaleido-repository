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
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindAllConfiguration;
import org.kaleidofoundry.core.lang.label.model.Labels;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test {@link ConfigurationController} with a configuration model that have been persisted, but not registered
 * 
 * @author jraduget
 */
public class ConfigurationManager01Test extends AbstractConfigurationManagerTest {

   public ConfigurationManager01Test() {
	super(new ConfigurationController());
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager01Test.class);

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


   /**
    * create a database with mocked data
    */
   @Before
   public void setup() {

	try {

	   // current entity manager
	   em = UnmanagedEntityManagerFactory.currentEntityManager();

	   // begin transaction
	   em.getTransaction().begin();

	   // remove all configuration entries
	   Query query = em.createQuery(Query_FindAllConfiguration.Jql);
	   for (Object model : query.getResultList()) {
		em.remove(model);
	   }
	   em.flush();

	   // create configuration meta model
	   ConfigurationModel configurationModel = new ConfigurationModel();
	   configurationModel.setUri(MyConfigurationUri);
	   configurationModel.setName(MyConfigurationName);
	   configurationModel.setDescription("description");
	   configurationModel.setProperties(new HashSet<ConfigurationProperty>());

	   ConfigurationProperty property;

	   property = new ConfigurationProperty("//key01", "value01", String.class, "descr01", new Labels(new String[] { "APP" }));
	   property.getConfigurations().add(configurationModel);
	   configurationModel.getProperties().add(property);

	   property = new ConfigurationProperty("//key02", 123.45f, Float.class, "descr02", new Labels(new String[] { "APP", "VERSION" }));
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
	assertNotNull(configModel.getId());
	assertEquals(MyConfigurationName, configModel.getName());
	assertEquals("description", configModel.getDescription());
	assertEquals(MyConfigurationUri, configModel.getUri());
	assertNotNull(configModel.getProperties());
	assertEquals(2, configModel.getProperties().size());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#findConfigurationModel()
    */
   @Override
   @Test
   public void findConfigurationModel() {

	super.findConfigurationModel();

	List<ConfigurationModel> models;

	// search in description
	models = configurationManager.findModel("description");
	assertNotNull(models);
	assertFalse(models.isEmpty());
	assertEquals(1, models.size());

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

	assertNotNull(property.getLabels());
	Iterator<String> labels = property.getLabels().iterator();
	assertEquals("APP", labels.next());
	assertFalse(labels.hasNext());

	property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertNotNull(property.getId());
	assertEquals("descr02", property.getDescription());
	assertNotNull(property.getLabels());
	labels = property.getLabels().iterator();
	assertEquals("APP", labels.next());
	assertEquals("VERSION", labels.next());
	assertFalse(labels.hasNext());
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

   @Override
   @Test
   public void findProperties() {
	super.findProperties();

	List<ConfigurationProperty> properties;

	// search in description
	properties = configurationManager.findProperties(MyConfigurationName, "descr");
	assertNotNull(properties);
	assertFalse(properties.isEmpty());
	assertEquals(2, properties.size());
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
