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

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.FileHandler;
import org.kaleidofoundry.core.store.SingleFileStore;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Properties {@link Configuration} implementation
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.PropertiesConfigurationPluginName)
public class PropertiesConfiguration extends AbstractConfiguration {

   /**
    * @param context
    * @throws StoreException
    */
   public PropertiesConfiguration(final RuntimeContext<Configuration> context) throws StoreException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public PropertiesConfiguration(@NotNull final String name, @NotNull final String resourceUri, @NotNull final RuntimeContext<Configuration> context)
	   throws StoreException {
	super(name, resourceUri, context);
   }

   /**
    * @see AbstractConfiguration#AbstractConfiguration()
    */
   PropertiesConfiguration() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> loadProperties(final FileHandler resourceHandler, final Cache<String, Serializable> properties) throws StoreException,
	   ConfigurationException {
	try {
	   final Properties lprops = new Properties();
	   lprops.load(resourceHandler.getInputStream());

	   for (final String propName : lprops.stringPropertyNames()) {
		properties.put(normalizeKey(propName), lprops.getProperty(propName));
	   }

	   return properties;
	} catch (final IOException ioe) {
	   throw new StoreException(ioe, resourceHandler.getResourceUri());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.cache.Cache,
    * org.kaleidofoundry.core.store.SingleFileStore)
    */
   @Override
   @NotYetImplemented
   protected Cache<String, Serializable> storeProperties(final Cache<String, Serializable> cacheProperties, final SingleFileStore fileStore)
	   throws StoreException, ConfigurationException {
	// try {
	// properties.save(resourceHandler.getInputStream());
	// return properties;
	// } catch (IOException ioe) {
	// throw new StoreException(ioe);
	// }

	return null; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }
}
