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

import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.PluginImplementationRegistry;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.Registry;

/**
 * Registry for all declared {@link Configuration}
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationRegistry extends Registry<String, Configuration> {

   private static final long serialVersionUID = -6914735437869325831L;

   /**
    * Find a configuration class implementation by its resource extension type
    * 
    * @param configurationResource
    * @return configuration implementation class which handle the given resource, null if configurationResource does not manage by any
    *         configuration registered
    */
   @Nullable
   public Class<? extends Configuration> findByResourceExtension(@NotNull final URI configurationResource) {

	final PluginImplementationRegistry pluginRegistry = PluginFactory.getImplementationRegistry();
	final Set<Plugin<Configuration>> configurationImpls = pluginRegistry.findByInterface(Configuration.class);
	final String resourceExtention = configurationResource.getRawPath().substring(configurationResource.getRawPath().lastIndexOf("."));

	for (final Plugin<Configuration> pi : configurationImpls) {
	   if (pi.getName().endsWith(resourceExtention)) { return pi.getAnnotatedClass(); }
	}

	return null;

   }
}
