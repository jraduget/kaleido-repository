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
package org.kaleidofoundry.core.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationAdapter;
import org.kaleidofoundry.core.config.ConfigurationChangeEvent;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.ConfigurationListener;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.lang.annotation.Tasks;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginHelper;

/**
 * Base implementation for {@link ProviderService} <br/>
 * The dynamic runtime contexts are registered here, in order to trigger configuration changes
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@ThreadSafe
@Tasks(tasks = { @Task(labels = TaskLabel.Enhancement, comment = "use singletons annotation information and move default registry instance here ?"),
	@Task(labels = TaskLabel.Enhancement, comment = "create default method introspection which provide instance") })
public abstract class AbstractProviderService<T> implements ProviderService<T> {

   protected final Class<T> genericClassInterface;

   /** runtime context instances */
   protected final List<RuntimeContext<T>> dynamicsRegisterContext;

   /** created configurations listeners instances by configuration */
   protected final Map<String, ConfigurationListener> configurationsListeners;

   /**
    * @param genericClassInterface
    */
   public AbstractProviderService(final Class<T> genericClassInterface) {
	this.genericClassInterface = genericClassInterface;
	this.dynamicsRegisterContext = Collections.synchronizedList(new ArrayList<RuntimeContext<T>>());
	this.configurationsListeners = new ConcurrentHashMap<String, ConfigurationListener>();
	registerConfigurationsListeners();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#provides(org.kaleidofoundry.core.context.Context, java.lang.String, java.lang.Class)
    */
   @Override
   public final T provides(final Context context, final String defaultName, final Class<T> genericClassInterface) throws ProviderException {
	return provides(RuntimeContext.createFrom(context, defaultName, genericClassInterface));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.ProviderService#provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public final T provides(final RuntimeContext<T> context) throws ProviderException {
	registerDynamicContext(context);
	return _provides(context);
   }

   /**
    * @param context
    * @return new T instance, build from context informations
    * @throws ProviderException
    */
   protected abstract T _provides(RuntimeContext<T> context) throws ProviderException;

   /**
    * register for listening configuration changes (multiples), for all registered configuration
    */
   synchronized void registerConfigurationsListeners() {

	// for each declared configuration
	for (final Configuration configuration : ConfigurationFactory.getRegistry().values()) {

	   if (configuration.isUpdateAllowed()) {

		// clear existing listeners
		cleanupConfigurationsListeners(configuration);

		// create configuration change listener that trigger changes to registered runtime context
		final ConfigurationListener configurationListener = new ConfigurationAdapter() {
		   @Override
		   public void propertiesChanges(final LinkedHashSet<ConfigurationChangeEvent> events) {

			boolean fireChanges = false;
			final Plugin<?> currentPlugin = PluginHelper.getInterfacePlugin(genericClassInterface);

			// if one event is bound to current plugin (property name start by the plugin code) -> fire changes to runtime contexts
			for (final ConfigurationChangeEvent evt : events) {
			   if (evt != null && evt.getPropertyName().startsWith(currentPlugin.getName())) {
				fireChanges = true;
				break;
			   }
			}

			if (fireChanges) {
			   for (final RuntimeContext<T> rc : dynamicsRegisterContext) {
				if (rc.isDynamics()) {
				   rc.triggerConfigurationChangeEvents(events);
				}
			   }
			}
		   }
		};

		// add and register the new configuration listener
		configuration.addConfigurationListener(configurationListener);
		configurationsListeners.put(configuration.getName(), configurationListener);
	   }
	}

   }

   /**
    * cleanup configuration listeners that have been created by current provider instance
    * 
    * @param configuration
    */
   synchronized void cleanupConfigurationsListeners(final Configuration configuration) {
	if (configuration != null) {
	   final ConfigurationListener listener = configurationsListeners.get(configuration.getName());
	   if (listener != null) {
		configuration.removeConfigurationListener(listener);
	   }
	}
   }

   /**
    * registered given runtime context (if it is dynamics)
    * 
    * @param context
    */
   protected void registerDynamicContext(final RuntimeContext<T> context) {
	if (context.isDynamics()) {
	   dynamicsRegisterContext.add(context);
	}
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#finalize()
    */
   @Override
   protected void finalize() throws Throwable {
	dynamicsRegisterContext.clear();
	super.finalize();
   }

}