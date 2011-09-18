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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * Test all configuration listeners :
 * <ul>
 * <li> {@link ConfigurationListener#propertyCreate(ConfigurationChangeEvent)}</li>
 * <li> {@link ConfigurationListener#propertyUpdate(ConfigurationChangeEvent)}</li>
 * <li> {@link ConfigurationListener#propertyRemove(ConfigurationChangeEvent)}</li>
 * <li> {@link ConfigurationListener#configurationUnload(Configuration)}</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractConfigurationListenerTest extends Assert {

   private Configuration configuration;

   private int fireCreateCount = 10;
   private int fireUpdateCount = 20;
   private int fireRemoveCount = 30;
   private int fireUnloadCount = 40;

   private ConfigurationChangeEvent fireCreateEvent = null;
   private ConfigurationChangeEvent fireUpdateEvent = null;
   private ConfigurationChangeEvent fireRemoveEvent = null;

   /**
    * @return listener count to test 1 , 2 , 3 ...
    */
   protected abstract int getListenerCount();

   @Before
   public void setup() throws ResourceException {

	// disable jpa i18n control in order to speed up test case run
	I18nMessagesFactory.disableJpaControl();

	// create & load configuration
	configuration = ConfigurationFactory.provides("myConf", "classpath:/config/test.properties");

	// add listener that will be tested
	configuration.addConfigurationListener(new ConfigurationAdapter() {

	   @Override
	   public void propertyCreate(final ConfigurationChangeEvent evt) {
		fireCreateCount = fireCreateCount + getListenerCount();
		fireCreateEvent = evt;
	   }

	   @Override
	   public void propertyUpdate(final ConfigurationChangeEvent evt) {
		fireUpdateCount = fireUpdateCount + getListenerCount();
		fireUpdateEvent = evt;
	   }

	   @Override
	   public void propertyRemove(final ConfigurationChangeEvent evt) {
		fireRemoveCount = fireRemoveCount + getListenerCount();
		fireRemoveEvent = evt;
	   }

	   @Override
	   public void configurationUnload(final Configuration source) {
		fireUnloadCount = fireUnloadCount + getListenerCount();
	   }
	});
   }

   @After
   public void cleanup() throws ResourceException {
	// unregister configuration
	ConfigurationFactory.unregister("myConf");
	// re enable jpa i18n control
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void firePropertyCreateTest() {

	commonInitAssertions();

	configuration.setProperty("newProperty", "foo");
	configuration.fireConfigurationChangesEvents();

	assertEquals(10 + getListenerCount(), fireCreateCount);
	assertEquals(20, fireUpdateCount);
	assertEquals(30, fireRemoveCount);
	assertEquals(40, fireUnloadCount);

	assertNotNull(fireCreateEvent);
	assertEquals("//newProperty", fireCreateEvent.getPropertyName());
	assertNull(fireCreateEvent.getOldValue());
	assertEquals("foo", fireCreateEvent.getNewValue());
	assertSame(configuration, fireCreateEvent.getSource());
	assertNull(fireUpdateEvent);
	assertNull(fireRemoveEvent);
   }

   @Test
   public void firePropertyUpdateTest() {
	commonInitAssertions();

	configuration.setProperty("application.name", "app new name");
	configuration.fireConfigurationChangesEvents();

	assertEquals(10, fireCreateCount);
	assertEquals(20 + getListenerCount(), fireUpdateCount);
	assertEquals(30, fireRemoveCount);
	assertEquals(40, fireUnloadCount);
	assertNull(fireCreateEvent);
	assertNotNull(fireUpdateEvent);
	assertSame(configuration, fireUpdateEvent.getSource());
	assertEquals("//application/name", fireUpdateEvent.getPropertyName());
	assertEquals("app", fireUpdateEvent.getOldValue());
	assertEquals("app new name", fireUpdateEvent.getNewValue());
	assertNull(fireRemoveEvent);
   }

   @Test
   public void firePropertyRemoveTest() {
	commonInitAssertions();

	configuration.removeProperty("application.name");
	configuration.fireConfigurationChangesEvents();

	assertEquals(10, fireCreateCount);
	assertEquals(20, fireUpdateCount);
	assertEquals(30 + getListenerCount(), fireRemoveCount);
	assertEquals(40, fireUnloadCount);
	assertNull(fireCreateEvent);
	assertNull(fireUpdateEvent);
	assertNotNull(fireRemoveEvent);
	assertEquals("//application/name", fireRemoveEvent.getPropertyName());
	assertEquals("app", fireRemoveEvent.getOldValue());
	assertNull(fireRemoveEvent.getNewValue());
	assertSame(configuration, fireRemoveEvent.getSource());
   }

   @Test
   public void fireConfigurationUnloadTest() throws ResourceException {
	commonInitAssertions();

	configuration.unload();

	assertEquals(10, fireCreateCount);
	assertEquals(20, fireUpdateCount);
	assertEquals(30, fireRemoveCount);
	assertEquals(40 + getListenerCount(), fireUnloadCount);

	assertFalse(configuration.isLoaded());
	assertTrue(configuration.keySet().isEmpty());
	assertNull(configuration.getProperty("application.name"));
   }

   private void commonInitAssertions() {
	assertEquals(10, fireCreateCount);
	assertEquals(20, fireUpdateCount);
	assertEquals(30, fireRemoveCount);
	assertEquals(40, fireUnloadCount);
	assertNull(fireCreateEvent);
	assertNull(fireUpdateEvent);
	assertNull(fireRemoveEvent);
   }
}
