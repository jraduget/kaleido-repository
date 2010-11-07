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

import java.net.URI;

import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * {@link Configuration} base {@link RuntimeContext} builder & properties.<br/>
 * <p>
 * <b>{@link Configuration} commons context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>
 * <td>Property name</td></th>
 * <th>
 * <td>Perimeter</td></th>
 * <th>
 * <td>Property description</td></th>
 * </tr>
 * <tr>
 * <td>name</td>
 * <td>all</td>
 * <td>unique name identifier of the configuration</td>
 * </tr>
 * <tr>
 * <td>storageAllowed</td>
 * <td>all</td>
 * <td><code>true|false</code> , active or not readonly usage (for storage) of the configuration</td>
 * </tr>
 * <tr>
 * <td>updateAllowed</td>
 * <td>all</td>
 * <td><code>true|false</code> , active or not readonly usage (no updates) of the configuration</td>
 * </tr>
 * <tr>
 * <td>resourceUri</td>
 * <td>all</td>
 * <td>cache configuration resource uri</td>
 * </tr>
 * <tr>
 * <td>resourceStoreRef</td>
 * <td>all</td>
 * <td>resource store context name that will be used to access the resourceUri (if configuration resource uri need a specific resource store
 * for authentication, or other needs..)</td>
 * </tr>
 * <tr>
 * <td>cacheManagerRef</td>
 * <td>all</td>
 * <td>cache manager context name to use, if not specify default will be used (see {@link CacheManagerFactory})</td>
 * </tr>
 * <tr>
 * <th>
 * <td>Property name</td></th>
 * <th>
 * <td>Perimeter</td></th>
 * <th>
 * <td>Property description</td></th>
 * </tr>
 * <tr>
 * <td>argsMainString</td>
 * <td>mainArgs</td>
 * <td>string representation parameters of the main arguments array</td>
 * </tr>
 * <tr>
 * <td>argsSeparator</td>
 * <td>mainArgs</td>
 * <td>string arguments separator character</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationContextBuilder extends AbstractRuntimeContextBuilder<Configuration> {

   /**
    * common configuration context property - unique name identifier of the configuration
    * 
    * @see Configuration#getName()
    */
   public static final String Name = "name";
   /** common configuration context property - read-only storage usage <code>true|false</code> value */
   public static final String StorageAllowed = "storageAllowed";
   /** common configuration context property - read-only update usage <code>true|false</code> value */
   public static final String UpdateAllowed = "updateAllowed";
   /** common configuration context property - resource store {@link URI} to accessed the configuration file */
   public static final String ResourceUri = "resourceUri";
   /** common configuration context property - resource store context name that will be used to access the resourceUri */
   public static final String ResourceStoreRef = "resourceStoreRef";
   /** common configuration context property - cache manager context name to use */
   public static final String CacheManagerRef = "cacheManagerRef";

   /**
    * {@link MainArgsConfiguration} configuration context property - string representation parameters of the main arguments array
    * 
    * @see #ArgsSeparator
    * @see MainArgsConfiguration
    */
   public static final String ArgsMainString = "argsMainString";

   /**
    * {@link MainArgsConfiguration} configuration context property - string arguments separator character
    * 
    * @see #ArgsMainString
    * @see MainArgsConfiguration
    */
   public static final String ArgsSeparator = "argsSeparator";

   /**
    * 
    */
   public ConfigurationContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public ConfigurationContextBuilder(final Class<Configuration> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    */
   public ConfigurationContextBuilder(final Class<Configuration> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param configurations
    */
   public ConfigurationContextBuilder(final Configuration... configurations) {
	super(configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public ConfigurationContextBuilder(final String name, final Class<Configuration> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public ConfigurationContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public ConfigurationContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public ConfigurationContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public ConfigurationContextBuilder(final String name) {
	super(name);
   }

   /**
    * @param name
    * @return set name context parameter
    */
   public ConfigurationContextBuilder withName(final String name) {
	getContextParameters().put(Name, name);
	return this;
   }

   /**
    * @param allowStorage
    * @return set allow storage context parameter
    */
   public ConfigurationContextBuilder withStorageAllowed(final boolean allowStorage) {
	getContextParameters().put(StorageAllowed, Boolean.valueOf(allowStorage).toString());
	return this;
   }

   /**
    * @param allowUpdate
    * @return set allow update context parameter
    */
   public ConfigurationContextBuilder withUpdateAllowed(final String allowUpdate) {
	getContextParameters().put(UpdateAllowed, allowUpdate);
	return this;
   }

   /**
    * @param resourceuri
    * @return set resourceuri context parameter
    */
   public ConfigurationContextBuilder withResourceUri(final String resourceuri) {
	getContextParameters().put(ResourceUri, resourceuri);
	return this;
   }

   /**
    * @param resourcestoreref
    * @return set resourcestoreref context parameter
    */
   public ConfigurationContextBuilder withResourceStoreRef(final String resourcestoreref) {
	getContextParameters().put(ResourceStoreRef, resourcestoreref);
	return this;
   }

   /**
    * @param cacheManagerRef
    * @return set cacheManagerRef context parameter
    */
   public ConfigurationContextBuilder withCacheManagerRef(final String cacheManagerRef) {
	getContextParameters().put(CacheManagerRef, cacheManagerRef);
	return this;
   }

   /**
    * @param args
    * @return set main args context parameter
    */
   public ConfigurationContextBuilder withMainArgsString(final String args) {
	getContextParameters().put(ArgsMainString, args);
	return this;
   }

   /**
    * @param separator
    * @return set main args separator context parameter
    */
   public ConfigurationContextBuilder withMainArgsSeparator(final String separator) {
	getContextParameters().put(ArgsSeparator, separator);
	return this;
   }

}
