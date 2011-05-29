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
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.config.entity.FireChangesReport;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Configuration properties manager
 * 
 * @author Jerome RADUGET
 */
@Local
@Remote
public interface ConfigurationManager {

   /**
    * @param config
    * @return the requested configuration
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws ConfigurationException
    */
   ConfigurationModel getConfigurationModel(@NotNull String config) throws ConfigurationNotFoundException, ConfigurationException;

   /**
    * get the raw property value
    * 
    * @param config
    * @param property
    * @param type
    * @return the property value (converted as type T)
    * @throws ConfigurationNotFoundException
    * @throws PropertyNotFoundException
    * @param <T>
    */
   <T extends Serializable> T getPropertyValue(final String config, final String property, final Class<T> type) throws ConfigurationNotFoundException,
	   PropertyNotFoundException;

   /**
    * get the property value as a string
    * 
    * @param config
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
    * @param config
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
    * @param config
    * @param property
    * @param value the new value to set
    * @return the old property value
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration meta model. Call
    *            {@link #putProperty(String, ConfigurationProperty)} to set a new one
    * @throws ConfigurationException if configuration is not yet loaded
    */
   String setPropertyValue(String config, String property, String value) throws ConfigurationNotFoundException, PropertyNotFoundException,
	   ConfigurationException;

   /**
    * get the property
    * 
    * @param config
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
    * @param config
    * @param property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws ConfigurationException
    */
   @NotNull
   void putProperty(@NotNull String config, @NotNull ConfigurationProperty property) throws ConfigurationNotFoundException, ConfigurationException;

   /**
    * @param config
    * @param property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws ConfigurationException if configuration is not yet loaded
    */
   @NotNull
   void removeProperty(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, PropertyNotFoundException,
	   ConfigurationException;

   /**
    * <p>
    * For the top class properties example, implements by {@link PropertiesConfiguration} :
    * 
    * <pre>
    * configuration.keySet()= {&quot;//application/name&quot;, &quot;//application/version&quot;, &quot;//application/description&quot;, &quot;//application/date&quot;, &quot;//application/librairies&quot;, "application.modules.sales", ...}
    * </pre>
    * 
    * </p>
    * 
    * @param config
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @return a set (clone) of all the declared property keys <br/>
    */
   @NotNull
   List<String> keys(@NotNull String config) throws ConfigurationNotFoundException;

   /**
    * @param config
    * @param prefix prefix key name filtering
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    * @return a set (clone) of all declared property keys filtered by prefix argument
    */
   @NotNull
   List<String> keys(@NotNull String config, @NotNull String prefix) throws ConfigurationNotFoundException;

   /**
    * @param config
    * @param key property key to find
    * @return <code>true</code>if key exists, <code>false</code> otherwise
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   boolean containsKey(@NotNull String config, @NotNull String key) throws ConfigurationNotFoundException;

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
    * @param config
    * @see Configuration#store()
    * @throws StoreException
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void store(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * load the configuration
    * 
    * @param config
    * @throws StoreException
    * @see Configuration#load()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void load(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * unload the configuration
    * 
    * @param config
    * @throws StoreException
    * @see Configuration#unload()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void unload(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * reload the configuration
    * 
    * @param config
    * @throws StoreException
    * @see Configuration#reload()
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   void reload(@NotNull String config) throws ConfigurationNotFoundException, StoreException;

   /**
    * @param config
    * @return <code>true|false</code>
    * @throws ConfigurationNotFoundException if configuration can't be found in registry or in database model
    */
   boolean isLoaded(@NotNull String config) throws ConfigurationNotFoundException;

}
