package org.kaleidofoundry.core.config;

import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.PluginImplementationRegistry;
import org.kaleidofoundry.core.util.Registry;

/**
 * Registery for all declared {@link Configuration}
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationRegistry extends Registry<Configuration> {

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

	PluginImplementationRegistry pluginRegistry = PluginFactory.getImplementationRegistry();
	Set<Plugin<Configuration>> configurationImpls = pluginRegistry.findByInterface(Configuration.class);
	String resourceExtention = configurationResource.getRawPath().substring(configurationResource.getRawPath().lastIndexOf("."));

	for (Plugin<Configuration> pi : configurationImpls) {
	   if (pi.getName().endsWith(resourceExtention)) { return pi.getAnnotatedClass(); }
	}

	return null;

   }
}
