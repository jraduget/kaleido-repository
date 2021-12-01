/*  
 * Copyright 2008-2021 the original author or authors 
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
package org.kaleidofoundry.messaging.jms;

import static org.kaleidofoundry.messaging.MessagingConstantsTests.JMS_CONFIG_PATH;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.JMS_CONFIG_TRANSPORT_KEY;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import org.kaleidofoundry.messaging.Transport;
import org.kaleidofoundry.messaging.TransportException;
import org.kaleidofoundry.messaging.TransportProviderEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Test JMS Transport
 * 
 * @author jraduget
 */
@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "jmsTransport", uri = JMS_CONFIG_PATH)
public class JmsTransportTest  {

   static final Logger LOGGER = LoggerFactory.getLogger(JmsTransportTest.class);

   // Active MQ embedded broker
   private static BrokerService ActiveMqBroker;

   private JmsTransport jmsTransport;
   
   @BeforeClass
   public static void setupStatic() throws Exception {
	
	// Manually configure a embedded active MQ instance
	ActiveMqBroker = new BrokerService();
	ActiveMqBroker.setPersistent(false);
	ActiveMqBroker.start();
   }
   
   @AfterClass
   public static void cleanupStatic() throws Exception {
	ActiveMqBroker.stop();
   }
   
   @Before
   public void setUp() throws Exception {
	jmsTransport = new JmsTransport(new RuntimeContext<Transport>(JMS_CONFIG_TRANSPORT_KEY, Transport.class));
   }

   @After
   public void cleanup() throws Exception {
	jmsTransport.close();
   }

   @Test
   public void jmsTransport() {

	assertNotNull(jmsTransport);

	assertEquals(TransportProviderEnum.jms.getCode(), jmsTransport.getProviderCode());
	assertEquals(TransportProviderEnum.jms.getVersion(), jmsTransport.getProviderVersion());
	assertEquals(1, jmsTransport.getAcknowledgeMode());
	assertEquals("activemq", jmsTransport.getNamingServiceRef());
	assertEquals("guest", jmsTransport.getUser());
	assertEquals("guest", jmsTransport.getPassword());
	assertEquals(false, jmsTransport.isSessionTransacted());
		
	try {
	   Connection connection = jmsTransport.createConnection();
	   Session session = jmsTransport.createSession(connection);
	   
	   assertNotNull(jmsTransport.sessions);
	   assertEquals(1, jmsTransport.sessions.size());
	   assertEquals(1, jmsTransport.connections.size());
	   
	   jmsTransport.closeSession(session);	  
	   assertEquals(0, jmsTransport.sessions.size());
	   assertEquals(1, jmsTransport.connections.size());
	   
	   jmsTransport.closeConnection(connection);
	   assertEquals(0, jmsTransport.sessions.size());
	   assertEquals(0, jmsTransport.connections.size());
	   
	   connection = jmsTransport.createConnection();
	   assertEquals(0, jmsTransport.sessions.size());
	   assertEquals(1, jmsTransport.connections.size());
	   
	   session = jmsTransport.createSession(connection);
	   assertEquals(1, jmsTransport.sessions.size());
	   assertEquals(1, jmsTransport.connections.size());
	   
	   jmsTransport.close();
	   
	   assertEquals(0, jmsTransport.sessions.size());
	   assertEquals(0, jmsTransport.connections.size());
	   
	} catch (final TransportException tme) {
	   LOGGER.error(tme.getMessage(), tme);
	   fail(tme.getMessage());
	}
   }

}
