/*
 *  Copyright 2008-2021 the original author or authors.
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

import static org.kaleidofoundry.core.env.model.EnvironmentConstants.KALEIDO_PERSISTENT_UNIT_NAME;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
import javax.xml.bind.annotation.XmlElementWrapper;

import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindAllConfiguration;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindConfigurationByName;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindConfigurationByText;
import org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Query_FindPropertyByName;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.config.model.FireChangesReport;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.lang.annotation.Tasks;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Configuration controller is used to expose configuration management.<br/>
 * With it you can manage the configuration model and its properties <br/>
 * It is exposed as a RESTful service or as an EJB managed bean.<br/>
 * <br/>
 * <p>
 * <h4>RESTful API urls :</h4>
 * <table border="1">
 * <tr>
 * <td>Action</td>
 * <td>Url</td>
 * <td>Filter parameter / Remarks</td>
 * </tr>
 * <tr>
 * <td>GET</td>
 * <td>/configurations/</td>
 * <td>"text" is filter to search by name, url, description</td>
 * </tr>
 * <tr>
 * <td>GET|DELETE</td>
 * <td>/configurations/{config}/</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>GET</td>
 * <td>/configurations/{config}/keys</td>
 * <td>"text" is a filter to search keys containing this token</td>
 * </tr>
 * <tr>
 * <td>GET|PUT</td>
 * <td>/configurations/{config}/{property}</td>
 * <td>for {property} use the java propety syntax like "application.version"</td>
 * </tr>
 * <tr>
 * <td>GET|PUT|DELETE</td>
 * <td>/configurations/{config}/property/{property}</td>
 * <td>for {property} use the java propety syntax like "application.version"</td>
 * </tr>
 * <tr>
 * <td>GET</td>
 * <td>/configurations/properties</td>
 * <td>"config" is a filter on the configuration name | "text" is a filter on name / description</td>
 * </tr>
 * </table>
 * <p>
 * TODO refactor following actions PUT {config}/register (Refactor some rest actions url: rest url is resources not action)<br/>
 * PUT {config}/unregister <br/>
 * GET {config}/registered<br/>
 * GET {config}/fireChanges<br/>
 * PUT {config}/store<br/>
 * PUT {config}/load<br/>
 * PUT {config}/unload<br/>
 * PUT {config}/reload<br/>
 * GET {config}/loaded<br/>
 * </p>
 * </p> <h4>RESTful resources :</h4>
 * <p>
 * <ul>
 * <li>jee5 : http://blogs.sun.com/enterprisetechtips/entry/implementing_restful_web_services_in</li>
 * <li>jee6 : http://java.sun.com/developer/technicalArticles/WebServices/jax-rs/index.html</li>
 * <li>jax-rs 1.0 : http://wikis.sun.com/display/Jersey/Overview+of+JAX-RS+1.0+Features</li>
 * <li>htpp : http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html</li>
 * </ul>
 * </p>
 * 
 * @author jraduget
 * @see Configuration
 * @see ConfigurationProperty
 */
@Stateless(mappedName = "ejb/configurations/manager")
@Path("/configurations/")
@Tasks(tasks = {
	@Task(labels = TaskLabel.Defect, comment = "restore 'implements ConfigurationController which cause a bug' - I open a GF3.x bug for this : GLASSFISH-16199"),
	@Task(labels = TaskLabel.Documentation, comment = "Refactor some rest actions url: rest url is resources not action") })
public class ConfigurationController {

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   /** injected entity manager */
   @PersistenceContext(unitName = KALEIDO_PERSISTENT_UNIT_NAME)
   EntityManager em;

   // ### Property management methods ############################################################################################

   public ConfigurationController() {
	try {
	   // em will be injected by by java ee container or if not by aspectj
	   if (em == null) {
		em = UnmanagedEntityManagerFactory.currentEntityManager(KALEIDO_PERSISTENT_UNIT_NAME);
	   }
	} catch (PersistenceException pe) {
	   em = null;
	}
   }

   /**
    * get the property value as a string
    * 
    * @param config configuration name identifier
    * @param property
    * @return the raw property value
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws ConfigurationException if configuration is not yet loaded
    */
   @GET
   @Path("/{config}/{property}")
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

