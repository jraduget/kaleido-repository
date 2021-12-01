/*
 *  Copyright 2008-2021 the original author or authors.
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
package org.kaleidofoundry.core.naming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.ResourceException;
/**
 * @author jraduget
 */
public abstract class RemoteJndiIntegrationTest  {

   /**
    * @return naming service instance to test
    */
   public abstract NamingServiceJndiSample getNamingServiceJndiSample();

   @Before
   public void setup() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/naming/myContext.properties
	ConfigurationFactory.provides("myConfig", "classpath:/naming/myContext.properties");
   }

   @After
   public void cleanupClass() throws ResourceException {
	I18nMessagesFactory.clearCache();
	ConfigurationFactory.unregister("myConfig");
   }

   @Test
   public void testDatasource() throws SQLException {
	final String message = "hello world !";
	NamingServiceJndiSample jndiSample = getNamingServiceJndiSample();
	assertNotNull(jndiSample);
	assertNotNull(jndiSample.echoFromDatabase(message));
	assertEquals(message, jndiSample.echoFromDatabase(message));
   }

   @Test
   public void testJms() throws JMSException {
	final String message = "hello world !";
	NamingServiceJndiSample jndiSample = getNamingServiceJndiSample();
	assertNotNull(jndiSample);
	TextMessage jmsMessage = jndiSample.echoFromJMS(message);
	assertNotNull(jmsMessage);
	assertEquals(message, jmsMessage.getText());
	assertNotNull(jmsMessage.getJMSMessageID());
   }

   @Test
   public void testEjb() {
	NamingServiceJndiSample jndiSample = getNamingServiceJndiSample();
	assertNotNull(jndiSample);
	assertEquals("hello world", jndiSample.echoFromEJB("hello world"));
	assertEquals("hello world2", jndiSample.echoFromEJB("hello world2"));
	assertEquals("hello world3", jndiSample.echoFromEJB("hello world3"));
	assertFalse("foo?".equals(jndiSample.echoFromEJB("foo")));
   }

   @Test(expected=NamingServiceNotFoundException.class)
   public void testResourceNotFound() {
	NamingServiceJndiSample jndiSample = getNamingServiceJndiSample();
	assertNotNull(jndiSample);
	jndiSample.getNamingService().locate("jdbc/unknown", DataSource.class);

   }
}
