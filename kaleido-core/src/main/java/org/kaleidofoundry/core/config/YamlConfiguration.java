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

import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyRoot;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeySeparator;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Yaml configuration implementation
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.YamlConfigurationPluginName)
public class YamlConfiguration extends AbstractConfiguration {

   public YamlConfiguration(final RuntimeContext<Configuration> context) throws ResourceException {
	super(context);
   }

   public YamlConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#loadProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> loadProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException {

	// log timezone information for date value
	LOGGER.debug("java default timezone is {}", TimeZone.getDefault());

	// loader options
	Representer option = new Representer();
	// this avoid a time offset, due to the default system timezone (api it seems to not work)
	// option.setTimeZone(TimeZone.getTimeZone("GMT"));
	Yaml yaml = new Yaml(option);

	// load all yaml documents ( --- ...)
	Iterable<Object> objectList = yaml.loadAll(resourceHandler.getReader());
	// for each document of the resource, load it
	for (Object obj : objectList) {
	   feedProperties(obj, new StringBuilder(), properties);
	}
	return properties;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> storeProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> cacheProperties)
	   throws ResourceException, ConfigurationException {

	DumperOptions options = new DumperOptions();
	options.setPrettyFlow(true);
	options.setDefaultFlowStyle(FlowStyle.BLOCK);
	// this avoid a time offset, due to the default system timezone (api it seems to not work)
	// options.setTimeZone(TimeZone.getTimeZone("GMT"));

	final Yaml yaml = new Yaml(options);
	final Map<String, Object> rootYamlMap = new LinkedHashMap<String, Object>();
	final Map<String, Map<String, Object>> registeredMap = new HashMap<String, Map<String, Object>>();

	for (String key : keySet()) {
	   StringTokenizer strToken = new StringTokenizer(key.substring(KeyRoot.length()), KeySeparator);
	   String subKey = "";

	   Map<String, Object> currentYamlMap = rootYamlMap;

	   while (strToken.hasMoreTokens()) {
		String nodeName = strToken.nextToken();
		subKey = subKey + KeySeparator + nodeName;

		Map<String, Object> newYamlMap;
		if (!registeredMap.containsKey(subKey)) {
		   newYamlMap = new LinkedHashMap<String, Object>();
		   registeredMap.put(subKey, newYamlMap);
		} else {
		   newYamlMap = registeredMap.get(subKey);
		}

		if (strToken.hasMoreElements()) {
		   currentYamlMap.put(nodeName, newYamlMap);
		   currentYamlMap = newYamlMap;
		} else {
		   currentYamlMap.put(nodeName, getProperty(key));

		   /*
		    * List<String> values = getStringList(key);
		    * if (values == null || values.size() == 1) {
		    * currentYamlMap.put(nodeName, getProperty(key));
		    * } else {
		    * currentYamlMap.put(nodeName, values);
		    * }
		    */
		   currentYamlMap = newYamlMap;
		}
	   }
	}

	Writer yamlWriter = null;

	try {
	   // create output writer
	   yamlWriter = new StringWriter();

	   // write yaml content
	   yaml.dump(rootYamlMap, yamlWriter);

	   // Store the document content
	   singleFileStore.store(singleFileStore.createResourceHandler(resourceHandler.getResourceUri(), yamlWriter.toString()));

	   return cacheProperties;

	} catch (IOException ioe) {
	   if (ioe instanceof ResourceException) {
		throw (ResourceException) ioe;
	   } else {
		throw new ResourceException(ioe, resourceHandler.getResourceUri());
	   }
	} finally {
	   if (yamlWriter != null) {
		try {
		   yamlWriter.close();
		} catch (IOException ioe) {
		   throw new ResourceException(ioe, resourceHandler.getResourceUri());
		}
	   }
	}
   }

   /**
    * recursive method, use to
    * 
    * @param nodeList
    * @param keyName
    * @param cacheProperties
    */
   @SuppressWarnings("unchecked")
   protected <T extends Serializable> void feedProperties(final Object values, final StringBuilder keyName, final Cache<String, Serializable> cacheProperties) {
	if (values instanceof Map) {
	   Map<String, ?> valuesByName = Map.class.cast(values);
	   for (Entry<String, ?> node : valuesByName.entrySet()) {
		final StringBuilder newKeyName = new StringBuilder(keyName).append(keyName.length() > 0 ? KeySeparator : KeyRoot).append(node.getKey());
		feedProperties(node.getValue(), newKeyName, cacheProperties);
	   }
	} else if (values instanceof Collection) {

	   cacheProperties.put(keyName.toString(), (Serializable) values);
	   /*
	    * Collection<?> result = Collection.class.cast(values);
	    * List<String> serializedValues = new LinkedList<String>();
	    * int cpt = 0;
	    * for (Object o : result) {
	    * T value = (T) o;
	    * Class<T> c = (Class<T>) o.getClass();
	    * LOGGER.debug("{}[{}] : {value='{}', class='{}' serialization='{}'}", new String[] { keyName.toString(), String.valueOf(cpt),
	    * String.valueOf(value),
	    * value.getClass().getName(), serialize(value, c) });
	    * serializedValues.add(serialize(value, c));
	    * cpt++;
	    * }
	    * // properties.put(keyName.toString(), StringHelper.unsplit(MultiValuesSeparator, serializedValues.toArray(new
	    * // Object[serializedValues.size()])));
	    * cacheProperties.put(keyName.toString(), (Serializable) serializedValues);
	    */
	} else {
	   if (values != null) {
		T value = (T) values;
		Class<T> c = (Class<T>) values.getClass();

		if (LOGGER.isDebugEnabled()) {
		   LOGGER.debug("{} : {value='{}', class='{}' serialization='{}'}", new String[] { keyName.toString(), String.valueOf(value),
			   value.getClass().getName(), serialize(value, c) });
		}

		// properties.put(keyName.toString(), serialize(value, c));
		cacheProperties.put(keyName.toString(), value);
	   } else {
		cacheProperties.put(keyName.toString(), "");
	   }
	}

   }

}
