/*
 * Copyright 2008-2014 the original author or authors
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Json configuration implementation
 * 
 * @author jraduget
 */
@Declare(ConfigurationConstants.JsonConfigurationPluginName)
public class JsonConfiguration extends AbstractConfiguration {

   private static final JsonFactory JSON_FACTORY = new JsonFactory();

   JsonConfiguration() {
	super();
   }

   /**
    * @param context
    * @throws ResourceException
    */
   public JsonConfiguration(final RuntimeContext<Configuration> context) throws ResourceException {
	super(context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public JsonConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   @Override
   protected Cache<String, Serializable> loadProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException {

	ObjectMapper mapper = new ObjectMapper();
	try {
	   JsonNode rootNode = mapper.readValue(resourceHandler.getReader(), JsonNode.class);
	   if (rootNode != null) {
		feedProperties(rootNode.getFields(), new StringBuilder(), properties);
	   }
	   return properties;

	} catch (JsonParseException jpe) {
	   throw new ConfigurationException("config.load.json.dom.error", jpe);
	} catch (JsonMappingException jme) {
	   throw new ConfigurationException("config.load.json.parsing.error", jme, singleFileStore.getResourceBinding().toString());
	} catch (IOException ioe) {
	   throw new ResourceException(ioe, resourceHandler.getUri());
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.store.ResourceHandler,
    * org.kaleidofoundry.core.cache.Cache)
    */
   @Override
   protected Cache<String, Serializable> storeProperties(final ResourceHandler resourceHandler, final Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException {

	ObjectMapper mapper = new ObjectMapper();
	ObjectNode rootNode = mapper.createObjectNode();

	Map<String, ObjectNode> currentNodes = new HashMap<String, ObjectNode>();

	for (String key : keySet()) {
	   StringTokenizer strToken = new StringTokenizer(key.substring(KeyRoot.length()), KeySeparator);
	   String subKey = "";

	   ObjectNode node = rootNode;

	   while (strToken.hasMoreTokens()) {
		String nodeName = strToken.nextToken();
		subKey = subKey + KeySeparator + nodeName;

		ObjectNode newNode;
		if (!currentNodes.containsKey(subKey)) {
		   newNode = mapper.createObjectNode();
		   currentNodes.put(subKey, newNode);
		} else {
		   newNode = currentNodes.get(subKey);
		}

		if (strToken.hasMoreElements()) {
		   node.put(nodeName, newNode);
		   node = newNode;
		} else {
		   List<String> values = getStringList(key);
		   if (values == null || values.size() == 1) {
			node.put(nodeName, getString(key));
		   } else {
			ArrayNode arrayNode = mapper.createArrayNode();
			for (String value : values) {
			   arrayNode.add(value);
			}
			node.put(nodeName, arrayNode);
		   }
		   node = newNode;
		}
	   }
	}

	ByteArrayOutputStream jsonOutput = null;
	JsonGenerator jsonGenerator = null;

	try {
	   // create output stream and json serializer
	   jsonOutput = new ByteArrayOutputStream();
	   jsonGenerator = JSON_FACTORY.createJsonGenerator(jsonOutput);
	   jsonGenerator.useDefaultPrettyPrinter();
	   // jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

	   // write json content
	   mapper.writeTree(jsonGenerator, rootNode);
	   // flush output stream writes
	   jsonGenerator.close();
	   // Store the document content
	   singleFileStore.store(singleFileStore.createResourceHandler(resourceHandler.getUri(), new ByteArrayInputStream(jsonOutput.toByteArray())));

	   return properties;

	} catch (IOException ioe) {
	   if (ioe instanceof ResourceException) {
		throw (ResourceException) ioe;
	   } else {
		throw new ResourceException(ioe, resourceHandler.getUri());
	   }
	} finally {
	   if (jsonGenerator != null && !jsonGenerator.isClosed()) {
		try {
		   jsonGenerator.close();
		} catch (IOException ioe) {
		   throw new ResourceException(ioe, resourceHandler.getUri());
		}
	   }
	}

   }

   /**
    * recursive method, use to
    * 
    * @param nodeList
    * @param keyName
    * @param properties
    */
   protected void feedProperties(final Iterator<Entry<String, JsonNode>> nodesByName, final StringBuilder keyName, final Cache<String, Serializable> properties) {

	while (nodesByName.hasNext()) {
	   Entry<String, JsonNode> node = nodesByName.next();
	   final StringBuilder newKeyName = new StringBuilder(keyName).append(keyName.length() > 0 ? KeySeparator : KeyRoot).append(node.getKey());
	   if (node.getValue().isContainerNode()) {
		if (node.getValue().isArray()) {

		   List<String> result = new LinkedList<String>();
		   Iterator<JsonNode> arrayValues = node.getValue().iterator();
		   while (arrayValues.hasNext()) {
			result.add(arrayValues.next().asText());
		   }

		   properties.put(newKeyName.toString(), StringHelper.unsplit(MultiValuesSeparator, result.toArray(new Object[result.size()])));

		} else {
		   feedProperties(node.getValue().getFields(), newKeyName, properties);
		}
	   } else {
		properties.put(newKeyName.toString(), node.getValue().getTextValue());
	   }
	}
   }

}
