/*
 *  Copyright 2008-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.config;

import java.util.List;


import org.junit.Test;
import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.store.ResourceException;

import static org.junit.Assert.*;

/**
 * @author jraduget
 */
public abstract class AbstractConfigurationManagerTest  {

   protected static final String MyConfigurationName = "myNamedConfig";
   protected static final String MyConfigurationUri = "classpath:/config/myNamedConfig.properties";

   protected final ConfigurationController configurationManager;

   public AbstractConfigurationManagerTest(final ConfigurationController configurationManager) {
	this.configurationManager = configurationManager;
   }

   @Test
   public void getConfigurationModel() {
	try {
	   configurationManager.getModel("unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	ConfigurationModel configModel = configurationManager.getModel(MyConfigurationName);
	assertNotNull(configModel);
	assertEquals(MyConfigurationName, configModel.getName());
	assertEquals(MyConfigurationUri, configModel.getUri());
   }

   @Test
   public void findConfigurationModel() {
	List<ConfigurationModel> models;
	models = configurationManager.findModel("unknown");
	assertNotNull(models);
	assertTrue(models.isEmpty());

	// search all
	models = configurationManager.findModel(null);
	assertNotNull(models);
	assertFalse(models.isEmpty());
	assertEquals(1, models.size());

	// search by name
	models = configurationManager.findModel("Named");
	assertNotNull(models);
	assertFalse(models.isEmpty());
	assertEquals(1, models.size());

	// search by uri
	models = configurationManager.findModel("config/");
	assertNotNull(models);
	assertFalse(models.isEmpty());
	assertEquals(1, models.size());
   }

   @Test
   public void removeConfigurationModel() throws ResourceException {

	try {
	   configurationManager.removeModel("unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	ConfigurationModel configModel = configurationManager.getModel(MyConfigurationName);
	assertNotNull(configModel);
	assertEquals(MyConfigurationName, configModel.getName());
	assertEquals(MyConfigurationUri, configModel.getUri());

	// remove from database
	configurationManager.removeModel(MyConfigurationName);

	// unregister in memory configuration
	try {
	   configurationManager.unregister(MyConfigurationName);
	} catch (ConfigurationNotFoundException cnfe) {
	}

	try {
	   configModel = configurationManager.getModel(MyConfigurationName);
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}
   }

   @Test
   public void register() throws ResourceException {
	try {
	   assertFalse(ConfigurationFactory.getRegistry().containsKey("test"));
	   configurationManager.register("test", "classpath:/config/test.properties");
	   assertTrue(ConfigurationFactory.getRegistry().containsKey("test"));
	} finally {
	   ConfigurationFactory.unregister("test");
	}
   }

   @Test
   public void unregister() throws ResourceException {
	try {
	   assertFalse(ConfigurationFactory.getRegistry().containsKey("test"));
	   configurationManager.register("test", "classpath:/config/test.properties");
	   assertTrue(ConfigurationFactory.getRegistry().containsKey("test"));
	   configurationManager.unregister("test");
	   assertFalse(ConfigurationFactory.getRegistry().containsKey("test"));
	} finally {
	   try {
		ConfigurationFactory.unregister("test");
	   } catch (ConfigurationNotFoundException cnfe) {
	   }
	}
   }

   @Test
   public void getProperty() {
	try {
	   configurationManager.getProperty("unknown", "//unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	try {
	   configurationManager.getProperty(MyConfigurationName, "//unknown");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}

	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key01");
	assertNotNull(property);
	assertEquals("//key01", property.getName());
	assertEquals(String.class, property.getType());
	assertEquals("value01", property.getValue());
	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());
   }

   @Test
   public void getPropertyValue() {
	try {
	   configurationManager.getPropertyValue("unknown", "//unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}
	try {
	   configurationManager.getPropertyValue(MyConfigurationName, "unknown");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertNotNull(property);
	assertEquals("//key02", property.getName());
	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());
   }

   @Test
   public void findProperties() {
	List<ConfigurationProperty> properties;

	try {
	   configurationManager.findProperties("unknown", "key01");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	properties = configurationManager.findProperties(null, null);
	assertNotNull(properties);
	assertFalse(properties.isEmpty());
	assertEquals(2, properties.size());

	properties = configurationManager.findProperties(null, "key0");
	assertNotNull(properties);
	assertFalse(properties.isEmpty());
	assertEquals(2, properties.size());

	properties = configurationManager.findProperties(MyConfigurationName, "unknown");
	assertNotNull(properties);
	assertTrue(properties.isEmpty());

	// search in name
	properties = configurationManager.findProperties(MyConfigurationName, "key0");
	assertNotNull(properties);
	assertFalse(properties.isEmpty());
	assertEquals(2, properties.size());

	// search in value
	properties = configurationManager.findProperties(MyConfigurationName, "value01");
	assertNotNull(properties);
	assertFalse(properties.isEmpty());
	assertEquals(1, properties.size());
   }

   @Test
   public void setPropertyValue() {
	try {
	   configurationManager.setPropertyValue("unknown", "//unknown", "");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}
	// try {
	// configurationManager.setPropertyValue(MyConfigurationName, "//unknown", "");
	// fail();
	// } catch (PropertyNotFoundException pnfe) {
	// }
	Float oldValue = configurationManager.setPropertyValue(MyConfigurationName, "//key02", 678.9f, Float.class);
	assertEquals(Float.valueOf(678.9f), configurationManager.getPropertyValue(MyConfigurationName, "//key02", Float.class));
	assertNotNull(oldValue);
	assertEquals("123.45", String.valueOf(oldValue));

	// store have not been called, check that property value don't change in the persistence layer
	ConfigurationProperty property = configurationManager.getProperty(MyConfigurationName, "//key02");
	assertNotNull(property);
	// assertEquals(123.45f, property.getValue()); //if configuration is only in model database, assertion can't be true
   }

   @Test
   public void putProperty() {

	try {
	   configurationManager.putProperty("unknown", new ConfigurationProperty());
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}
	try {
	   configurationManager.getProperty(MyConfigurationName, "//newKey");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}

	assertEquals(2, configurationManager.keySet(MyConfigurationName).size());

	ConfigurationProperty property = new ConfigurationProperty("//newKey", "newValue", String.class, "newDescription");
	assertNull(property.getId());
	configurationManager.putProperty(MyConfigurationName, property);

	assertEquals(3, configurationManager.keySet(MyConfigurationName).size());

	property = configurationManager.getProperty(MyConfigurationName, "//newKey");
	assertNotNull(property);
	assertNotNull(property.getId());
	assertEquals("//newKey", property.getName());
	assertEquals("newDescription", property.getDescription());
	assertEquals(String.class, property.getType());
	assertEquals("newValue", property.getValue());
	assertNotNull(property.getConfigurations());
	assertEquals(1, property.getConfigurations().size());

	// clean
	configurationManager.removeProperty(MyConfigurationName, "//newKey");

   }

   @Test
   public void removeProperty() {

	try {
	   configurationManager.removeProperty("unknown", "//unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}
	try {
	   configurationManager.removeProperty(MyConfigurationName, "//unknown");
	   fail();
	} catch (PropertyNotFoundException pnfe) {
	}

	assertFalse(configurationManager.keySet(MyConfigurationName).contains("//newKey"));
	assertEquals(2, configurationManager.keySet(MyConfigurationName).size());

	ConfigurationProperty property = new ConfigurationProperty("//newKey", "newValue", String.class, "newDescription");
	assertNull(property.getId());
	configurationManager.putProperty(MyConfigurationName, property);

	assertEquals(3, configurationManager.keySet(MyConfigurationName).size());
	assertTrue(configurationManager.keySet(MyConfigurationName).contains("//newKey"));

	configurationManager.removeProperty(MyConfigurationName, "//newKey");

	assertFalse(configurationManager.keySet(MyConfigurationName).contains("//newKey"));
	assertEquals(2, configurationManager.keySet(MyConfigurationName).size());
   }

   @Test
   public void keySet() {

	try {
	   configurationManager.keySet("//unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	assertNotNull(configurationManager.keySet(MyConfigurationName));
	assertTrue(configurationManager.keySet(MyConfigurationName).contains("//key01"));
	assertTrue(configurationManager.keySet(MyConfigurationName).contains("//key02"));
	assertEquals(2, configurationManager.keySet(MyConfigurationName).size());
   }

   @Test
   public void containsKey() {
	assertTrue(configurationManager.containsKey(MyConfigurationName, "//key01"));
	assertTrue(configurationManager.containsKey(MyConfigurationName, "//key02"));
	assertFalse(configurationManager.containsKey(MyConfigurationName, "//key03"));
   }

   @Test
   public void store() {
	try {
	   configurationManager.store("unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	} catch (ResourceException se) {
	   fail();
	}
   }
}
