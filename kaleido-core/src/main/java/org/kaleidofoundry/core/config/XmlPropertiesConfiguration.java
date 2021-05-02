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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Properties;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.util.locale.LocaleFactory;

/**
 * Xml properties implementation
 * 
 * @author jraduget
 */
@Declare(ConfigurationConstants.XmlPropertiesConfigurationPluginName)
public class XmlPropertiesConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param context
    * @throws ResourceException
    */
   public XmlPropertiesConfiguration(final RuntimeContext<Configuration> context) throws ResourceException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public XmlPropertiesConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   /**
    * @see AbstractConfiguration#AbstractConfiguration()
    */
   XmlPropertiesConfiguration() {
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

	   final Properties lprops = new Properties();
	   lprops.loadFromXML(resourceHandler.getInputStream());

	   for (final String propName : lprops.stringPropertyNames()) {
		properties.put(normalizeKey(propName), lprops.getProperty(propName));
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
	try {
	   ByteArrayOutputStream out = new ByteArrayOutputStream();
	   toProperties().storeToXML(out, Calendar.getInstance(LocaleFactory.getDefaultFactory().getCurrentLocale()).getTime().toString());
	   singleFileStore.store(singleFileStore.createResourceHandler(resourceHandler.getUri(), new ByteArrayInputStream(out.toByteArray())));
	   return cacheProperties;
	} catch (ResourceException re) {
	   throw re;
	} catch (IOException ioe) {
	   throw new ResourceException(ioe, resourceHandler.getUri());
	}
   }

}
