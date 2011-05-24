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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ConfigurationMessageBundle;
import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationModelConstants.Query_FindConfigurationByName;
import org.kaleidofoundry.core.config.entity.ConfigurationModelConstants.Query_FindPropertyByName;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.config.entity.FireChangesReport;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Configuration manager (REST or JPA) used to managed configuration properties <br/>
 * <p>
 * <ul>
 * <li>jee5 : http://blogs.sun.com/enterprisetechtips/entry/implementing_restful_web_services_in</li>
 * <li>jee6 : http://java.sun.com/developer/technicalArticles/WebServices/jax-rs/index.html</li>
 * <li>jax-rs 1.0 : http://wikis.sun.com/display/Jersey/Overview+of+JAX-RS+1.0+Features</li>
 * <li>htpp : http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html</li>
 * </ul>
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Stateless(mappedName = "ejb/configuration")
@Path("/configurations/{config}")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Task(labels = TaskLabel.Defect, comment = "restore 'implements ConfigurationManager which cause a bug' - I open a GF3.x bug for this : GLASSFISH-16199")
public class ConfigurationManagerBean implements ConfigurationManager {

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   /** injected entity manager */
   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   EntityManager em;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getConfiguration(java.lang.String)
    */
   @GET
   @Path("/")
   public ConfigurationModel getConfigurationModel(final @PathParam("config") String config) throws ConfigurationNotFoundException, IllegalStateException {
	final ConfigurationModel configurationModel;
	if (em == null) {
	   Configuration configuration = getRegisteredConfiguration(config);
	   configurationModel = new ConfigurationModel(config);
	   for (String key : configuration.keySet()) {
		configurationModel.getProperties().add(getRegisteredProperty(config, key, false));
	   }
	} else {
	   configurationModel = findConfigurationModelByName(config, true);
	}
	return configurationModel;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getProperty(java.lang.String, java.lang.String)
    */
   @GET
   @Path("get/{property}")
   public Serializable getPropertyValue(final @PathParam("config") String config, final @PathParam("property") String property) {
	try {
	   Serializable value = getRegisteredConfiguration(config).getProperty(property);
	   if (value == null) { throw new PropertyNotFoundException(config, property); }
	   return value;
	} catch (ConfigurationNotFoundException cne) {
	   return getProperty(config, property).getValue();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#setProperty(java.lang.String, java.lang.String, java.io.Serializable)
    */
   @GET
   @Path("set/{property}")
   public Serializable setPropertyValue(final @PathParam("config") String config, @PathParam("property") final String property,
	   @QueryParam("value") final Serializable value) {

	boolean foundConfiguration = false;
	boolean foundConfigurationProperty = false;
	Serializable oldValue = null;

	// get registered configuration and set the property value
	try {
	   Configuration configuration = getRegisteredConfiguration(config);
	   oldValue = configuration.getProperty(property);
	   configuration.setProperty(property, value);
	   foundConfiguration = true;
	   foundConfigurationProperty = true;
	} catch (ConfigurationNotFoundException cne) {
	}

	if (em != null && !foundConfigurationProperty) {
	   // get model configuration and set the property value
	   try {
		ConfigurationProperty configurationProperty = findConfigurationPropertyByName(config, property, true);
		if (!foundConfigurationProperty) {
		   oldValue = configurationProperty.getValue();
		}
		configurationProperty.setValue(value);
		em.merge(configurationProperty);
		em.flush();
		foundConfigurationProperty = true;
	   } catch (ConfigurationNotFoundException cnfe) {
		if (!foundConfiguration) { throw cnfe; }
	   }
	}

	if (!foundConfigurationProperty) { throw new PropertyNotFoundException(config, property); }

	// check that property exists in the model
	return oldValue;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getProperty(java.lang.String, java.lang.String)
    */
   @GET
   @Path("getProperty/{property}")
   public ConfigurationProperty getProperty(final @PathParam("config") String config, final @PathParam("property") String property) {
	return findConfigurationPropertyByName(config, property, true);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#putProperty(java.lang.String, ConfigurationProperty)
    */
   @PUT
   @Path("putProperty")
   @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   public void putProperty(final @PathParam("config") String config, final ConfigurationProperty property) {
	ConfigurationProperty currentProperty = findConfigurationPropertyByName(config, property.getName(), false);
	// meta data update
	if (em != null) {
	   ConfigurationModel configurationModel = getConfigurationModel(config);
	   configurationModel.getProperties().add(property);
	   property.getConfigurations().add(configurationModel);
	   // if it is a new property creation
	   if (currentProperty == null) {
		em.persist(property);
	   }
	   // if it is an update property
	   else {
		property.setId(currentProperty.getId());
		em.merge(property);
	   }
	   em.merge(configurationModel);
	   em.flush();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#removeProperty(java.lang.String, java.lang.String)
    */
   @DELETE
   @Path("remove/{property}")
   public void removeProperty(final @PathParam("config") String config, @PathParam("property") final String property) {
	boolean foundConfiguration = false;
	boolean foundConfigurationProperty = false;
	if (em != null) {
	   ConfigurationModel configurationModel = findConfigurationModelByName(config, false);
	   if (configurationModel != null) {
		foundConfiguration = true;
		ConfigurationProperty configurationProperty = configurationModel.getPropertiesByName().get(property);
		if (configurationProperty != null) {
		   foundConfigurationProperty = true;
		   // remove it from meta data database
		   configurationModel.getProperties().remove(configurationProperty);
		   em.merge(configurationModel);
		   em.remove(em.merge(configurationProperty));
		   em.flush();
		}
	   }
	}
	try {
	   Configuration configuration = getRegisteredConfiguration(config);
	   // check if property exists
	   if (!foundConfigurationProperty && !configuration.containsKey(property)) { throw new PropertyNotFoundException(config, property); }
	   // remove it from registered configuration
	   configuration.removeProperty(property);
	} catch (ConfigurationNotFoundException cnfe) {
	   if (!foundConfiguration) { throw cnfe; }
	   if (!foundConfigurationProperty) { throw new PropertyNotFoundException(config, property); }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#keySet(java.lang.String)
    */
   public List<String> keys(final @PathParam("config") String config) {
	return keys(config, null);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#keySet(java.lang.String, java.lang.String)
    */
   @GET
   @Path("keys")
   public List<String> keys(final @PathParam("config") String config, @QueryParam("prefix") final String prefix) {
	final List<String> keys = new ArrayList<String>();
	try {
	   // first attempt in the registered configuration
	   keys.addAll(StringHelper.isEmpty(prefix) ? getRegisteredConfiguration(config).keySet() : getRegisteredConfiguration(config).keySet(prefix));
	} catch (ConfigurationNotFoundException cnfe) {
	   // second attempt in the databases
	   String normalizePrefix = !StringHelper.isEmpty(prefix) ? AbstractConfiguration.normalizeKey(prefix) : prefix;
	   ConfigurationModel model = findConfigurationModelByName(config, true);
	   for (ConfigurationProperty p : model.getProperties()) {
		if (!StringHelper.isEmpty(p.getName()) && (StringHelper.isEmpty(prefix) || p.getName().startsWith(normalizePrefix))) {
		   keys.add(p.getName());
		}
	   }
	}
	return keys;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#containsKey(java.lang.String, java.lang.String, java.lang.String)
    */
   @GET
   @Path("contains/{property}")
   public boolean containsKey(final @PathParam("config") String config, @PathParam("property") final String key) {
	return keys(config, null).contains(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#fireChanges(java.lang.String)
    */
   @GET
   @Path("fireChanges")
   public FireChangesReport fireChanges(@PathParam("config") final String config) {
	return getRegisteredConfiguration(config).fireConfigurationChangesEvents();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#store(java.lang.String)
    */
   @PUT
   @Path("store")
   public void store(@PathParam("config") final String config) throws StoreException {

	// store configuration resource file
	Configuration configuration = getRegisteredConfiguration(config);
	configuration.store();

	// store configuration model
	if (em != null) {
	   ConfigurationModel model = findConfigurationModelByName(config, false);
	   if (model != null) {
		for (String name : configuration.keySet()) {
		   ConfigurationProperty property = model.getPropertiesByName().get(name);
		   if (property != null) {
			property.setValue(configuration.getProperty(name));
			em.merge(property);
		   }

		}
	   }
	   em.flush();
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#load(java.lang.String)
    */
   @PUT
   @Path("load")
   public void load(@PathParam("config") final String config) throws StoreException {
	getRegisteredConfiguration(config).load();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#unload(java.lang.String)
    */
   @PUT
   @Path("unload")
   public void unload(@PathParam("config") final String config) throws StoreException {
	getRegisteredConfiguration(config).unload();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#reload(java.lang.String)
    */
   @PUT
   @Path("reload")
   public void reload(@PathParam("config") final String config) throws StoreException {
	getRegisteredConfiguration(config).reload();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#isLoaded(java.lang.String)
    */
   @PUT
   @Path("isLoaded")
   public boolean isLoaded(@PathParam("config") final String config) throws ConfigurationNotFoundException {
	return getRegisteredConfiguration(config).isLoaded();
   }

   // ** private / protected part ***********************************************************************************************

   /**
    * @param configName
    * @return the given configuration search by name
    * @throws ConfigurationNotFoundException if configuration can't be found in current registry
    * @throws IllegalStateException if configuration is not loaded
    */
   protected Configuration getRegisteredConfiguration(final String configName) throws ConfigurationNotFoundException {
	Configuration config = ConfigurationFactory.getRegistry().get(configName);
	if (config == null) {
	   throw new ConfigurationNotFoundException(configName);
	} else {
	   if (!config.isLoaded()) {
		throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.notloaded", configName));
	   } else {
		return config;
	   }
	}
   }

   /**
    * @param configName
    * @param propertyName
    * @param checkPropExists
    * @return configuration property
    * @throws ConfigurationNotFoundException
    * @throws PropertyNotFoundException if checkPropExists is true and if configuration property can't be found in current registry
    */
   protected ConfigurationProperty getRegisteredProperty(final String configName, final String propertyName, final boolean checkPropExists)
	   throws ConfigurationNotFoundException, PropertyNotFoundException {
	Configuration configuration = getRegisteredConfiguration(configName);
	if (checkPropExists) {
	   if (!configuration.containsKey(propertyName, "")) { throw new PropertyNotFoundException(configName, propertyName); }
	}
	Serializable value = configuration.getProperty(propertyName);
	Class<?> type = value != null ? value.getClass() : null;
	return new ConfigurationProperty(propertyName, configuration.getString(propertyName), type, DefaultDescription);
   }

   /**
    * @param config
    * @param checkConfigExists
    * @return configuration meta data
    * @throws ConfigurationNotFoundException if checkConfigExists is true and if configuration can't be found in current registry
    */
   protected ConfigurationModel findConfigurationModelByName(final String config, final boolean checkConfigExists) throws ConfigurationNotFoundException {

	ConfigurationModel configurationModel = null;

	// first check in the database model
	if (em != null) {
	   // JPA 1.x for jee5 compatibility
	   Query query = em.createQuery(Query_FindConfigurationByName.Jql);
	   query.setParameter(Query_FindConfigurationByName.Parameter_ConfigurationName, config);

	   try {
		configurationModel = (ConfigurationModel) query.getSingleResult();
	   } catch (NoResultException nre) {
		configurationModel = null;
	   }
	}
	if (checkConfigExists) {
	   if (configurationModel == null) { throw new ConfigurationNotFoundException(config); }
	}

	return configurationModel;
   }

   /**
    * @param config
    * @param propertyName
    * @param checkPropExists
    * @return property meta data
    * @throws ConfigurationNotFoundException
    * @throws PropertyNotFoundException
    */
   protected ConfigurationProperty findConfigurationPropertyByName(final String config, final String propertyName, final boolean checkPropExists)
	   throws ConfigurationNotFoundException, PropertyNotFoundException {

	// check that configuration is registered or have been persist
	try {
	   getRegisteredConfiguration(config);
	} catch (ConfigurationNotFoundException cnfe) {
	   findConfigurationModelByName(config, true);
	}

	ConfigurationProperty property = null;

	// first check in the database model
	if (em != null) {
	   // JPA 1.x for jee5 compatibility
	   Query query = em.createQuery(Query_FindPropertyByName.Jql);
	   query.setParameter(Query_FindPropertyByName.Parameter_Name, AbstractConfiguration.normalizeKey(propertyName));
	   query.setParameter(Query_FindPropertyByName.Parameter_ConfigurationName, config);

	   try {
		property = (ConfigurationProperty) query.getSingleResult();
	   } catch (NoResultException nre) {
	   }
	}

	if (property == null && checkPropExists) { throw new PropertyNotFoundException(config, propertyName); }

	return property;
   }

   /** default property description for a non persistent database property */
   public static final String DefaultDescription = "<no persistent property description>";
}
