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
package org.kaleidofoundry.messaging.rdv;

import static junit.framework.Assert.assertNotNull;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvCertifiedConfigPath;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvReliabledConfigPath;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvTransportCertifiedConfigKey;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvTransportReliableConfigKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.messaging.TransportMessagingContext;

/**
 * Test TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public class TestRdvTransportMessaging {

   private RdvTransportMessaging rdvReliableTransport;
   private RdvTransportMessaging rdvCertifiedTransport;

   @Before
   protected void setUp() throws Exception {

	Configuration reliableConfig = null;
	TransportMessagingContext reliableTransportContext = null;

	Configuration certifiedConfig = null;
	TransportMessagingContext certifiedTransportContext = null;

	// Reliable config test
	// **********************
	// search and load config for reliable transport
	reliableConfig = ConfigurationFactory.provides("rdvReliableTransport", RdvReliabledConfigPath, new RuntimeContext<Configuration>());
	reliableConfig.load();
	// context config for transport
	reliableTransportContext = new TransportMessagingContext(RdvTransportReliableConfigKey, reliableConfig);
	// transport instanciation
	rdvReliableTransport = new RdvTransportMessaging(reliableTransportContext);

	// Certified config test
	// **********************
	// search and load config for reliable transport
	certifiedConfig = ConfigurationFactory.provides("rdvCertifiedTransport", RdvCertifiedConfigPath, new RuntimeContext<Configuration>());
	certifiedConfig.load();
	// context config for transport
	certifiedTransportContext = new TransportMessagingContext(RdvTransportCertifiedConfigKey, certifiedConfig);
	// transport instanciation
	rdvCertifiedTransport = new RdvTransportMessaging(certifiedTransportContext);
   }

   @After
   protected void tearDown() throws Exception {

	// transport close
	rdvReliableTransport.close();
	rdvCertifiedTransport.close();
   }

   /**
    * Transports bien instancié
    */
   @Test
   public void testRdvReliableTransport() {

	assertNotNull(rdvReliableTransport);
   }

   /**
    * Transports bien instancié
    */
   @Test
   public void testRdvCertifiedTransport() {

	assertNotNull(rdvCertifiedTransport);
   }

}
