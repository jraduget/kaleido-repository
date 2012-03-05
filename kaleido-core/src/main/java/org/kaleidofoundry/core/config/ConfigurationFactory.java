/*
 * Copyright 2008-2010 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.config;

import static org.kaleidofoundry.core.config.ConfigurationConstants.JavaEnvProperties;
import static org.kaleidofoundry.core.config.ConfigurationConstants.JavaEnvPropertiesSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.JavaEnvPropertiesValueSeparator;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.config.ConfigurationConstants.Extension;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Configuration factory provider
 * <p>
 * <b>How to declare the configuration to load ?</b> <br/>
 * <br/>
 * Define the following java environment variable {@link ConfigurationConstants#JavaEnvProperties} like this :
 * <p>
 * <code>
 * -Dkaleido.configurations=configurationName01:configurationUri01;configurationName02:configurationUri02;...
 * </code>
 * </p>
 * <p>
 * <b>Example :</b> <br/>
 * <code>
 * 	java -Dkaleido.configurations=datasource:classpath:/datasource.properties;otherResource:http:/host/path/otherResource;...  ...
 * </code>
 * </p>
 * </p>
 * 
 * @author Jerome RADUGET
 * @see ConfigurationProvider delegate configuration creation & registry
 */
public abstract class ConfigurationFactory {

   // does default init Configurations have occurred
   private static boolean INIT_LOADED = false;

   // configuration provider used by the factory
   private static final ConfigurationProvider CONFIGURATION_PROVIDER = new ConfigurationProvider(Configuration.class);

   /**
    * Create / Load / Register all configurations, that have been declared via java system environment <br/>
    * <br/>
    * If load have already be called, it does nothing more.
    * 
    * @throws ResourceException
    * @see ConfigurationConstants#JavaEnvProperties
    */
   public static synchronized final void init() throws ResourceException {

	if (!INIT_LOADED) {
	   final StringTokenizer strConfigToken = new StringTokenizer(System.getProperty(JavaEnvProperties), JavaEnvPropertiesSeparator);
	   while (strConfigToken.hasMoreTokens()) {
		final String configItemStr = strConfigToken.nextToken().trim();
		final String[] configItem = StringHelper.split(configItemStr, JavaEnvPropertiesValueSeparator);
		// named declaration
		if (configItem.length == 2) {
		   provides(configItem[0].trim(), configItem[1].trim());
		}
		// anonymous declaration
		else if (configItem.length == 1) {
		   provides(configItem[0].trim(), configItem[0].trim());
		}
	   }
	   INIT_LOADED = true;
	}

   }

   /**
    * Unload / Unregister / Destroy all registered configurations
    * 
    * @throws ResourceException
    */
   public static synchronized final void unregisterAll() throws ResourceException {

	for (final Configuration configuration : getRegistry().values()) {
	   configuration.unload();
	}

	getRegistry().clear();

	INIT_LOADED = false;

   }

   /**
    * Unload / Unregister / Destroy the given configuration
    * 
    * @param configName
    * @throws ResourceException
    * @throws ConfigurationNotFoundException
    */
   public static final void unregister(@NotNull final String configName) throws ConfigurationNotFoundException, ResourceException {

	final Configuration configToDestroy = getRegistry().get(configName);

	if (configToDestroy != null) {
	   if (configToDestroy.isLoaded()) {
		configToDestroy.unload();
	   }
	   getRegistry().remove(configName);
	} else {
	   throw new ConfigurationNotFoundException(configName);
	}
   }

   /**
    * does configuration is registered
    * 
    * @param config
    * @return <code>true|false</code>
    */
   public static boolean isRegistered(final String config) {
	return ConfigurationFactory.getRegistry().containsKey(config);
   }

   /**
    * @return shortcut to main configuration registry stored in {@link ConfigurationProvider}
    */
   public static final ConfigurationRegistry getRegistry() {
	return ConfigurationProvider.getRegistry();
   }

