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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.Response;
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
@Stateless(mappedName = "ejb/configuration/manager")
@Path("/configurations/{config}")
@Task(labels = TaskLabel.Defect, comment = "restore 'implements ConfigurationManager which cause a bug' - I open a GF3.x bug for this : GLASSFISH-16199")
public class ConfigurationManagerBean { // implements ConfigurationManager {

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   /** injected entity manager */
   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   EntityManager em;

   // ### Property management methods ############################################################################################

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getProperty(java.lang.String, java.lang.String)
    */
   @GET
   @Path("get/{property}")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public String getPropertyValue(final @PathParam("config") String config, final @PathParam("property") String property) {
	try {
	   String value = getRegisteredConfiguration(config).getString(property);
	   if (value == null) { throw new PropertyNotFoundException(config, property); }
	   return value;
	} catch (ConfigurationNotFoundException cne) {
	   return getProperty(config, property).getValue(String.class);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getProperty(java.lang.String, java.lang.String)
    */
   public <T extends Serializable> T getPropertyValue(final String config, final String property, final Class<T> type) {
	try {
	   T value = getRegisteredConfiguration(config).getProperty(property, type);
	   if (value == null) { throw new PropertyNotFoundException(config, property); }
	   return value;
	} catch (ConfigurationNotFoundException cne) {
	   return getProperty(config, property).getValue(type);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#setPropertyValue(java.lang.String, java.lang.String, java.lang.String)
    */
   @PUT
   @Path("set/{property}")
   public void setPropertyValue(final @PathParam("config") String config, @PathParam("property") final String property, @QueryParam("value") final String value) {
	setPropertyValue(config, property, value, String.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#setPropertyValue(java.lang.String, java.lang.String, java.io.Serializable,
    * java.lang.Class)
    */
   public <T extends Serializable> T setPropertyValue(final String config, final String property, final T value, final Class<T> type) {

	boolean foundConfiguration = false;
	boolean foundConfigurationProperty = false;
	T oldValue = null;

	// get registered configuration and set the property value
	try {
	   Configuration configuration = getRegisteredConfiguration(config);
	   oldValue = configuration.getProperty(property, type);
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
		   oldValue = configurationProperty.getValue(type);
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
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public ConfigurationProperty getProperty(final @PathParam("config") String config, final @PathParam("property") String property) {

	ConfigurationException ce;

	try {
	   // first, check that property exists in the model
	   return findConfigurationPropertyByName(config, property, true);
	} catch (ConfigurationNotFoundException cnfe) {
	   ce = cnfe;
	} catch (PropertyNotFoundException pne) {
	   ce = pne;
	}

	// second, build a non persistent one with the given information
	try {
	   ConfigurationProperty cp = getRegisteredProperty(config, property, true);
	   ConfigurationModel cm = getModel(config);
	   cp.getConfigurations().add(cm);
	   return cp;
	} catch (ConfigurationNotFoundException cnfe) {
	   if (ce instanceof PropertyNotFoundException) {
		throw ce;
	   } else {
		throw cnfe;
	   }
	}

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
	   ConfigurationModel configurationModel = getModel(config);
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
   @Path("removeProperty/{property}")
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
   public Set<ConfigurationProperty> getProperties(final String config) {
	Set<ConfigurationProperty> result = new HashSet<ConfigurationProperty>();
	result.addAll(getProperties(config, null));
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#keySet(java.lang.String, java.lang.String)
    */
   @GET
   @Path("properties")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public List<ConfigurationProperty> getProperties(final @PathParam("config") String config, @QueryParam("prefix") final String prefix) {
	final List<ConfigurationProperty> properties = new ArrayList<ConfigurationProperty>();
	boolean foundConfiguration = false;

	if (em != null) {
	   // first attempt in the configuration model
	   String normalizePrefix = !StringHelper.isEmpty(prefix) ? AbstractConfiguration.normalizeKey(prefix) : prefix;
	   ConfigurationModel model = findConfigurationModelByName(config, false);
	   if (model != null) {
		foundConfiguration = true;
		for (ConfigurationProperty p : model.getProperties()) {
		   if (!StringHelper.isEmpty(p.getName()) && (StringHelper.isEmpty(prefix) || p.getName().startsWith(normalizePrefix))) {
			properties.add(p);
		   }
		}
	   }
	}

	if (!foundConfiguration) {
	   // second attempt in the registered configuration
	   Configuration configuration = getRegisteredConfiguration(config);
	   Set<String> keys = StringHelper.isEmpty(prefix) ? getRegisteredConfiguration(config).keySet() : getRegisteredConfiguration(config).keySet(prefix);
	   for (String key : keys) {
		properties.add(newConfigurationProperty(configuration, key));
	   }
	}

	return properties;
   }

   /**
    * @param config
    * @return properties keys of the configuration
    * @throws ConfigurationNotFoundException
    */
   @GET
   @Path("keys")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public Response keys(@PathParam("config") final String config, @QueryParam("prefix") final String prefix) throws ConfigurationNotFoundException {
	Set<String> result = keySet(config);
	return Response.ok(result).build();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#keySet(java.lang.String)
    */
   public Set<String> keySet(final String config) throws ConfigurationNotFoundException {
	Set<String> result = new HashSet<String>();
	for (ConfigurationProperty prop : getProperties(config)) {
	   result.add(prop.getName());
	}
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#keySet(java.lang.String, java.lang.String)
    */
   public Set<String> keySet(final String config, final String prefix) throws ConfigurationNotFoundException {
	Set<String> result = new HashSet<String>();
	for (ConfigurationProperty prop : getProperties(config, prefix)) {
	   result.add(prop.getName());
	}
	return result;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#containsKey(java.lang.String, java.lang.String, java.lang.String)
    */
   @GET
   @Path("contains/{property}")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public boolean containsKey(final @PathParam("config") String config, @PathParam("property") final String property) {
	return keySet(config, null).contains(property);
   }

   // ### Configuration management methods #######################################################################################

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getModel(java.lang.String)
    */
   @GET
   @Path("/")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public ConfigurationModel getModel(final @PathParam("config") String config) throws ConfigurationNotFoundException, IllegalStateException {
	ConfigurationModel configurationModel = findConfigurationModelByName(config, false);
	if (em == null || configurationModel == null) {
	   Configuration configuration = getRegisteredConfiguration(config);
	   configurationModel = new ConfigurationModel(config, configuration.getResourceUri());
	   for (String key : configuration.keySet()) {
		ConfigurationProperty property = newConfigurationProperty(configuration, key);
		property.getConfigurations().add(configurationModel);
		configurationModel.getProperties().add(property);
	   }
	}
	return configurationModel;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#removeModel(java.lang.String)
    */
   @DELETE
   @Path("delete")
   public void removeModel(@PathParam("config") final String config) {
	ConfigurationModel configurationModel = findConfigurationModelByName(config, true);
	if (em != null) {
	   // remove orphan properties
	   for (ConfigurationProperty cp : configurationModel.getProperties()) {
		if (cp.getConfigurations().size() == 1) {
		   em.remove(cp);
		}
	   }
	   // clean current configuration properties
	   configurationModel.getProperties().clear();
	   // remove configuration and flush all changes
	   em.remove(configurationModel);
	   em.flush();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#register(java.lang.String, java.lang.String)
    */
   @PUT
   @Path("register")
   public void register(final @PathParam("config") String config, final @PathParam("resourceURI") String resourceURI) {
	ConfigurationFactory.provides(config, resourceURI);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#unregister(java.lang.String)
    */
   @PUT
   @Path("unregister")
   public void unregister(final @PathParam("config") String config) throws StoreException {
	ConfigurationFactory.unregister(config);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#fireChanges(java.lang.String)
    */
   @GET
   @Path("fireChanges")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
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
    * @throws ConfigurationException if configuration is not loaded
    */
   protected Configuration getRegisteredConfiguration(final String configName) throws ConfigurationNotFoundException {
	Configuration config = ConfigurationFactory.getRegistry().get(configName);
	if (config == null) {
	   throw new ConfigurationNotFoundException(configName);
	} else {
	   if (!config.isLoaded()) {
		throw new ConfigurationException("config.load.notloaded", configName);
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
	   if (!configuration.containsKey(propertyName)) { throw new PropertyNotFoundException(configName, propertyName); }
	}

	return newConfigurationProperty(configuration, propertyName);
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

   private ConfigurationProperty newConfigurationProperty(final Configuration configuration, final String propertyName) {
	Serializable value = configuration.getProperty(propertyName);
	Class<?> type = value != null ? value.getClass() : null;
	return new ConfigurationProperty(propertyName, configuration.getProperty(propertyName), type, DefaultDescription);
   }

   /** default property description for a non persistent database property */
   public static final String DefaultDescription = "<no persistent property description>";
}
