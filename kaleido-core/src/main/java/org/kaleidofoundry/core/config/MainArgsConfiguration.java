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
import java.util.Map;
import java.util.Map.Entry;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;
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
    * enumeration of local context property name
    */
   public static enum ContextProperty {
	/** string representation of the main arguments array */
	argsMainString,
	/** arguments separator character */
	argsSeparator;
   }

   /**
    * @param identifier
    * @param resourceUri ignored
    * @param context
    * @throws StoreException
    */
   public MainArgsConfiguration(final String identifier, final URI resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, URI.create("memory:/internal/" + identifier + ".mainargs"), context);
   }

   /**
    * @param identifier
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public MainArgsConfiguration(final String identifier, final String resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, "memory:/internal/" + identifier + ".mainargs", context);
   }

   /**
    * @param identifier
    * @param runtimeContext
    * @throws StoreException
    */
   public MainArgsConfiguration(final String identifier, final RuntimeContext<Configuration> runtimeContext) throws StoreException {
	this(identifier, (String) null, runtimeContext);
   }

   @Override
   protected Cache<String, String> loadProperties(final ResourceHandler resourceHandler, final Cache<String, String> cacheProperties) throws StoreException,
	   ConfigurationException {

	String mainArgs = context.getProperty(ContextProperty.argsMainString.name());
	String argsSeparator = context.getProperty(ContextProperty.argsSeparator.name());

	final String[] args = ConverterHelper.stringToArray(mainArgs, argsSeparator != null ? argsSeparator : " ");
	Map<String, String> argsMap = ConverterHelper.argsToMap(args);

	for (Entry<String, String> entry : argsMap.entrySet()) {
	   String rawArgValue = entry.getValue();
	   if (rawArgValue != null && rawArgValue.contains("|")) {
		cacheProperties.put(entry.getKey(), StringHelper.replaceAll(rawArgValue, "|", " "));
	   } else {
		cacheProperties.put(entry.getKey(), StringHelper.replaceAll(rawArgValue != null ? rawArgValue : "", "&nbsp;", " "));
	   }
	}

	return cacheProperties;
   }

   @Override
   protected Cache<String, String> storeProperties(final Cache<String, String> cacheProperties, final SingleResourceStore resourceStore) throws StoreException,
	   ConfigurationException {
	return cacheProperties; // unused code because read only is set to true
   }

   @Override
   public boolean isReadOnly() {
	return true;
   }

}