   /**
    * get the raw property value
    * 
    * @param config configuration name identifier
    * @param property
    * @param type
    * @return the property value (converted as type T)
    * @throws ConfigurationNotFoundException
    * @throws PropertyNotFoundException
    * @param <T>
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

   /**
    * set / define / change the value of a property (but do not persist it. Call {@link #store(String)} to persist its value)
    * 
    * @param config configuration name identifier
    * @param property
    * @param value the new value to set
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration meta model. Call
    *            {@link #putProperty(String, ConfigurationProperty)} to set a new one
    * @throws ConfigurationException if configuration is not yet loaded
    */
   @PUT
   @Path("/{config}/{property}")
   public void setPropertyValue(final @PathParam("config") String config, @PathParam("property") final String property, @QueryParam("value") final String value) {
	setPropertyValue(config, property, value, String.class);
   }

   /**
    * set / define / change the value of a property (but do not persist it. Call {@link #store(String)} to persist its value)
    * 
    * @param config configuration name identifier
    * @param property
    * @param value the new value to set
    * @param type the type of the value
    * @return the old property value
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration meta model. Call
    *            {@link #putProperty(String, ConfigurationProperty)} to set a new one
    * @throws ConfigurationException if configuration is not yet loaded
    * @param <T>
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

   /**
    * get the property
    * 
    * @param config configuration name identifier
    * @param property
    * @return the raw property value
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws ConfigurationException if configuration is not yet loaded
    */
   @GET
   @Path("/{config}/property/{property}")
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

   /**
    * define and persist a new property in the configuration meta model
    * 
    * @param config configuration name identifier
    * @param property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws ConfigurationException
    */
   @PUT
   @Path("/{config}/property")
   @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   public void putProperty(final @PathParam("config") String config, final ConfigurationProperty property) {
	ConfigurationProperty currentProperty = findConfigurationPropertyByName(config, property.getName(), false);
	// meta data update
	if (em != null) {
	   ConfigurationModel configurationModel = getModel(config);
	   // if it is a new property creation
	   if (currentProperty == null) {
		if (configurationModel.getProperties().contains(property)) {
		   configurationModel.getProperties().remove(property);
		}
		configurationModel.getProperties().add(property);
		property.getConfigurations().add(configurationModel);
		em.persist(property);
	   }
	   // if it is an update property
	   else {
		// configurationModel.getProperties().add(currentProperty);
		// currentProperty.getConfigurations().add(configurationModel);
		currentProperty.setName(property.getName());
		currentProperty.setType(property.getType());
		currentProperty.setValue(property.getValue());
		currentProperty.setDescription(property.getDescription());
		em.merge(currentProperty);
	   }
	   em.merge(configurationModel);
	   em.flush();
	}
   }

   /**
    * remove the property from the configuration
    * 
    * @param config configuration name identifier
    * @param property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws ConfigurationException if configuration is not yet loaded
    */
   @DELETE
   @Path("/{config}/property/{property}")
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

   /**
    * @param config configuration name identifier
    * @return a set (clone) of all the declared property keys <br/>
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   public Set<ConfigurationProperty> getProperties(final String config) {
	return getModel(config).getProperties();
   }

   /**
    * finding configuration properties by using a text search in the fields {@link ConfigurationProperty#getName()},
    * {@link ConfigurationProperty#getDescription()}, {@link ConfigurationProperty#getLabels()}, {@link ConfigurationProperty#getValue()}
    * 
    * @param config configuration name identifier (not mandatory)
    * @param text text token to search
    * @return list of configuration properties matching the text argument
    */
   @GET
   @Path("/properties")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public List<ConfigurationProperty> findProperties(final @QueryParam("config") String config, @QueryParam("text") final String text) {
	final List<ConfigurationProperty> properties = new ArrayList<ConfigurationProperty>();
	boolean foundConfiguration = false;

	// if a config argument is defined
	if (!StringHelper.isEmpty(config)) {
	   // first attempt in the configuration model
	   if (em != null) {
		ConfigurationModel model = findConfigurationModelByName(config, false);
		if (model != null) {
		   foundConfiguration = true;
		   for (ConfigurationProperty p : model.getProperties()) {
			properties.add(p);
		   }
		}
	   }

	   // second attempt in the registered configurations, if a config argument is defined
	   if (!foundConfiguration) {
		Configuration configuration = getRegisteredConfiguration(config);
		for (String key : getRegisteredConfiguration(config).keySet()) {
		   properties.add(newConfigurationProperty(configuration, key));
		}
	   }

	}
	// if no config argument is given, we collect all persistent and registered properties
	else {

	   // properties from configuration model (and registry)
	   for (ConfigurationModel model : findModel(null)) {
		for (ConfigurationProperty p : model.getProperties()) {
		   properties.add(p);
		}
	   }

	}

	// filter properties using text on : name, labels, description and value
	final List<ConfigurationProperty> filterProperties = new ArrayList<ConfigurationProperty>();

	if (!StringHelper.isEmpty(text)) {
	   for (ConfigurationProperty property : properties) {
		String normalizePrefix = AbstractConfiguration.normalizeKey(text);
		if (!StringHelper.isEmpty(property.getName()) && property.getName().contains(normalizePrefix)) {
		   filterProperties.add(property);
		} else if (property.getDescription() != null && property.getDescription().contains(text)) {
		   filterProperties.add(property);
		} else if (String.valueOf(property.getValue()).contains(text)) {
		   filterProperties.add(property);
		} else if (property.getLabels() != null && property.getLabels().contains(text)) {
		   filterProperties.add(property);
		}
	   }
	} else {
	   filterProperties.addAll(properties);
	}

	return filterProperties;
   }

