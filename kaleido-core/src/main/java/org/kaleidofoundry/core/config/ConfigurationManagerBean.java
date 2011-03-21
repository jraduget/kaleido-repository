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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

import org.kaleidofoundry.core.config.entity.ConfigurationEntity;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.config.entity.FireChangesReport;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
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
@Review(category = ReviewCategoryEnum.ImplementIt, comment = "JPA implementation and tests")
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
   public ConfigurationEntity getConfigurationEntity(final @PathParam("config") String config) throws ConfigurationNotFoundException, IllegalStateException {
	Configuration configuration = getConfiguration(config);
	// TODO entity-manager jpa access (field description, ...) if no jpa defined, use the following code
	ConfigurationEntity configurationEntity = new ConfigurationEntity(configuration.getName());

	for (String key : configuration.keySet()) {
	   configurationEntity.getProperties().add(getConfigurationProperty(config, key, false));
	}
	return configurationEntity;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#getProperty(java.lang.String, java.lang.String)
    */
   @GET
   @Path("get/{property}")
   public Serializable getPropertyValue(final @PathParam("config") String config, final @PathParam("property") String property) {
	Serializable value = getConfiguration(config).getProperty(property);
	if (value == null) { throw new PropertyNotFoundException(config, property); }
	return value;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#setProperty(java.lang.String, java.lang.String, java.io.Serializable)
    */
   @GET
   @Path("set/{property}")
   public ConfigurationProperty setPropertyValue(final @PathParam("config") String config, @PathParam("property") final String property,
	   @QueryParam("value") final String value) {
	ConfigurationProperty configProperty = getConfigurationProperty(config, property, false);
	getConfiguration(config).setProperty(property, value);
	return configProperty;
   }

   @GET
   @Path("getProperty/{property}")
   public ConfigurationProperty getProperty(final @PathParam("config") String config, final @PathParam("property") String property) {
	return getConfigurationProperty(config, property, true);
   }

   @PUT
   @Path("putProperty")
   @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   @Review(category = ReviewCategoryEnum.ImplementIt, comment = "JPA implementation")
   public void putProperty(final @PathParam("config") String config, final ConfigurationProperty property) {
	ConfigurationProperty configProperty = getConfigurationProperty(config, property.getName(), false);
	configProperty.getName();
	// TODO JPA update
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#removeProperty(java.lang.String, java.lang.String)
    */
   @DELETE
   @Path("remove/{property}")
   public void removeProperty(final @PathParam("config") String config, @PathParam("property") final String property) {
	// throw not found exception if not found
	getConfigurationProperty(config, property, true);
	// remove it
	getConfiguration(config).removeProperty(property);
	// TODO JPA remove
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#keySet(java.lang.String)
    */
   public List<ConfigurationProperty> keys(final @PathParam("config") String config) {
	return keys(config, null);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#keySet(java.lang.String, java.lang.String)
    */
   @GET
   @Path("keys")
   public List<ConfigurationProperty> keys(final @PathParam("config") String config, @QueryParam("prefix") final String prefix) {
	Set<ConfigurationProperty> properties = new HashSet<ConfigurationProperty>();
	Set<String> keys = StringHelper.isEmpty(prefix) ? getConfiguration(config).keySet() : getConfiguration(config).keySet(prefix);
	for (String key : keys) {
	   properties.add(getConfigurationProperty(config, key, false));
	}
	return new ArrayList<ConfigurationProperty>(properties);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManager#containsKey(java.lang.String, java.lang.String, java.lang.String)
    */
   @GET
   @Path("contains/{property}")
   public boolean containsKey(final @PathParam("config") String config, @PathParam("property") final String key) {
	return getConfiguration(config).containsKey(key, null);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#fireChanges(java.lang.String)
    */
   @GET
   @Path("fireChanges")
   public FireChangesReport fireChanges(@PathParam("config") final String config) {
	return getConfiguration(config).fireConfigurationChangesEvents();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#store(java.lang.String)
    */
   @PUT
   @Path("store")
   public void store(@PathParam("config") final String config) throws StoreException {
	getConfiguration(config).store();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#load(java.lang.String)
    */
   @PUT
   @Path("load")
   public void load(@PathParam("config") final String config) throws StoreException {
	getConfiguration(config).load();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#unload(java.lang.String)
    */
   @PUT
   @Path("unload")
   public void unload(@PathParam("config") final String config) throws StoreException {
	getConfiguration(config).unload();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#reload(java.lang.String)
    */
   @PUT
   @Path("reload")
   public void reload(@PathParam("config") final String config) throws StoreException {
	getConfiguration(config).reload();
   }

   // ** private / protected part ***********************************************************************************************

   /**
    * @param name
    * @return the given configuration search by name
    * @throws ConfigurationNotFoundException if configuration can't be found
    * @throws IllegalStateException if configuration is not loaded
    */
   protected Configuration getConfiguration(final String name) throws ConfigurationNotFoundException {
	Configuration config = ConfigurationFactory.getRegistry().get(name);
	if (config == null) {
	   // TODO entity-manager jpa access (field description, uri ...) if no jpa defined, use the following code
	   throw new ConfigurationNotFoundException(name);
	} else {
	   if (!config.isLoaded()) {
		throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.notloaded", name));
	   } else {
		return config;
	   }
	}
   }

   /**
    * @param config
    * @param propertyName
    * @param checkPropExists
    * @return property meta data
    */
   protected ConfigurationProperty getConfigurationProperty(final String config, final String propertyName, final boolean checkPropExists) {
	Configuration configuration = getConfiguration(config);
	if (checkPropExists) {
	   if (!configuration.containsKey(propertyName, "")) { throw new PropertyNotFoundException(config, propertyName); }
	}
	Serializable value = configuration.getProperty(propertyName);
	Class<?> type = value != null ? value.getClass() : null;

	// TODO entity-manager jpa access (field description, uri ...) if no jpa defined, use the following code
	ConfigurationEntity configurationEntity = new ConfigurationEntity(configuration.getName(), "", "");
	ConfigurationProperty property = new ConfigurationProperty(configurationEntity, propertyName, configuration.getString(propertyName), type, "");
	return property;
   }

}
