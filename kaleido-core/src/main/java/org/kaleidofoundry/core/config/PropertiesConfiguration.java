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
import java.net.URI;
import java.util.Properties;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;

/**
 * Properties {@link Configuration} implementation
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.PropertiesConfigurationPluginName)
public class PropertiesConfiguration extends AbstractConfiguration {

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public PropertiesConfiguration(final String name, final URI resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public PropertiesConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * java.util.Properties)
    */
   @Override
   protected Cache<String, String> loadProperties(final ResourceHandler resourceHandler, final Cache<String, String> properties) throws ResourceException,
	   ConfigurationException {
	try {
	   Properties lprops = new Properties();
	   lprops.load(resourceHandler.getInputStream());

	   for (String propName : lprops.stringPropertyNames()) {
		properties.put(propName, lprops.getProperty(propName));
	   }

	   return properties;
	} catch (IOException ioe) {
	   throw new ResourceException(ioe);
	}
   }

   @Override
   @NotYetImplemented
   protected Cache<String, String> storeProperties(final Cache<String, String> cacheProperties, final SingleResourceStore resourceStore)
	   throws ResourceException, ConfigurationException {
	// try {
	// properties.save(resourceHandler.getInputStream());
	// return properties;
	// } catch (IOException ioe) {
	// throw new StoreException(ioe);
	// }

	return null; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }
}