   /**
    * @param config
    * @param text text token to search
    * @return properties keys of the configuration
    * @throws ConfigurationNotFoundException
    */
   @GET
   @Path("/{config}/keys")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public Response keys(@PathParam("config") final String config, @QueryParam("text") final String text) throws ConfigurationNotFoundException {
	Set<String> result = keySet(config);
	return Response.ok(result).build();
   }

   /**
    * <p>
    * For the top class properties example in {@link PropertiesConfiguration} :
    * 
    * <pre>
    * 
    * configurationManager.keySet(&quot;appConfig&quot;) = {&quot;//application/name&quot;, &quot;//application/version&quot;, &quot;//application/description&quot;, &quot;//application/date&quot;, &quot;//application/librairies&quot;, "application.modules.sales", ...}
    * </pre>
    * 
    * </p>
    * 
    * @param config configuration name identifier
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @return a set (clone) of all the declared property keys <br/>
    */
   public Set<String> keySet(final String config) throws ConfigurationNotFoundException {
	Set<String> result = new HashSet<String>();
	for (ConfigurationProperty prop : getProperties(config)) {
	   result.add(prop.getName());
	}
	return result;
   }

   /**
    * For the top class properties example in {@link PropertiesConfiguration} :
    * 
    * <pre>
    * configurationManager.keySet(&quot;appConfig&quot;, &quot;application.modules&quot;)= {&quot;application.modules.sales=Sales&quot;,&quot;application.modules.sales.version=1.1.0&quot;,&quot;application.modules.marketing=Market.&quot;,&quot;application.modules.netbusiness=&quot;}
    * </pre>
    * 
    * @param config configuration name identifier
    * @param text text key filtering
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @return a set (clone) of all declared property keys filtered by prefix argument
    */
   public Set<String> keySet(final String config, final String prefix) throws ConfigurationNotFoundException {
	Set<String> result = new HashSet<String>();
	for (ConfigurationProperty prop : findProperties(config, prefix)) {
	   result.add(prop.getName());
	}
	return result;
   }

   /**
    * @param config configuration name identifier
    * @param property property key to find
    * @return <code>true</code>if key exists, <code>false</code> otherwise
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   public boolean containsKey(final String config, final String property) {
	return keySet(config, null).contains(property);
   }

   // ### Configuration management methods #######################################################################################

   /**
    * get configuration model by its name
    * 
    * @param config configuration name identifier
    * @return the requested configuration
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws ConfigurationException
    */
   @GET
   @Path("{config}/")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public ConfigurationModel getModel(final @PathParam("config") String config) throws ConfigurationNotFoundException, IllegalStateException {
	ConfigurationModel configurationModel = findConfigurationModelByName(config, false);
	if (em == null || configurationModel == null) {
	   Configuration configuration = getRegisteredConfiguration(config);
	   configurationModel = new ConfigurationModel(config, configuration.getResourceUri());
	   configurationModel.setLoaded(configuration.isLoaded());
	   configurationModel.setUpdateable(configuration.isUpdateable());
	   configurationModel.setStorable(configuration.isStorable());
	   for (String key : configuration.keySet()) {
		ConfigurationProperty property = newConfigurationProperty(configuration, key);
		property.getConfigurations().add(configurationModel);
		configurationModel.getProperties().add(property);
	   }
	}
	return configurationModel;
   }

