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
import java.util.Properties;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;

/**
 * Java environment variable configuration implementation (read only)<br/>
 * <br/>
 * It will reference all java user variable defined like :
 * 
 * <pre>
 * -Dlog4j.configuration=file:/... -Dapp.config.file=...
 * </pre>
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.JavaSystemConfigurationPluginName)
public class JavaSystemConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param name
    * @param resourceUri ignored
    * @param context
    * @throws ResourceException
    */
   public JavaSystemConfiguration(final String name, final URI resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, URI.create("memory:/internal/" + name + ".javasystem"), context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public JavaSystemConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, "memory:/internal/" + name + ".javasystem", context);
   }

   /**
    * @param name
    * @param context
    * @throws ResourceException
    */
   public JavaSystemConfiguration(final String name, final RuntimeContext<Configuration> context) throws ResourceException {
	this(name, (String) null, context);
   }

   @Override
   protected Cache<String, String> loadProperties(final ResourceHandler resourceHandler, final Cache<String, String> cacheProperties) throws ResourceException,
	   ConfigurationException {

	Properties javaEnvVariables = System.getProperties();

	for (String key : javaEnvVariables.stringPropertyNames()) {
	   cacheProperties.put(key, javaEnvVariables.getProperty(key));
	}

	return cacheProperties;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#setProperty(java.lang.String, java.lang.Object)
    */
   @Override
   public void setProperty(final String key, final Object value) {
	super.setProperty(key, value);
	System.getProperties().put(key, value);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.cache.Cache,
    * org.kaleidofoundry.core.store.SingleResourceStore)
    */
   @Override
   protected Cache<String, String> storeProperties(final Cache<String, String> properties, final SingleResourceStore resourceStore) throws ResourceException,
	   ConfigurationException {
	return properties; // never called
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#isStorageAllowed()
    */
   @Override
   public boolean isStorageAllowed() {
	return false;
   }

}