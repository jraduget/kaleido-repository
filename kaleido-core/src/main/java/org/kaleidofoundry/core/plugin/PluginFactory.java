package org.kaleidofoundry.core.plugin;

import java.util.Set;

import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This factory will load and handle a global registry of :<br/>
 * <ul>
 * <li>plugin declare interface {@link DeclarePlugin} - {@link PluginRegistry}</li>
 * <li>plugin declare class implementation {@link DeclarePlugin} - {@link PluginImplementationRegistry}</li>
 * </ul>
 * <p>
 * 1. The registries are loaded statically, during first factory call<br/>
 * 2. During this load, it scan and introspect all annotation plugin interface /implementation {@link DeclarePlugin}<br/>
 * 3. It can be reloaded (synchronized way) at each time.
 * </p>
 * 
 * @author Jerome RADUGET
 */
public final class PluginFactory {

   private static final PluginRegistry INTERFACE_REGISTRY = new PluginRegistry();

   private static final PluginImplementationRegistry IMPLEMENTATION_REGISTRY = new PluginImplementationRegistry();

   private static final Logger LOGGER = LoggerFactory.getLogger(PluginFactory.class);

   static {
	loadPluginInspector();
   }

   /*
    * init static factory plugin load
    */
   private static void loadPluginInspector() throws PluginRegistryException {

	INTERFACE_REGISTRY.clear();
	IMPLEMENTATION_REGISTRY.clear();

	final PluginInspector pluginInspector = new PluginInspector();

	Set<Plugin<?>> plugins = null;
	Set<Plugin<?>> pluginImpls = null;
	Set<PluginRegistryException> errors = null;

	// inspect @Declare
	plugins = pluginInspector.loadPluginMetaData();
	pluginImpls = pluginInspector.loadPluginImplementationMetaData();

	// print load processing messages
	for (final String message : pluginInspector.getEchoMessages()) {
	   LOGGER.info(message);
	}

	// check declaration coherence
	errors = pluginInspector.checkDeclarations();

	// all right, registered them
	if (errors.isEmpty()) {
	   for (final Plugin<?> p : plugins) {
		INTERFACE_REGISTRY.put(p.getName(), p);
	   }
	   for (final Plugin<?> p : pluginImpls) {
		IMPLEMENTATION_REGISTRY.put(p.getName(), p);
	   }
	} else {
	   // process multiple errors
	   final StringBuilder errorMessage = new StringBuilder();
	   for (final PluginRegistryException pre : errors) {
		LOGGER.error(pre.getMessage());
		errorMessage.append("\t").append(pre.getMessage()).append("\n");
	   }
	   throw new PluginRegistryException("plugin.error.load.declare.all", new String[] { errorMessage.toString() });
	}
   }

   /**
    * @return plugin interface registry
    */
   public final static PluginRegistry getInterfaceRegistry() {
	return INTERFACE_REGISTRY;
   }

   /**
    * @return plugin implementations registry
    */
   public final static PluginImplementationRegistry getImplementationRegistry() {
	return IMPLEMENTATION_REGISTRY;
   }

   /**
    * @throws PluginRegistryException
    */
   public final static synchronized void reload() throws PluginRegistryException {
	loadPluginInspector();
   }
}