   /**
    * finding configurations by using a text search in the fields {@link ConfigurationModel#getName()},
    * {@link ConfigurationModel#getDescription()}, {@link ConfigurationModel#getLabels()}
    * 
    * @param text
    * @return list of configuration properties matching the text argument
    */
   @GET
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   @XmlElementWrapper(name = "configurations")
   @SuppressWarnings("unchecked")
   public List<ConfigurationModel> findModel(final @QueryParam("text") String text) {

	Set<ConfigurationModel> configurationModels = new HashSet<ConfigurationModel>();

	// first check in the database model
	if (em != null) {

	   final Query query;
	   if (!StringHelper.isEmpty(text)) {
		// JPA 1.x for jee5 compatibility
		query = em.createQuery(Query_FindConfigurationByText.Jql);
		query.setParameter(Query_FindConfigurationByText.Parameter_Text, "%" + text + "%");
	   } else {
		query = em.createQuery(Query_FindAllConfiguration.Jql);
	   }

	   try {
		configurationModels.addAll(query.getResultList());
	   } catch (NoResultException nre) {
	   }
	}

	// second check in memory registry
	for (Entry<String, Configuration> entry : ConfigurationFactory.getRegistry().entrySet()) {
	   if (StringHelper.isEmpty(text) || entry.getValue().getName().contains(text) || entry.getValue().getResourceUri().contains(text)) {
		configurationModels.add(getModel(entry.getKey()));
	   }
	}

	return new ArrayList<ConfigurationModel>(configurationModels);
   }

   /**
    * does configuration model exists
    * 
    * @param config
    * @return <code>true|false</code>
    */
   public boolean exists(final String config) {
	return findConfigurationModelByName(config, false) != null;
   }

   /**
    * remove configuration model by its name (but does not unregister it)
    * 
    * @param config configuration name identifier
    * @throws ConfigurationNotFoundException
    * @throws ConfigurationException
    */
   @DELETE
   @Path("{config}")
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

   /**
    * register a configuration with the given name
    * 
    * @param config configuration name identifier
    * @param resourceURI configuration resource uri
    */
   @PUT
   @Path("{config}/register")
   public void register(final @PathParam("config") String config, final @QueryParam("resourceURI") String resourceURI) {
	ConfigurationFactory.provides(config, resourceURI);
   }

   /**
    * unregister the configuration with the given name
    * 
    * @param config configuration name identifier
    */
   @PUT
   @Path("{config}/unregister")
   public void unregister(final @PathParam("config") String config) throws ResourceException {
	ConfigurationFactory.unregister(config);
   }

   /**
    * does following configuration have been registered
    * 
    * @param config
    * @return <code>true|false</code>
    */
   public boolean isRegistered(final String config) {
	return ConfigurationFactory.isRegistered(config);
   }

   @GET
   @Path("{config}/registered")
   @Produces({ MediaType.TEXT_PLAIN })
   public Response isRegisteredForRest(final @PathParam("config") String config) {
	return Response.ok(Boolean.valueOf(isRegistered(config)).toString()).build();
   }

   /**
    * fire all the last configuration changes to the registered class instances
    * 
    * @return number of configurations changes which have been fired
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @GET
   @Path("{config}/fireChanges")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public FireChangesReport fireChanges(@PathParam("config") final String config) {
	return getRegisteredConfiguration(config).fireConfigurationChangesEvents();
   }

   /**
    * store the changes made to the configuration
    * 
    * @param config configuration name identifier
    * @see Configuration#store()
    * @throws ResourceException
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @PUT
   @Path("{config}/store")
   public void store(@PathParam("config") final String config) throws ResourceException {
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

   /**
    * load the configuration
    * 
    * @param config configuration name identifier
    * @throws ResourceException
    * @see Configuration#load()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @PUT
   @Path("{config}/load")
   public void load(@PathParam("config") final String config) throws ResourceException {
	getRegisteredConfiguration(config).load();
   }

   /**
    * unload the configuration
    * 
    * @param config configuration name identifier
    * @throws ResourceException
    * @see Configuration#unload()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @PUT
   @Path("{config}/unload")
   public void unload(@PathParam("config") final String config) throws ResourceException {
	getRegisteredConfiguration(config).unload();
   }

   /**
    * reload the configuration
    * 
    * @param config configuration name identifier
    * @throws ResourceException
    * @see Configuration#reload()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @PUT
   @Path("{config}/reload")
   public void reload(@PathParam("config") final String config) throws ResourceException {
	getRegisteredConfiguration(config).reload();
   }

   /**
    * @param config configuration name identifier
    * @return <code>true|false</code>
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   public boolean isLoaded(final String config) throws ConfigurationNotFoundException {
	return getRegisteredConfiguration(config).isLoaded();
   }

   @GET
   @Path("{config}/loaded")
   @Produces({ MediaType.TEXT_PLAIN })
   public Response isLoadedForRest(final @PathParam("config") String config) {
	return Response.ok(Boolean.valueOf(isLoaded(config)).toString()).build();
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
	   return config;
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