   /**
    * Provides a new configuration instance
    * <p>
    * To known what is the configuration format, it scans the resource path URI extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xmlproperties,</li>
    * <li>.xml,</li>
    * <li>.javasystem,</li>
    * <li>.mainargs,</li>
    * <li>.osenv,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * a default enumeration of the handle extensions can be found at {@link Extension}<br/>
    * </p>
    * <br/>
    * <p>
    * To known the configuration store to use, it scans the scheme of the URI :
    * <ul>
    * <li>file:/,</li>
    * <li>http://,</li>
    * <li>ftp://,</li>
    * <li>classpath:/,</li>
    * <li>webapp:/,</li>
    * <li>jpa://,</li>
    * <li>jdbc://,</li>
    * </ul>
    * </p>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param runtimeContext see {@link ConfigurationContextBuilder} informations for common context properties, and specific implementation
    *           class
    *           if needed
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    * @see Configuration
    */
   public static Configuration provides(final RuntimeContext<Configuration> runtimeContext) {
	return CONFIGURATION_PROVIDER.provides(runtimeContext);
   }

   /**
    * Provides a new configuration instance
    * <p>
    * To known what is the configuration format, it scans the resource path URI extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xmlproperties,</li>
    * <li>.xml,</li>
    * <li>.javasystem,</li>
    * <li>.mainargs,</li>
    * <li>.osenv,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * a default enumeration of the handle extensions can be found at {@link Extension}<br/>
    * </p>
    * <br/>
    * <p>
    * To known the configuration store to use, it scans the scheme of the URI :
    * <ul>
    * <li>file:/,</li>
    * <li>http://,</li>
    * <li>ftp://,</li>
    * <li>classpath:/,</li>
    * <li>webapp:/,</li>
    * <li>jpa://,</li>
    * <li>jdbc://,</li>
    * </ul>
    * </p>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param name configuration name (unique identifier)
    * @param resourceURI
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    * @see Configuration
    */
   public static Configuration provides(@NotNull final String name, @NotNull final String resourceURI) {
	return CONFIGURATION_PROVIDER.provides(name, resourceURI);
   }

   /**
    * Provides a new configuration instance
    * <p>
    * To known what is the configuration format, it scans the resource path URI extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xmlproperties,</li>
    * <li>.xml,</li>
    * <li>.javasystem,</li>
    * <li>.mainargs,</li>
    * <li>.osenv,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * a default enumeration of the handle extensions can be found at {@link Extension}<br/>
    * </p>
    * <br/>
    * <p>
    * To known the configuration store to use, it scans the scheme of the URI :
    * <ul>
    * <li>file:/,</li>
    * <li>http://,</li>
    * <li>ftp://,</li>
    * <li>classpath:/,</li>
    * <li>webapp:/,</li>
    * <li>jpa://,</li>
    * <li>jdbc://,</li>
    * </ul>
    * </p>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param name configuration name (unique identifier)
    * @param resourceURI configuration resource URI
    * @param runtimeContext see {@link ConfigurationContextBuilder} informations for common context properties, and specific implementation
    *           class
    *           if needed
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    * @see Configuration
    */
   public static Configuration provides(@NotNull final String name, @NotNull final String resourceURI,
	   @NotNull final RuntimeContext<Configuration> runtimeContext) {
	return CONFIGURATION_PROVIDER.provides(name, resourceURI, runtimeContext);
   }

   /**
    * Provides a new configuration instance
    * <p>
    * To known what is the configuration format, it scans the resource path URI extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xmlproperties,</li>
    * <li>.xml,</li>
    * <li>.javasystem,</li>
    * <li>.mainargs,</li>
    * <li>.osenv,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * a default enumeration of the handle extensions can be found at {@link Extension}<br/>
    * </p>
    * <br/>
    * <p>
    * To known the configuration store to use, it scans the scheme of the URI :
    * <ul>
    * <li>file:/,</li>
    * <li>http://,</li>
    * <li>ftp://,</li>
    * <li>classpath:/,</li>
    * <li>webapp:/,</li>
    * <li>jpa://,</li>
    * <li>jdbc://,</li>
    * </ul>
    * </p>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param name configuration name (unique identifier)
    * @param resourceURI configuration resource URI
    * @param runtimeContext see {@link ConfigurationContextBuilder} informations for common context properties, and specific implementation
    *           class
    *           if needed
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    * @see Configuration
    */
   public static Configuration provides(@NotNull final String name, @NotNull final URI resourceURI, @NotNull final RuntimeContext<Configuration> runtimeContext) {

	return CONFIGURATION_PROVIDER.provides(name, resourceURI, runtimeContext);

   }

}
