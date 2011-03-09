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

import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * handle configuration properties access
 * 
 * @author Jerome RADUGET
 */
@Local
@Remote
public interface ConfigurationManager {

   /**
    * get the raw property value
    * 
    * @param config
    * @param property
    * @return the raw property value
    * @throws ConfigurationNotFoundException if configuration can't be found in registry
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws IllegalStateException if configuration is not yet loaded
    */
   @NotNull
   Serializable getPropertyValue(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, IllegalStateException;

   /**
    * get the property
    * 
    * @param config
    * @param property
    * @return the raw property value
    * @throws ConfigurationNotFoundException if configuration can't be found in registry
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws IllegalStateException if configuration is not yet loaded
    */
   @NotNull
   ConfigurationProperty getProperty(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, IllegalStateException;

   /**
    * @param config
    * @param property
    * @param value
    * @return the updated property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry
    * @throws IllegalStateException if configuration is not yet loaded
    */
   @NotNull
   ConfigurationProperty setPropertyValue(@NotNull String config, @NotNull String property, String value) throws ConfigurationNotFoundException,
	   IllegalStateException;

   /**
    * @param config
    * @param property
    * @return the updated property
    * @throws ConfigurationNotFoundException
    * @throws IllegalStateException
    */
   @NotNull
   ConfigurationProperty setProperty(@NotNull String config, @NotNull ConfigurationProperty property) throws ConfigurationNotFoundException,
	   IllegalStateException;

   /**
    * @param config
    * @param property
    * @return the removed property
    * @throws ConfigurationNotFoundException if configuration can't be found in registry
    * @throws PropertyNotFoundException if property can't be found in configuration
    * @throws IllegalStateException if configuration is not yet loaded
    */
   @NotNull
   ConfigurationProperty removeProperty(@NotNull String config, @NotNull String property) throws ConfigurationNotFoundException, IllegalStateException;

   /**
    * <p>
    * For the top class properties example, implements by{@link PropertiesConfiguration} :
    * 
    * <pre>
    * configuration.keySet()= {&quot;//application/name&quot;, &quot;//application/version&quot;, &quot;//application/description&quot;, &quot;//application/date&quot;, &quot;//application/librairies&quot;, "application.modules.sales", ...}
    * </pre>
    * 
    * </p>
    * 
    * @param config
    * @return a set (clone) of all the declared property keys <br/>
    */
   @NotNull
   List<ConfigurationProperty> keys(@NotNull String config);

   /**
    * @param config
    * @param prefix prefix key name filtering
    * @return a set (clone) of all declared property keys filtered by prefix argument
    */
   @NotNull
   List<ConfigurationProperty> keys(@NotNull String config, @NotNull String prefix);

   /**
    * @param config
    * @param key property key to find
    * @return <code>true</code>if key exists, <code>false</code> otherwise
    */
   boolean containsKey(@NotNull String config, @NotNull String key);

   /**
    * fire all the last configuration changes to the registered class instances
    * 
    * @return number of configurations changes which have been fired
    */
   int fireChanges(@NotNull String config);
}
