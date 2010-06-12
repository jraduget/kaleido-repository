package org.kaleidofoundry.core.config;

import static org.kaleidofoundry.core.config.ConfigurationConstants.ConfigurationPluginName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

import org.kaleidofoundry.core.config.ConfigurationConstants.Extension;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Configuration factory provider
 * 
 * @author Jerome RADUGET
 */
public abstract class ConfigurationFactory {

   /**
    * configuration registry instance
    */
   private static final ConfigurationRegistry REGISTRY = new ConfigurationRegistry();

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
	   return newInstance;
	} else {
	   if (!configuration.isLoaded()) {
		configuration.load();
	   }
	   // recheck uri coherence ?
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

	Set<Plugin<Configuration>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(Configuration.class);

	// scan each @DeclarePlugin resource store implementation, to get one which handle the uri scheme
	for (Plugin<Configuration> pi : pluginImpls) {
	   Class<? extends Configuration> impl = pi.getAnnotatedClass();
	   try {

		DeclarePlugin declarePlugin = impl.getAnnotation(DeclarePlugin.class);

		String uriPath = resourceURI.getPath().toLowerCase();
		String pluginConfigExtention = declarePlugin.value().replace(ConfigurationPluginName, "").toLowerCase();

		if (uriPath.endsWith(pluginConfigExtention)) {
		   Constructor<? extends Configuration> constructor = impl.getConstructor(String.class, URI.class, RuntimeContext.class);
		   return constructor.newInstance(identifier, resourceURI, runtimeContext);
		}

	   } catch (NoSuchMethodException e) {
		throw new StoreException("store.resource.factory.create.NoSuchMethodException", impl.getName());
	   } catch (InstantiationException e) {
		throw new StoreException("store.resource.factory.create.InstantiationException", impl.getName(), e.getMessage());
	   } catch (IllegalAccessException e) {
		throw new StoreException("store.resource.factory.create.IllegalAccessException=ResourceStore", impl.getName());
	   } catch (InvocationTargetException e) {
		throw new StoreException("store.resource.factory.create.InvocationTargetException", impl.getName(), e.getMessage());
	   }
	}

	throw new StoreException("store.resource.uri.custom.notmanaged", resourceURI.getScheme());
   }

}
