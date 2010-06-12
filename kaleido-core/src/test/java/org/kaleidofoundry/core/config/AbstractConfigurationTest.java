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
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.lang.NotNullException;
import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractConfigurationTest extends Assert {

   protected Configuration configuration;

   /**
    * create configuration instance calling abstract method {@link #newInstance()}<br/>
    * and load it after creation
    * 
    * @throws StoreException
    * @throws URISyntaxException
    */
   @Before
   public void setup() throws StoreException, URISyntaxException {
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
    * @throws StoreException
    */
   @After
   public void cleanup() throws StoreException {
	if (configuration != null) {
	   configuration.unload();
	}
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   /**
    * the configuration manager to load and test
    * 
    * @return
    * @throws StoreException
    * @throws URISyntaxException
    */
   protected abstract Configuration newInstance() throws StoreException, URISyntaxException;

   @Test
   public void isLoaded() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void load() throws StoreException, URISyntaxException {
	Configuration configuration = newInstance();
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	configuration.load();
	assertTrue(configuration.isLoaded());
   }

   @Test
   public void unload() throws StoreException, URISyntaxException {
	Configuration configuration = newInstance();
	assertNotNull(configuration);
	assertFalse(configuration.isLoaded());
	configuration.load();
	assertTrue(configuration.isLoaded());
	configuration.unload();
	assertTrue(!configuration.isLoaded());
   }

   @Test
   @Ignore
   public void store() throws StoreException {
	assertNotNull(configuration);
	// TODO configuration.store();
	throw new NotYetImplementedException();
   }

   @Test
   public void getProperty() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getRawProperty("//application/name"));
	assertEquals("1.0.0", configuration.getRawProperty("//application/version"));
	assertEquals("description of the application...", configuration.getRawProperty("//application/description"));
	// test unknown key
	assertNull(configuration.getRawProperty("foo"));
   }

   @Test
   public void setProperty() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getRawProperty("//application/name"));
	configuration.setProperty("//application/name", "foo");
	assertEquals("foo", configuration.getRawProperty("//application/name"));
   }

   @Test
   public void removeProperty() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getRawProperty("//application/name"));
	configuration.removeProperty("//application/name");
	assertNull(configuration.getRawProperty("//application/name"));
   }

   @Test
   public void getString() {
	assertNotNull(configuration);
	assertEquals("app", configuration.getString("//application/name"));
	assertEquals("1.0.0", configuration.getString("//application/version"));
	assertEquals("description of the application...", configuration.getString("//application/description"));
	// test unknown key
	assertNull(configuration.getString("foo"));
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
	assertEquals("2006-09-01T00:00:00", configuration.getRawProperty("//application/date"));
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
	assertEquals("2009-01-02T00:00:00 2009-12-31T00:00:00 2012-05-15T00:00:00", configuration.getRawProperty("//application/array/date"));
	assertNotNull(configuration.getDateList("//application/array/date"));
	assertEquals(3, configuration.getDateList("//application/array/date").size());
	assertEquals(df.parse("2009-01-02"), configuration.getDateList("//application/array/date").get(0));
	assertEquals(df.parse("2009-12-31"), configuration.getDateList("//application/array/date").get(1));
	assertEquals(df.parse("2012-05-15"), configuration.getDateList("//application/array/date").get(2));
	// test unknown key
	assertTrue(configuration.getDateList("foo").isEmpty());
   }

   @Test
   public void getBigDecimal() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("1.123456789", configuration.getRawProperty("//application/single/bigdecimal"));
	assertEquals(new BigDecimal("1.123456789"), configuration.getBigDecimal("//application/single/bigdecimal"));
	// test unknown key
	assertNull(configuration.getBigDecimal("foo"));
   }

   @Test
   public void getBigDecimalList() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("987.5 1.123456789", configuration.getRawProperty("//application/array/bigdecimal"));
	assertEquals(2, configuration.getBigDecimalList("//application/array/bigdecimal").size());
	assertEquals(new BigDecimal("987.5"), configuration.getBigDecimalList("//application/array/bigdecimal").get(0));
	assertEquals(new BigDecimal("1.123456789"), configuration.getBigDecimalList("//application/array/bigdecimal").get(1));
	// test unknown key
	assertTrue(configuration.getBigDecimalList("foo").isEmpty());
   }

   @Test
   public void getBoolean() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("true", configuration.getRawProperty("//application/single/boolean"));
	assertTrue(configuration.getBoolean("//application/single/boolean"));
	// test unknown key
	assertNull(configuration.getBoolean("foo"));
   }

   @Test
   public void getBooleanList() {
	assertNotNull(configuration);
	// test raw property
	assertEquals("false true", configuration.getRawProperty("//application/array/boolean"));
	assertEquals(2, configuration.getBooleanList("//application/array/boolean").size());
	assertTrue(!configuration.getBooleanList("//application/array/boolean").get(0));
	assertTrue(configuration.getBooleanList("//application/array/boolean").get(1));
	// test unknown key
	assertTrue(configuration.getBooleanList("foo").isEmpty());
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
	} catch (NotNullException nae) {
	}
   }

   @Test
   public void containsKey() {
	assertNotNull(configuration);
	assertTrue(configuration.containsKey("//application/modules/marketing/name", "//application/modules"));
	assertFalse(configuration.containsKey("//application/modules/marketing", "foo"));
	try {
	   configuration.containsKey("//application/modules/marketing", null);
	   fail("null prefix is not allowed");
	} catch (NotNullException nae) {
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
	} catch (NotNullException nae) {
	}
   }

   @Test
   public void addConfiguration() throws StoreException, URISyntaxException {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());

	assertNotNull(configuration.roots());
	assertNotNull(configuration.keySet());

	int roots = configuration.roots().size();
	int keys = configuration.keySet().size();

	// assertions before adding new configuration content
	assertTrue(configuration.containsRoot("application", ""));
	assertFalse(configuration.containsRoot("application2", ""));
	assertFalse(configuration.containsKey("//application2/name", ""));
	assertEquals("", configuration.getString("//application/modules/netbusiness/name"));

	// configuration to add
	Configuration configurationToAdd = new PropertiesConfiguration("app2Config", "classpath:/org/kaleidofoundry/core/config/addTest.properties",
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>());
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

   }

   @Test
   public void extractConfiguration() throws StoreException, URISyntaxException {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());

	// configuration to add
	Configuration emptyConfiguration = new PropertiesConfiguration("empty", "classpath:/org/kaleidofoundry/core/config/empty.properties",
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>());

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
	assertEquals("dom4j.jar log4j.jar mail.jar", configuration.toProperties().getProperty("application.librairies"));
	assertEquals("Sales", configuration.toProperties().getProperty("application.modules.sales.name"));
	assertEquals("1.1.0", configuration.toProperties().getProperty("application.modules.sales.version"));
	assertEquals("Market.", configuration.toProperties().getProperty("application.modules.marketing.name"));
	assertEquals("", configuration.toProperties().getProperty("application.modules.netbusiness.name"));
	assertEquals("987.5 1.123456789", configuration.toProperties().getProperty("application.array.bigdecimal"));
	assertEquals("false true", configuration.toProperties().getProperty("application.array.boolean"));
	assertEquals("true", configuration.toProperties().getProperty("application.single.boolean"));
	assertEquals("1.123456789", configuration.toProperties().getProperty("application.single.bigdecimal"));
	assertEquals("2009-01-02T00:00:00 2009-12-31T00:00:00 2012-05-15T00:00:00", configuration.toProperties().getProperty("application.array.date"));

   }

   @Test
   public void toPropertiesCount() {
	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	assertEquals(14, configuration.toProperties().stringPropertyNames().size());
   }

   @Test
   public void toPropertiesWithPreifx() {

	assertNotNull(configuration);
	assertTrue(configuration.isLoaded());
	assertEquals(4, configuration.toProperties("//application/modules").entrySet().size());
	assertEquals("Sales", configuration.toProperties("//application/modules").getProperty("application.modules.sales.name"));
	assertEquals("1.1.0", configuration.toProperties("//application/modules").getProperty("application.modules.sales.version"));
	assertEquals("Market.", configuration.toProperties("//application/modules").getProperty("application.modules.marketing.name"));
	assertEquals("", configuration.toProperties("//application/modules").getProperty("application.modules.netbusiness.name"));
   }

}
