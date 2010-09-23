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

import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyPropertiesSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.MultiValDefaultSeparator;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.util.StringHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Xml configuration implementation :<br/>
 * 
 * <pre>
 * &lt;root&gt;
 * 	&lt;application&gt;
 * 			&lt;name&gt;app&lt;/name&gt;
 * 			&lt;version&gt;1.0.0&lt;/version&gt;
 * 			&lt;description&gt;description of the application...&lt;/description&gt;
 * 			&lt;date&gt;2006-09-01&lt;/date&gt;	
 * 			&lt;librairies&gt;
 * 				&lt;value&gt;dom4j.jar&lt;/value&gt;
 * 				&lt;value&gt;log4j.jar&lt;/value&gt;
 * 				&lt;value&gt;mail.jar&lt;/value&gt;
 * 			&lt;/librairies&gt;
 * 			&lt;modules&gt;
 * 				&lt;sales&gt;Sales&lt;version&gt;1.1.0&lt;/version&gt;&lt;/sales&gt;
 * 				&lt;marketing&gt;Market.&lt;/marketing&gt;
 * 				&lt;netbusiness&gt;&lt;/netbusiness&gt;
 * 			&lt;/modules&gt;		
 * 	&lt;/application&gt;
 * &lt;/root&gt;
 * </pre>
 * 
 * @author Jerome RADUGET
 */
@Declare(ConfigurationConstants.XmlConfigurationPluginName)
public class XmlConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public XmlConfiguration(final String name, final URI resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
	super(name, resourceUri, context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    */
   public XmlConfiguration(final String name, final String resourceUri, final RuntimeContext<Configuration> context) throws ResourceException {
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

	try {
	   final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	   final DocumentBuilder documentBuilder = domFactory.newDocumentBuilder();
	   final Document document = documentBuilder.parse(resourceHandler.getInputStream());
	   final Element root = document.getDocumentElement();

	   if (root != null) {
		feedProperties(root.getChildNodes(), new StringBuilder(), properties);
	   }

	   return properties;

	} catch (final ParserConfigurationException pce) {
	   throw new ConfigurationException("config.load.xml.dom.error", pce);
	} catch (final SAXException sae) {
	   throw new ConfigurationException("config.load.xml.parsing.error", sae, singleResourceStore.getResourceBinding().toString());
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfiguration#storeProperties(org.kaleidofoundry.core.cache.Cache,
    * org.kaleidofoundry.core.store.SingleResourceStore)
    */
   @Override
   @NotYetImplemented
   protected Cache<String, Serializable> storeProperties(final Cache<String, Serializable> cacheProperties, final SingleResourceStore resourceStore)
	   throws ResourceException, ConfigurationException {
	return null; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

   /**
    * recursive method, use to
    * 
    * @param nodeList
    * @param keyName
    * @param properties
    */
   protected void feedProperties(final NodeList nodeList, final StringBuilder keyName, final Cache<String, Serializable> properties) {
	for (int i = 0; i < nodeList.getLength(); i++) {
	   final Node node = nodeList.item(i);
	   if (node.getNodeType() == Node.ELEMENT_NODE) {
		final StringBuilder newKeyName = new StringBuilder(keyName).append(keyName.length() > 0 ? KeyPropertiesSeparator : "").append(node.getNodeName());
		// node without element child, but which containing test
		if (node.hasChildNodes() && node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
		   // LOGGER.debug("found key={} , value={}", newKeyName.toString(), node.getTextContent());
		   properties.put(normalizeKey(newKeyName.toString()), node.getTextContent());
		}
		// node without element child, but which containing test
		else if (!node.hasChildNodes()) {
		   // LOGGER.debug("found key={} , value={}", newKeyName.toString(), "");
		   properties.put(normalizeKey(newKeyName.toString()), "");
		} else {
		   final List<String> multipleValues = nodeMultipleValues(node.getChildNodes());
		   // simple element, recursive call, on children nodes
		   if (multipleValues == null) {
			feedProperties(node.getChildNodes(), newKeyName, properties);
		   }
		   // multiple value have been detected
		   else {
			properties.put(normalizeKey(newKeyName.toString()), StringHelper.unsplit(MultiValDefaultSeparator, multipleValues
				.toArray(new String[multipleValues.size()])));
			// properties.setMultiValueProperty(newKeyName.toString(), multipleValues.toArray(new String[multipleValues.size()]));
		   }
		}
	   }
	}
   }

   /**
    * @param nodeList
    * @return node multiple values
    */
   protected List<String> nodeMultipleValues(final NodeList nodeList) {
	List<String> result = null;
	for (int i = 0; i < nodeList.getLength(); i++) {
	   final Node node = nodeList.item(i);
	   if (node.getNodeType() == Node.ELEMENT_NODE && "value".equals(node.getNodeName())) {
		if (result == null) {
		   result = new ArrayList<String>();
		}
		if (node.hasChildNodes() && node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
		   result.add(node.getTextContent());
		}
	   }
	}
	return result;
   }

}
