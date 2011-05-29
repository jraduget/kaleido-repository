/*
 *  Copyright 2008-2011 the original author or authors.
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

import org.junit.Assert;
import org.junit.Test;
import org.kaleidofoundry.core.config.entity.ConfigurationModel;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractConfigurationManagerTest extends Assert {

   protected static final String MyConfigurationName = "myNamedConfig";
   protected static final String MyConfigurationUri = "classpath:/config/myNamedConfig.properties";

   protected final ConfigurationManagerBean configurationManager;

   public AbstractConfigurationManagerTest(final ConfigurationManagerBean configurationManager) {
	this.configurationManager = configurationManager;
   }

   @Test
   public void getConfigurationModel() {
	try {
	   configurationManager.getConfigurationModel("unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	ConfigurationModel configModel = configurationManager.getConfigurationModel(MyConfigurationName);
	assertNotNull(configModel);
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

	assertEquals(2, configurationManager.keys(MyConfigurationName).size());

	ConfigurationProperty property = new ConfigurationProperty("//newKey", "newValue", String.class, "newDescription");
	assertNull(property.getId());
	configurationManager.putProperty(MyConfigurationName, property);

	assertEquals(3, configurationManager.keys(MyConfigurationName).size());

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

	assertFalse(configurationManager.keys(MyConfigurationName).contains("//newKey"));
	assertEquals(2, configurationManager.keys(MyConfigurationName).size());

	ConfigurationProperty property = new ConfigurationProperty("//newKey", "newValue", String.class, "newDescription");
	assertNull(property.getId());
	configurationManager.putProperty(MyConfigurationName, property);

	assertEquals(3, configurationManager.keys(MyConfigurationName).size());
	assertTrue(configurationManager.keys(MyConfigurationName).contains("//newKey"));

	configurationManager.removeProperty(MyConfigurationName, "//newKey");

	assertFalse(configurationManager.keys(MyConfigurationName).contains("//newKey"));
	assertEquals(2, configurationManager.keys(MyConfigurationName).size());
   }

   @Test
   public void keys() {

	try {
	   configurationManager.keys("//unknown");
	   fail();
	} catch (ConfigurationNotFoundException cnfe) {
	}

	assertNotNull(configurationManager.keys(MyConfigurationName));
	assertTrue(configurationManager.keys(MyConfigurationName).contains("//key01"));
	assertTrue(configurationManager.keys(MyConfigurationName).contains("//key02"));
	assertEquals(2, configurationManager.keys(MyConfigurationName).size());
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
	} catch (StoreException se) {
	   fail();
	}
   }
}
