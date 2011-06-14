/*
 *  Copyright 2008-2010 the original author or authors.
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
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;

import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.config.entity.FireChangesReport;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Configuration properties manager used to manage the configuration model
 * For the following properties file (named "appConfig" in the following javadoc) implements by {@link PropertiesConfiguration} :
 * 
 * <pre>
 * application.name=app
 * application.version=1.0.0
 * application.description=description of the application...
 * application.date=2006-09-01T00:00:00
 * application.librairies=dom4j.jar log4j.jar mail.jar
 * 
 * application.modules.sales=Sales
 * application.modules.sales.version=1.1.0
 * application.modules.marketing=Market.
 * application.modules.netbusiness=
 * </pre>
 * 
 * @author Jerome RADUGET
 * @see Configuration
 * @see ConfigurationProperty
 */
@Local
@Remote
public interface ConfigurationManager {

   /**
    * register a configuration with the given name
    * 
    * @param config configuration name identifier
    * @param resourceURI configuration resource uri
    */
   void register(String config, String resourceURI);

   /**
    * unregister the configuration with the given name
    * 
    * @param config configuration name identifier
    */
   void unregister(String config) throws StoreException;

   /**
    * get configuration model by its name
    * 
    * @param config configuration name identifier
    * @return the requested configuration
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws ConfigurationException
    */
   ConfigurationModel getModel(@NotNull String config) throws ConfigurationNotFoundException, ConfigurationException;

   /**
    * remove configuration model by its name
    * 
    * @param config configuration name identifier
    * @throws ConfigurationNotFoundException
    * @throws ConfigurationException
    */
   void removeModel(@NotNull final String config) throws ConfigurationNotFoundException, ConfigurationException;

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
   <T extends Serializable> T getPropertyValue(@NotNull final String config, final String property, final Class<T> type) throws ConfigurationNotFoundException,
	   PropertyNotFoundException;

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
   String getPropertyValue(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, PropertyNotFoundException,
	   ConfigurationException;

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
   <T extends Serializable> T setPropertyValue(@NotNull String config, @NotNull String property, T value, Class<T> type) throws ConfigurationNotFoundException,
	   PropertyNotFoundException, ConfigurationException;

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
   void setPropertyValue(String config, String property, String value) throws ConfigurationNotFoundException, PropertyNotFoundException, ConfigurationException;

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
   @NotNull
   ConfigurationProperty getProperty(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, PropertyNotFoundException,
	   ConfigurationException;

   /**
    * define and persist a new property in the configuration meta model
    * 
    * @param config configuration name identifier
    * @param property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws ConfigurationException
    */
   @NotNull
   void putProperty(@NotNull String config, @NotNull ConfigurationProperty property) throws ConfigurationNotFoundException, ConfigurationException;

   /**
    * remove the property from the configuration
    * 
    * @param config configuration name identifier
    * @param property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws ConfigurationException if configuration is not yet loaded
    */
   @NotNull
   void removeProperty(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, PropertyNotFoundException,
	   ConfigurationException;

   /**
    * @param config configuration name identifier
    * @return a set (clone) of all the declared property keys <br/>
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @NotNull
   Set<ConfigurationProperty> getProperties(@NotNull String config) throws ConfigurationNotFoundException;

   /**
    * @param config configuration name identifier
    * @param prefix
    * @return a set (clone) of all the declared property keys <br/>
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   @NotNull
   Set<ConfigurationProperty> getProperties(@NotNull String config, @NotNull String prefix) throws ConfigurationNotFoundException;

   /**
    * <p>
    * For the top class properties example, implements by {@link PropertiesConfiguration} :
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
   @NotNull
   Set<String> keySet(@NotNull String config) throws ConfigurationNotFoundException;

   /**
    * For the top class properties example, implements by {@link PropertiesConfiguration} :
    * 
    * <pre>
    * configurationManager.keySet(&quot;appConfig&quot;, &quot;application.modules&quot;)= {&quot;application.modules.sales=Sales&quot;,&quot;application.modules.sales.version=1.1.0&quot;,&quot;application.modules.marketing=Market.&quot;,&quot;application.modules.netbusiness=&quot;}
    * </pre>
    * 
    * @param config configuration name identifier
    * @param prefix prefix key name filtering
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @return a set (clone) of all declared property keys filtered by prefix argument
    */
   @NotNull
   Set<String> keySet(@NotNull String config, @NotNull String prefix) throws ConfigurationNotFoundException;

   /**
    * @param config configuration name identifier
    * @param property property key to find
    * @return <code>true</code>if key exists, <code>false</code> otherwise
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   boolean containsKey(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException;

   /**
    * fire all the last configuration changes to the registered class instances
    * 
    * @return number of configurations changes which have been fired
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   FireChangesReport fireChanges(@NotNull String config) throws ConfigurationNotFoundException;

   /**
    * store the changes made to the configuration
    * 
    * @param config configuration name identifier
    * @see Configuration#store()
    * @throws StoreException
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void store(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * load the configuration
    * 
    * @param config configuration name identifier
    * @throws StoreException
    * @see Configuration#load()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void load(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * unload the configuration
    * 
    * @param config configuration name identifier
    * @throws StoreException
    * @see Configuration#unload()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void unload(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * reload the configuration
    * 
    * @param config configuration name identifier
    * @throws StoreException
    * @see Configuration#reload()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void reload(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * @param config configuration name identifier
    * @return <code>true|false</code>
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   boolean isLoaded(@NotNull String config) throws ConfigurationNotFoundException;

}
