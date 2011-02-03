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

import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.ArgsMainString;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.ArgsSeparator;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.FileHandler;
import org.kaleidofoundry.core.store.SingleFileStore;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.ConverterHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * If you want to use a space in a property value, use "& nbsp;" instead<br/>
 * for the separator value use the character '|'
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.MainArgsConfigurationPluginName)
public class MainArgsConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param context
    * @throws StoreException
    */
   public MainArgsConfiguration(final RuntimeContext<Configuration> context) throws StoreException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public MainArgsConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(name, "memory:/internal/" + name + ".mainargs", context);
   }

   /**
    * @param name
    * @param runtimeContext
    * @throws StoreException
    */
   public MainArgsConfiguration(final String name, final RuntimeContext<Configuration> runtimeContext) throws StoreException {
	this(name, (String) null, runtimeContext);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> loadProperties(final FileHandler resourceHandler, final Cache<String, Serializable> cacheProperties)
	   throws StoreException, ConfigurationException {

	final String mainArgs = context.getString(ArgsMainString);
	final String argsSeparator = context.getString(ArgsSeparator);

	final String[] args = ConverterHelper.stringToArray(mainArgs, argsSeparator != null ? argsSeparator : " ");
	final Map<String, String> argsMap = ConverterHelper.argsToMap(args);

	if (argsMap != null) {
	   for (final Entry<String, String> entry : argsMap.entrySet()) {
		final String rawArgValue = entry.getValue();
		if (rawArgValue != null && rawArgValue.contains("|")) {
		   cacheProperties.put(normalizeKey(entry.getKey()), StringHelper.replaceAll(rawArgValue, "|", " "));
		} else {
		   cacheProperties.put(normalizeKey(entry.getKey()), StringHelper.replaceAll(rawArgValue != null ? rawArgValue : "", "&nbsp;", " "));
		}
	   }
	}

	return cacheProperties;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.cache.Cache,
    * org.kaleidofoundry.core.store.SingleFileStore)
    */
   @Override
   protected Cache<String, Serializable> storeProperties(final Cache<String, Serializable> properties, final SingleFileStore fileStore) throws StoreException,
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
