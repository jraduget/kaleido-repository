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

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.lang.NotNullException;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractConfigurationTest extends Assert {

   protected Configuration configuration;

   /**
    * create configuration instance calling abstract method {@link #newInstance()}<br/>
    * and load it after creation
    * 
    * @throws ResourceException
    * @throws URISyntaxException
    */
   @Before
   public void setup() throws ResourceException, URISyntaxException {
	// disable i18n message bundle control to speed up test (no need of a local derby instance startup)
	I18nMessagesFactory.disableJpaControl();
	// create and load instance
	configuration = newInstance();
	if (configuration != null) {
	   configuration.load();
	}
   }

   /**
    * unload and cleanup internal configuration cache
    * 
    * @throws ResourceException
    */
   @After
   public void cleanup() throws ResourceException {
	if (configuration != null) {
	   configuration.unload();
	}
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   /**
    * @return the configuration manager to load and test
    * @throws ResourceException
    * @throws URISyntaxException
    */
   protected abstract Configuration newInstance() throws ResourceException, URISyntaxException;

   @Test
   public void isLoaded() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void load() throws ResourceException, URISyntaxException {
	final Configuration configuration = newInstance();
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	configuration.load();
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void unload() throws ResourceException, URISyntaxException {
	final Configuration configuration = newInstance();
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	configuration.load();
	assertTrue(configuration.isLoaded());
	configuration.unload();
	assertTrue(!configuration.isLoaded());
   }

   @Test
   public void store() throws ResourceException, URISyntaxException {

	assertNotNull(configuration);

	final Configuration configuration = newInstance();
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	try {
	   configuration.store();
	   fail();
	} catch (final ConfigurationException ce) {
	   assertEquals("config.load.notloaded", ce.getCode());
	}

	configuration.load();
	assertTrue(configuration.isLoaded());
	try {
	   configuration.store();
	   if (!configuration.isStorable()) {
		fail();
	   }
	} catch (final ResourceException se) {
	   assertEquals("store.readonly.illegal", se.getCode());
	}

	// can't test store anymore
	// -> store can be a classpath resource and store method is not handled
	// -> store() is tested in file store module
   }

   @Test
   public void getProperty() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getProperty("//application/name"));
	assertEquals("1.0.0", configuration.getProperty("//application/version"));
	assertEquals("description of the application...", configuration.getProperty("//application/description"));
	assertEquals("", configuration.getProperty("//application/modules/netbusiness/name"));
	// test unknown key
	assertNull(configuration.getProperty("foo"));
   }

   @Test
   public void setProperty() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getProperty("//application/name"));
	configuration.setProperty("//application/name", "foo");
	assertEquals("foo", configuration.getProperty("//application/name"));
	// restore right value
	configuration.setProperty("//application/name", "app");
   }

   @Test
   public void removeProperty() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getProperty("//application/name"));
	configuration.removeProperty("//application/name");
	assertNull(configuration.getProperty("//application/name"));
   }

   @Test
   public void getCharacter() {
	assertNotNull(configuration);
	assertEquals(Character.valueOf('a'), configuration.getCharacter("//application/name"));
	assertEquals(Character.valueOf('1'), configuration.getCharacter("//application/version"));
	assertEquals(Character.valueOf('d'), configuration.getCharacter("//application/description"));
	assertNull(configuration.getCharacter("//application/modules/netbusiness/name"));
	// test unknown key
	assertNull(configuration.getCharacter("foo"));
	assertEquals(Character.valueOf('e'), configuration.getCharacter("foo", 'e'));
   }

   @Test
   public void getCharacterList() {
	assertNotNull(configuration);
	final List<Character> values = configuration.getCharacterList("//application/librairies");
	assertNotNull(values);
	assertEquals(3, values.size());
	assertEquals(Character.valueOf('d'), values.get(0));
	assertEquals(Character.valueOf('l'), values.get(1));
	assertEquals(Character.valueOf('m'), values.get(2));
	// test unknown key
	assertNull(configuration.getCharacterList("foo"));
   }

   @Test
   public void getString() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getString("//application/name"));
	assertEquals("1.0.0", configuration.getString("//application/version"));
	assertEquals("description of the application...", configuration.getString("//application/description"));
	assertEquals("", configuration.getString("//application/modules/netbusiness/name"));
	// test unknown key
	assertNull(configuration.getString("foo"));
	assertEquals("eheh", configuration.getString("foo", "eheh"));
   }

   @Test
   public void getStringList() {
	assertNotNull(configuration);
	final List<String> values = configuration.getStringList("//application/librairies");
	assertNotNull(values);
	assertEquals(3, values.size());
	assertEquals("dom4j.jar", values.get(0));
	assertEquals("log4j.jar", values.get(1));
	assertEquals("mail.jar", values.get(2));
	// test unknown key
	assertNull(configuration.getStringList("foo"));
   }

   @Test
   public void getDate() throws ParseException {
	final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	final Date mockDate = df.parse("2006-09-01");
	assertNotNull(configuration);
	// test raw property
	assertEquals("2006-09-01T00:00:00", configuration.getProperty("//application/date"));
	assertEquals(mockDate, configuration.getDate("//application/date"));
	// test unknown key
	assertNull(configuration.getDate("foo"));
	assertEquals(mockDate, configuration.getDate("foo", mockDate));
   }

   @Test
   public void getDateList() throws ParseException {
	final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	assertNotNull(configuration);
	// test raw property
	assertEquals("2009-01-02T00:00:00|2009-12-31T00:00:00|2012-05-15T00:00:00", configuration.getProperty("//application/array/date"));
	assertNotNull(configuration.getDateList("//application/array/date"));
	assertEquals(3, configuration.getDateList("//application/array/date").size());
	assertEquals(df.parse("2009-01-02"), configuration.getDateList("//application/array/date").get(0));
	assertEquals(df.parse("2009-12-31"), configuration.getDateList("//application/array/date").get(1));
	assertEquals(df.parse("2012-05-15"), configuration.getDateList("//application/array/date").get(2));
	// test unknown key
	assertNull(configuration.getDateList("foo"));
   }

   @Test
   public void getBigDecimal() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("1.123456789", configuration.getProperty("//application/single/bigdecimal"));
	assertEquals(new BigDecimal("1.123456789"), configuration.getBigDecimal("//application/single/bigdecimal"));
	// test unknown key
	assertNull(configuration.getBigDecimal("foo"));
	assertEquals(new BigDecimal(1.123456789), configuration.getBigDecimal("foo", new BigDecimal(1.123456789)));
   }

   @Test
   public void getBigDecimalList() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("987.5|1.123456789", configuration.getProperty("//application/array/bigdecimal"));
	assertEquals(2, configuration.getBigDecimalList("//application/array/bigdecimal").size());
	assertEquals(new BigDecimal("987.5"), configuration.getBigDecimalList("//application/array/bigdecimal").get(0));
	assertEquals(new BigDecimal("1.123456789"), configuration.getBigDecimalList("//application/array/bigdecimal").get(1));
	// test unknown key
	assertNull(configuration.getBigDecimalList("foo"));
   }

   @Test
   public void getBoolean() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("true", configuration.getProperty("//application/single/boolean"));
	assertTrue(configuration.getBoolean("//application/single/boolean"));
	// test unknown key
	assertNull(configuration.getBoolean("foo"));
	assertEquals(Boolean.TRUE, configuration.getBoolean("foo", Boolean.TRUE));
   }

   @Test
   public void getBooleanList() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("false|true", configuration.getProperty("//application/array/boolean"));
	assertEquals(2, configuration.getBooleanList("//application/array/boolean").size());
	assertTrue(!configuration.getBooleanList("//application/array/boolean").get(0));
	assertTrue(configuration.getBooleanList("//application/array/boolean").get(1));
	// test unknown key
	assertNull(configuration.getBooleanList("foo"));
   }

   @Test
   public void keySet() {
	assertNotNull(configuration);
	assertNotNull(configuration.keySet());
	assertEquals(14, configuration.keySet().size());
   }

   @Test
   public void keySetWithPrefix() {
	assertNotNull(configuration);
	assertNotNull(configuration.keySet("//application/modules"));
	assertEquals(4, configuration.keySet("//application/modules").size());
	// @NotNull result
	assertNotNull(configuration.keySet("//application/modules/foo"));
	assertEquals(0, configuration.keySet("//application/modules/foo").size());
	// @NotNull argument
	try {
	   configuration.keySet(null);
	   fail("null prefix is not allowed");
	} catch (final NotNullException nae) {
	}
   }

   @Test
   public void containsKey() {
	assertNotNull(configuration);
	assertTrue(configuration.containsKey("//application/modules/marketing/name", "//application/modules"));
	assertTrue(configuration.containsKey("application.modules.marketing.name", "application.modules"));
	assertTrue(configuration.containsKey("application.modules.marketing.name", ""));
	assertTrue(configuration.containsKey("application.modules.marketing.name"));
	assertFalse(configuration.containsKey("//application/modules/marketing", "foo"));
	try {
	   configuration.containsKey("//application/modules/marketing", null);
	   fail("null prefix is not allowed");
	} catch (final NotNullException nae) {
	}
   }

   @Test
   public void roots() {
	assertNotNull(configuration);
	assertNotNull(configuration.roots());
	assertEquals(1, configuration.roots().size());
   }

   @Test
   public void rootsWithPrefix() {
	assertNotNull(configuration);
	assertNotNull(configuration.roots("//application/modules"));
	assertEquals(3, configuration.roots("//application/modules").size());

	assertTrue(configuration.roots("//application/modules").contains("sales"));
	assertTrue(configuration.roots("//application/modules").contains("marketing"));
	assertTrue(configuration.roots("//application/modules").contains("netbusiness"));
   }

   @Test
   public void containsRoot() {
	assertNotNull(configuration);
	assertTrue(configuration.containsRoot("sales", "//application/modules"));
	assertFalse(configuration.containsRoot("foo", "//application/modules"));
	try {
	   configuration.containsRoot("foo", null);
	   fail("null prefix is not allowed");
	} catch (final NotNullException nae) {
	}
   }

   @Test
   public void addConfiguration() throws ResourceException, URISyntaxException {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());

	assertNotNull(configuration.roots());
	assertNotNull(configuration.keySet());

	final int roots = configuration.roots().size();
	final int keys = configuration.keySet().size();

	// assertions before adding new configuration content
	assertTrue(configuration.containsRoot("application", ""));
	assertFalse(configuration.containsRoot("application2", ""));
	assertFalse(configuration.containsKey("//application2/name", ""));
	assertEquals("", configuration.getString("//application/modules/netbusiness/name"));

	// configuration to add
	final Configuration configurationToAdd = new PropertiesConfiguration("app2Config", "classpath:/config/addTest.properties",
		new RuntimeContext<Configuration>(Configuration.class));
	configurationToAdd.load();

	configuration.addConfiguration(configurationToAdd);

	// new assertions after adding new configuration content
	assertEquals(roots + 1, configuration.roots().size());
	assertEquals(keys + 2, configuration.keySet().size());
	assertTrue(configuration.containsRoot("application", ""));
	assertTrue(configuration.containsRoot("application2", ""));
	assertTrue(configuration.containsKey("//application2/name", ""));
	// test override
	assertEquals("Netbusi.", configuration.getString("//application/modules/netbusiness/name"));
	// restore original value
	configuration.setProperty("//application/modules/netbusiness/name", "");

   }

   @Test
   public void extractConfiguration() throws ResourceException, URISyntaxException {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());

	// configuration to add
	final Configuration emptyConfiguration = new PropertiesConfiguration("empty", "classpath:/config/empty.properties", new RuntimeContext<Configuration>(
		Configuration.class));

	assertNotNull(emptyConfiguration);

	configuration.extractConfiguration("//application/modules", emptyConfiguration);

	assertEquals(4, emptyConfiguration.keySet().size());
	assertTrue(emptyConfiguration.keySet().contains("//sales/version"));
	assertTrue(emptyConfiguration.keySet().contains("//marketing/name"));
	assertTrue(emptyConfiguration.keySet().contains("//sales/name"));
	assertTrue(emptyConfiguration.keySet().contains("//netbusiness/name"));

	assertEquals(3, emptyConfiguration.roots().size());
	assertTrue(emptyConfiguration.roots().contains("sales"));
	assertTrue(emptyConfiguration.roots().contains("marketing"));
	assertTrue(emptyConfiguration.roots().contains("netbusiness"));

   }

   @Test
   public void toProperties() {

	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());

	assertEquals("app", configuration.toProperties().getProperty("application.name"));
	assertEquals("description of the application...", configuration.toProperties().getProperty("application.description"));
	assertEquals("2006-09-01T00:00:00", configuration.toProperties().getProperty("application.date"));
	assertEquals("1.0.0", configuration.toProperties().getProperty("application.version"));
	assertEquals("dom4j.jar|log4j.jar|mail.jar", configuration.toProperties().getProperty("application.librairies"));
	assertEquals("Sales", configuration.toProperties().getProperty("application.modules.sales.name"));
	assertEquals("1.1.0", configuration.toProperties().getProperty("application.modules.sales.version"));
	assertEquals("Market.", configuration.toProperties().getProperty("application.modules.marketing.name"));
	assertEquals("", configuration.toProperties().getProperty("application.modules.netbusiness.name"));
	assertEquals("987.5|1.123456789", configuration.toProperties().getProperty("application.array.bigdecimal"));
	assertEquals("false|true", configuration.toProperties().getProperty("application.array.boolean"));
	assertEquals("true", configuration.toProperties().getProperty("application.single.boolean"));
	assertEquals("1.123456789", configuration.toProperties().getProperty("application.single.bigdecimal"));
	assertEquals("2009-01-02T00:00:00|2009-12-31T00:00:00|2012-05-15T00:00:00", configuration.toProperties().getProperty("application.array.date"));

   }

   @Test
   public void toPropertiesCount() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	assertEquals(14, configuration.toProperties().stringPropertyNames().size());
   }

   @Test
   public void toPropertiesWithPrefix() {

	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	assertEquals(4, configuration.toProperties("//application/modules").entrySet().size());
	assertEquals("Sales", configuration.toProperties("//application/modules").getProperty("application.modules.sales.name"));
	assertEquals("1.1.0", configuration.toProperties("//application/modules").getProperty("application.modules.sales.version"));
	assertEquals("Market.", configuration.toProperties("//application/modules").getProperty("application.modules.marketing.name"));
	assertEquals("", configuration.toProperties("//application/modules").getProperty("application.modules.netbusiness.name"));
   }

}
