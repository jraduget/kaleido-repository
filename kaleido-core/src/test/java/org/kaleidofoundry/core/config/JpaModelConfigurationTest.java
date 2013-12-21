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

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindAllConfiguration;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.store.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
public class JpaModelConfigurationTest extends AbstractConfigurationTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(JpaModelConfigurationTest.class);

   protected static final String MyConfigurationName = "jpaModelConfig";
   protected static final String MyConfigurationUri = "memory:/config/test.model";

   protected static EntityManagerFactory emf;
   protected EntityManager em;

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
    * 
    * @throws URISyntaxException
    * @throws ResourceException
    */
   @Override
   @Before
   public void setup() throws ResourceException, URISyntaxException {

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
	   configurationModel.setUri(MyConfigurationName);
	   configurationModel.setName(MyConfigurationName);
	   configurationModel.setProperties(new HashSet<ConfigurationProperty>());
	   em.persist(configurationModel);

	   Set<ConfigurationProperty> properties = new HashSet<ConfigurationProperty>();
	   properties.add(new ConfigurationProperty("application.name", "app", String.class, ""));
	   properties.add(new ConfigurationProperty("application.version", "1.0.0", String.class, ""));
	   properties.add(new ConfigurationProperty("application.description", "description of the application...", String.class, ""));
	   properties.add(new ConfigurationProperty("application.date", "2006-09-01T00:00:00", String.class, ""));
	   properties.add(new ConfigurationProperty("application.librairies", "dom4j.jar|log4j.jar|mail.jar", String.class, ""));
	   properties.add(new ConfigurationProperty("application.modules.sales.name", "Sales", String.class, ""));
	   properties.add(new ConfigurationProperty("application.modules.sales.version", "1.1.0", String.class, ""));
	   properties.add(new ConfigurationProperty("application.modules.marketing.name", "Market.", String.class, ""));
	   properties.add(new ConfigurationProperty("application.modules.netbusiness.name", "", String.class, ""));
	   properties.add(new ConfigurationProperty("application.array.boolean", "false|true", String.class, ""));
	   properties.add(new ConfigurationProperty("application.array.date", "2009-01-02T00:00:00|2009-12-31T00:00:00|2012-05-15T00:00:00", String.class, ""));
	   properties.add(new ConfigurationProperty("application.array.bigdecimal", "987.5|1.123456789", String.class, ""));
	   properties.add(new ConfigurationProperty("application.single.boolean", "true", String.class, ""));
	   properties.add(new ConfigurationProperty("application.single.bigdecimal", "1.123456789", String.class, ""));

	   for (ConfigurationProperty property : properties) {
		property.getConfigurations().add(configurationModel);
		configurationModel.getProperties().add(property);
		em.persist(property);
	   }

	   em.merge(configurationModel);
	   em.flush();

	   super.setup();

	} catch (final RuntimeException re) {
	   LOGGER.error("static setup", re);
	   throw re;
	}
   }

   @Override
   @After
   public void cleanup() throws ResourceException {

	try {
	   if (em != null) {
		em.getTransaction().commit();
		UnmanagedEntityManagerFactory.close(em);
	   }
	} finally {
	   // UnmanagedEntityManagerFactory.close(emf);
	}

	super.cleanup();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationTest#newInstance()
    */
   @Override
   protected Configuration newInstance() throws ResourceException, URISyntaxException {
	return new org.kaleidofoundry.core.config.JpaModelConfiguration(MyConfigurationName, MyConfigurationUri,
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>(Configuration.class));
   }

}
