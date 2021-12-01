/*  
 * Copyright 2008-2021 the original author or authors 
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

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.system.OsEnvironment;

/**
 * Operating system environment variable configuration implementation (read only)<br/>
 * It will reference all operating system environment variable defined via set OS command
 * 
 * @author jraduget
 */
@Declare(ConfigurationConstants.OsEnvConfigurationPluginName)
public class OsEnvConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param context
    * @throws ResourceException
    */
   public OsEnvConfiguration(final RuntimeContext<Configuration> context) throws ResourceException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri ignored
    * @param context
    * @throws ResourceException
    */
   public OsEnvConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, "memory:/internal/" + name + ".osenv", context);
   }

   /**
    * @param name
    * @param context
    * @throws ResourceException
    */
   public OsEnvConfiguration(final String name, final RuntimeContext<Configuration> context) throws ResourceException {
	this(name, (String) null, context);
   }

   /**
    * @see AbstractConfiguration#AbstractConfiguration()
    */
   OsEnvConfiguration() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> loadProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException {
	try {
	   final OsEnvironment environment = new OsEnvironment();
	   for (final String key : environment.stringPropertyNames()) {
		final String value = environment.getProperty(key);
		properties.put(normalizeKey(key), value != null ? value : "");
	   }
	   return properties;
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceHandler.getUri());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> storeProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> cacheProperties)
	   throws ResourceException, ConfigurationException {
	return cacheProperties; // never called
   }

   @Override
   public boolean isStorable() {
	return false;
   }

}
