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

import static org.kaleidofoundry.core.config.ConfigurationConstants.ConfigurationPluginName;
import static org.kaleidofoundry.core.config.ConfigurationConstants.JavaEnvProperties;
import static org.kaleidofoundry.core.config.ConfigurationConstants.JavaEnvPropertiesSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.JavaEnvPropertiesValueSeparator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.config.ConfigurationConstants.Extension;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration factory provider
 * 
 * @author Jerome RADUGET
 */
public abstract class ConfigurationFactory {

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationFactory.class);

   private static boolean INIT_LOADED = false;

   /**
    * configuration registry instance
    */
   private static final ConfigurationRegistry REGISTRY = new ConfigurationRegistry();

   /**
    * @return Main configuration registry
    */
   public static final ConfigurationRegistry getRegistry() {
	return REGISTRY;
   }

   /**
    * Create / Load / Register all configurations, that have been declared via java system environment <br/>
    * <br/>
    * If load have already be called, it does nothing more.
    * <p>
    * <b>How to declare the configuration to load ?</b> <br/>
    * use the java env variable {@link ConfigurationConstants#JavaEnvProperties} <br/>
    * -Dkaleido.configurations=configurationId01:configurationUri01;configurationId02:configurationUri02;...
    * 
    * <pre>
    * 	java -Dkaleido.configurations=datasource:classpath:/datasource.properties;otherResource:http:/host/path/otherResource;...  ...
    * </pre>
    * 
    * </p>
    * 
    * @throws StoreException
    * @see ConfigurationConstants#JavaEnvProperties
    */
   public static final void initConfigurations() throws StoreException {

	if (!INIT_LOADED) {
	   synchronized (ConfigurationFactory.class) {

		final StringTokenizer strConfigToken = new StringTokenizer(System.getProperty(JavaEnvProperties), JavaEnvPropertiesSeparator);
		while (strConfigToken.hasMoreTokens()) {
		   final String[] configItem = StringHelper.split(strConfigToken.nextToken(), JavaEnvPropertiesValueSeparator);
		   // named declaration
		   if (configItem.length == 2) {
			provideConfiguration(configItem[0], configItem[1]);
		   }
		   // anonymous declaration
		   else if (configItem.length == 1) {
			provideConfiguration(configItem[0], configItem[0]);
		   }
		}
		INIT_LOADED = true;
	   }
	}
   }

   /**
    * Unload / Unregister / Destroy all registered configurations
    * 
    * @throws StoreException
    */
   public static final void destroyConfigurations() throws StoreException {

	if (INIT_LOADED) {
	   synchronized (ConfigurationFactory.class) {
		for (final Configuration configuration : getRegistry().values()) {
		   configuration.unload();
		}

		getRegistry().clear();

		INIT_LOADED = false;
	   }
	}
   }

   /**
    * Provide a new configuration instance, by scanning resource uri type extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xml,</li>
    * <li>.xmlproperties,</li>
    * <li>.javasystem,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * enumeration of handle extension can be found at {@link Extension}<br/>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param identifier
    * @param resourceURI
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws StoreException
    */
   public static Configuration provideConfiguration(@NotNull final String identifier, @NotNull final String resourceURI) throws StoreException {
	return provideConfiguration(identifier, resourceURI, new RuntimeContext<Configuration>());
   }

   /**
    * Provide a new configuration instance, by scanning resource uri type extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xml,</li>
    * <li>.xmlproperties,</li>
    * <li>.javasystem,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * enumeration of handle extension can be found at {@link Extension}<br/>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param identifier
    * @param resourceURI
    * @param runtimeContext
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws StoreException
    */
   public static Configuration provideConfiguration(@NotNull final String identifier, @NotNull final String resourceURI,
	   @NotNull final RuntimeContext<Configuration> runtimeContext) throws StoreException {
	return provideConfiguration(identifier, resourceURI != null ? URI.create(resourceURI) : null, runtimeContext);
   }

   /**
    * Provide a new configuration instance, by scanning resource uri type extension :
    * <ul>
    * <li>.properties,</li>
    * <li>.xml,</li>
    * <li>.xmlproperties,</li>
    * <li>.javasystem,</li>
    * <li>...</li>
    * </ul>
    * <br/>
    * enumeration of handle extension can be found at {@link Extension}<br/>
    * <br/>
    * <b>Configuration is loaded before to be provided.</b> <br/>
    * 
    * @param identifier
    * @param resourceURI resource identifier
    * @param runtimeContext
    * @return configuration instance (loaded) which map to the resourceURI
    * @throws StoreException
    */
   public static Configuration provideConfiguration(@NotNull final String identifier, @NotNull final URI resourceURI,
	   @NotNull final RuntimeContext<Configuration> runtimeContext) throws StoreException {

	final Configuration configuration = REGISTRY.get(identifier);

	if (configuration == null) {
	   Configuration newInstance;
	   // create it
	   newInstance = createConfiguration(identifier, resourceURI, runtimeContext);
	   // load it
	   newInstance.load();
	   // register it
	   REGISTRY.put(identifier, newInstance);
	   // info
	   LOGGER.info(InternalBundleHelper.ConfigurationMessageBundle.getMessage("config.load.info", identifier, resourceURI.toString()));
	   // result
	   return newInstance;
	} else {
	   if (!configuration.isLoaded()) {
		configuration.load();
	   }
	   // re-check uri coherence ?
	   return configuration;
	}

   }

   /**
    * @param identifier
    * @param resourceURI
    * @param runtimeContext
    * @return new configuration instance which map to the resourceURI
    * @throws StoreException
    */
   private static Configuration createConfiguration(@NotNull final String identifier, @NotNull final URI resourceURI,
	   @NotNull final RuntimeContext<Configuration> runtimeContext) throws StoreException {

	final Set<Plugin<Configuration>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(Configuration.class);

	// scan each @Declare resource store implementation, to get one which handle the uri scheme
	for (final Plugin<Configuration> pi : pluginImpls) {
	   final Class<? extends Configuration> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		final String uriPath = resourceURI.getPath().toLowerCase();
		final String pluginConfigExtention = declarePlugin.value().replace(ConfigurationPluginName, "").toLowerCase();

		if (uriPath.endsWith(pluginConfigExtention)) {
		   final Constructor<? extends Configuration> constructor = impl.getConstructor(String.class, URI.class, RuntimeContext.class);
		   return constructor.newInstance(identifier, resourceURI, runtimeContext);
		}

	   } catch (final NoSuchMethodException e) {
		throw new StoreException("store.resource.factory.create.NoSuchMethodException", impl.getName());
	   } catch (final InstantiationException e) {
		throw new StoreException("store.resource.factory.create.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new StoreException("store.resource.factory.create.IllegalAccessException=ResourceStore", impl.getName());
	   } catch (final InvocationTargetException e) {
		throw new StoreException("store.resource.factory.create.InvocationTargetException", e.getCause(), impl.getName(), e.getMessage());
	   }
	}

	throw new StoreException("store.resource.uri.custom.notmanaged", resourceURI.getScheme());
   }

}
