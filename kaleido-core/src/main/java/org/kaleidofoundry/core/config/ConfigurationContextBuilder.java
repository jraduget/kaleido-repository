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

import java.io.Serializable;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * {@link Configuration} base {@link RuntimeContext} builder & properties.<br/>
 * <p>
 * <b>{@link Configuration} commons context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <td><b>Property name</b></td>
 * <td><b>Perimeter</b></td>
 * <td><b>Property description</b></td>
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
 * <td>fileStoreUri</td>
 * <td>all</td>
 * <td>cache configuration resource uri</td>
 * </tr>
 * <tr>
 * <td>fileStoreRef</td>
 * <td>all</td>
 * <td>file store context name that will be used to access the fileStoreUri (if configuration resource uri need a specific file store for
 * authentication, or other needs..)</td>
 * </tr>
 * <tr>
 * <td>cacheManagerRef</td>
 * <td>all</td>
 * <td>cache manager context name to use, if not specify default will be used (see {@link CacheManagerFactory})</td>
 * </tr>
 * <tr>
 * <td>multiValuesSeparator</td>
 * <td>all</td>
 * <td>separator used for property having multi-values : <code>;|,| </code></td>
 * </tr>
 * <tr>
 * <td>dateTimeFormat</td>
 * <td>all</td>
 * <td>used for property having a date time format : <code>yyyy-MM-dd'T'hh:mm:ss</code></td>
 * </tr>
 * <tr>
 * <td>numberFormat</td>
 * <td>all</td>
 * <td>used for property having a number format : <code>##0.0####</code></td>
 * </tr>
 * <tr>
 * <td><b>Property name</b></td>
 * <td><b>Perimeter</b></td>
 * <td><b>Property description</b></td>
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
 * @author jraduget
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
   /** common configuration context property - file store {@link URI} to accessed the configuration file */
   public static final String FileStoreUri = "fileStoreUri";
   /** common configuration context property - file store context name that will be used to access the fileStoreUri */
   public static final String FileStoreRef = "fileStoreRef";
   /** common configuration context property - cache manager context name to use */
   public static final String CacheManagerRef = "cacheManagerRef";

   /** common - separator used for property having multi-values : <code>;|,| </code> */
   public static final String MultiValuesSeparator = "multiValuesSeparator";
   /** common - used for property having a date format : <code>yyyy-MM-dd'T'hh:mm:ss */
   public static final String DateTimeFormat = "dateTimeFormat";
   /** common - used for property having a number format : <code>##0.0#### */
   public static final String NumberFormat = "numberFormat";

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
    * @param staticParameters
    */
   public ConfigurationContextBuilder(final Class<Configuration> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public ConfigurationContextBuilder(final Class<Configuration> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public ConfigurationContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
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
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public ConfigurationContextBuilder(final String name, final Class<Configuration> pluginInterface,
	   final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
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
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public ConfigurationContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
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
    * @param fileStoreUri
    * @return set fileStoreUri context parameter
    */
   public ConfigurationContextBuilder withFileStoreUri(final String fileStoreUri) {
	getContextParameters().put(FileStoreUri, fileStoreUri);
	return this;
   }

   /**
    * @param fileStoreRef
    * @return set fileStoreRef context parameter
    */
   public ConfigurationContextBuilder withFileStoreRef(final String fileStoreRef) {
	getContextParameters().put(FileStoreRef, fileStoreRef);
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

   /**
    * @param multiValuesSeparator
    * @return set main args separator context parameter
    */
   public ConfigurationContextBuilder withMultiValuesSeparator(final String multiValuesSeparator) {
	getContextParameters().put(MultiValuesSeparator, multiValuesSeparator);
	return this;
   }

   /**
    * @param dateTimeFormat
    * @return set main args separator context parameter
    */
   public ConfigurationContextBuilder withDateTimeFormat(final String dateTimeFormat) {
	getContextParameters().put(DateTimeFormat, dateTimeFormat);
	return this;
   }

   /**
    * @param numberFormat
    * @return set main args separator context parameter
    */
   public ConfigurationContextBuilder withNumberFormat(final String numberFormat) {
	getContextParameters().put(NumberFormat, numberFormat);
	return this;
   }

}
