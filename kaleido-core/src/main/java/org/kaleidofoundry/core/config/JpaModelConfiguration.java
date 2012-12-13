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

import static org.kaleidofoundry.core.persistence.PersistenceConstants.KaleidoPersistentContextUnitName;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindConfigurationByName;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;

/**
 * @author Jerome RADUGET
 */
// @Stateful(mappedName = "ejb/configuration/model")
// @Singleton
@Declare(ConfigurationConstants.JpaModelConfigurationPluginName)
public class JpaModelConfiguration extends AbstractConfiguration {

   /** injected entity manager */
   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   EntityManager em;

   /**
    * @param context
    * @throws ResourceException
    */
   public JpaModelConfiguration(final RuntimeContext<Configuration> context) throws ResourceException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public JpaModelConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   /**
    * @see AbstractConfiguration#AbstractConfiguration()
    */
   JpaModelConfiguration() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> loadProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException {

	ConfigurationModel configurationModel;
	Query query = em.createQuery(Query_FindConfigurationByName.Jql);
	query.setParameter(Query_FindConfigurationByName.Parameter_ConfigurationName, getName());

	try {
	   configurationModel = (ConfigurationModel) query.getSingleResult();
	} catch (NoResultException nre) {
	   configurationModel = null;
	}

	if (configurationModel == null) { throw new ConfigurationNotFoundException(getName()); }

	for (ConfigurationProperty cp : configurationModel.getProperties()) {
	   properties.put(cp.getName(), cp.getValue());
	}

	return properties;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> storeProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException {

	ConfigurationModel configurationModel;
	Query query = em.createQuery(Query_FindConfigurationByName.Jql);
	query.setParameter(Query_FindConfigurationByName.Parameter_ConfigurationName, getName());

	try {
	   configurationModel = (ConfigurationModel) query.getSingleResult();
	} catch (NoResultException nre) {
	   configurationModel = new ConfigurationModel(getName(), getResourceUri());
	   configurationModel.setLoaded(isLoaded());
	   configurationModel.setUpdateable(isUpdateable());
	   configurationModel.setStorable(isStorable());
	}

	// create configuration model if needed
	em.merge(configurationModel);

	// create each current configuration property
	for (String key : keySet()) {
	   ConfigurationProperty prop;
	   Serializable value = getProperty(key);
	   if (configurationModel.getPropertiesByName().containsKey(key)) {
		prop = configurationModel.getPropertiesByName().get(key);
	   } else {
		prop = new ConfigurationProperty(key, value != null ? value.getClass() : String.class);
	   }
	   prop.setValue(value);
	   prop.getConfigurations().add(configurationModel);
	   configurationModel.getProperties().add(prop);
	   em.merge(prop);
	}

	em.merge(configurationModel);
	em.flush();

	return properties;
   }

}
