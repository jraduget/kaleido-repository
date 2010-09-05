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
package org.kaleidofoundry.messaging.jms;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.JmsConfigPath;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.JmsTransportConfigKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.messaging.TransportMessagingContext;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public class TestJmsTransportMessaging {

   static final Logger LOGGER = LoggerFactory.getLogger(TestJmsTransportMessaging.class);

   private JmsTransportMessaging jmsTransport;

   @Before
   protected void setUp() throws Exception {

	Configuration jmsConfig = null;
	TransportMessagingContext jmsTransportContext = null;

	// Reliable config test
	// **********************
	// search and load config for reliable transport
	jmsConfig = ConfigurationFactory.provides("jmsTransport", JmsConfigPath, new RuntimeContext<Configuration>());
	jmsConfig.load();
	// context config for transport
	jmsTransportContext = new TransportMessagingContext(JmsTransportConfigKey, jmsConfig);
	// transport instanciation
	jmsTransport = new JmsTransportMessaging(jmsTransportContext);

   }

   @After
   protected void tearDown() throws Exception {

	// transport close
	jmsTransport.close();
   }

   /**
    * Transports bien instancié
    */
   @Test
   public void testJmsTransport() {

	assertNotNull(jmsTransport);

	try {
	   jmsTransport.createConnection();
	   jmsTransport.close();
	} catch (final TransportMessagingException tme) {
	   LOGGER.error(tme.getMessage(), tme);
	   fail(tme.getMessage());
	}
   }

}
