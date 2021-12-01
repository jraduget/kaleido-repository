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

import static org.kaleidofoundry.core.config.ConfigurationConstants.ConfigurationPluginName;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.Name;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.ProviderService;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.store.FileStoreProvider;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Configuration provider instance (creation + registry) <br/>
 * <ul>
 * <li>It can be manually used by developer via {@link ConfigurationFactory}</li>
 * <li>or</li>
 * <li>It will be used automatically by annotation processor (AOP AspectJ, Guice, Spring) which will handle {@link Context} annotation at
 * runtime</li>
 * </ul>
 * 
 * @author jraduget
 */
public class ConfigurationProvider extends AbstractProviderService<Configuration> implements ProviderService<Configuration> {


   /**
    * @param genericClass
    */
   public ConfigurationProvider(final Class<Configuration> genericClass) {
	super(genericClass);
   }

   /**
    * @return main configuration registry instance (shared between providers instances)
    */
   @Override
   public final ConfigurationRegistry getRegistry() {
	return ConfigurationFactory.REGISTRY;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public Configuration _provides(@NotNull final RuntimeContext<Configuration> runtimeContext) throws ProviderException {
	String name = runtimeContext.getString(Name);
	final String resourceUri = runtimeContext.getString(FileStoreUri);

	if (StringHelper.isEmpty(name)) {
	   name = runtimeContext.getName();
	}

	if (StringHelper.isEmpty(name)) { throw new EmptyContextParameterException(Name, runtimeContext); }
	if (StringHelper.isEmpty(resourceUri)) { throw new EmptyContextParameterException(FileStoreUri, runtimeContext); }

	return provides(name, resourceUri != null ? URI.create(FileStoreProvider.buildFullResourceURi(resourceUri)) : null, runtimeContext);
   }

   /**
    * @see ConfigurationFactory#provides(String, String)
    * @param name
    * @param resourceURI
    * @return configuration instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public Configuration provides(@NotNull final String name, @NotNull final String resourceURI) throws ProviderException {
	return provides(name, resourceURI, new RuntimeContext<Configuration>(name, Configuration.class));
   }

   /**
    * @see ConfigurationFactory#provides(String, String, RuntimeContext)
    * @param name
    * @param resourceURI
    * @param runtimeContext
    * @return configuration instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public Configuration provides(@NotNull final String name, @NotNull final String resourceURI, @NotNull final RuntimeContext<Configuration> runtimeContext)
	   throws ProviderException {
	String fullResourceUri = FileStoreProvider.buildFullResourceURi(resourceURI);
	return provides(name, resourceURI != null ? URI.create(fullResourceUri) : null, runtimeContext);
   }

   /**
    * @see ConfigurationFactory#provides(String, URI, RuntimeContext)
    * @param name
    * @param resourceURI
    * @param runtimeContext
    * @return configuration instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   public Configuration provides(@NotNull final String name, @NotNull final URI resourceURI, @NotNull final RuntimeContext<Configuration> runtimeContext)
	   throws ProviderException {

	final Configuration configuration = getRegistry().get(name);

	try {
	   if (configuration == null) {
		Configuration newInstance;
		// create it
		newInstance = create(name, resourceURI, runtimeContext);
		// load it
		newInstance.load();
		// register it
		getRegistry().put(name, newInstance);

		return newInstance;
	   } else {
		if (!configuration.isLoaded()) {
		   configuration.load();
		}
		// re-check uri coherence ?
		return configuration;
	   }
	} catch (final ResourceException ste) {
	   throw new ProviderException(ste);
	}
   }

   /**
    * @param name configuration name (unique identifier)
    * @param resourceURI configuration resource URI
    * @param runtimeContext see {@link ConfigurationContextBuilder} informations for common context properties, and specific implementation
    *           class
    *           if needed
    * @return new configuration instance which map to the resourceURI
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    * @see Configuration
    */
   private Configuration create(@NotNull final String name, @NotNull final URI resourceURI, @NotNull final RuntimeContext<Configuration> runtimeContext)
	   throws ProviderException {

	final Set<Plugin<Configuration>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(Configuration.class);

	// scan each @Declare store implementation, to get one which handle the uri scheme
	for (final Plugin<Configuration> pi : pluginImpls) {
	   final Class<? extends Configuration> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		final String uriPath = resourceURI.getPath().toLowerCase();
		final String pluginConfigExtention = declarePlugin.value().replace(ConfigurationPluginName, "").toLowerCase();

		if (uriPath.endsWith(pluginConfigExtention)) {
		   final Constructor<? extends Configuration> constructor = impl.getConstructor(String.class, String.class, RuntimeContext.class);
		   return constructor.newInstance(name, resourceURI.toString(), runtimeContext);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(),
			"String name, String resourceUri, RuntimeContext<Configuration> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(),
			"String name, String resourceUri, RuntimeContext<Configuration> context");
	   } catch (final InvocationTargetException e) {
		if (e.getCause() instanceof ResourceException) {
		   throw new ProviderException(e.getCause());
		} else {
		   throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(),
			   "String name, String resourceUri, RuntimeContext<Configuration> context");
		}
	   }
	}

	throw new ProviderException(new ResourceException("store.uri.notmanaged.illegal", resourceURI.toString()));
   }

}
