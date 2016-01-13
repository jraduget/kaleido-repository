/*  
 * Copyright 2008-2016 the original author or authors 
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

import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_CERTIFIED_CONFIG_PATH;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_CERTIFIED_CONFIG_TRANSPORT_KEY;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_RELIABLE_CONFIG_PATH;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_RELIABLE_CONFIG_TRANSPORT_KEY;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.messaging.Transport;

import static org.junit.Assert.*;

/**
 * Test Tibco RDV transport
 * 
 * @author jraduget
 */
@Ignore
public class RdvTransportTest {

   private Configuration reliableConfig = null;
   private Configuration certifiedConfig = null;
   
   private RdvTransport rdvReliableTransport;
   private RdvTransport rdvCertifiedTransport;

   @Before
   public void setUp() throws Exception {
	
	RuntimeContext<Transport> reliableTransportContext = null;	
	RuntimeContext<Transport> certifiedTransportContext = null;

	// Reliable config test
	// **********************
	// search and load config for reliable transport
	reliableConfig = ConfigurationFactory.provides("rdvReliableTransport", RDV_RELIABLE_CONFIG_PATH, new RuntimeContext<Configuration>());
	reliableConfig.load();
	// context config for transport
	reliableTransportContext = new RuntimeContext<Transport>(RDV_RELIABLE_CONFIG_TRANSPORT_KEY, Transport.class, reliableConfig);
	// transport instantiation
	rdvReliableTransport = new RdvTransport(reliableTransportContext);

	// Certified config test
	// **********************
	// search and load config for reliable transport
	certifiedConfig = ConfigurationFactory.provides("rdvCertifiedTransport", RDV_CERTIFIED_CONFIG_PATH, new RuntimeContext<Configuration>());
	certifiedConfig.load();
	// context config for transport
	certifiedTransportContext = new RuntimeContext<Transport>(RDV_CERTIFIED_CONFIG_TRANSPORT_KEY, Transport.class, certifiedConfig);
	// transport instantiation
	rdvCertifiedTransport = new RdvTransport(certifiedTransportContext);
   }

   @After
   public void tearDown() throws Exception {
	// transport close
	if (rdvReliableTransport != null) {
	   rdvReliableTransport.close();
	}
	if (rdvCertifiedTransport != null) {
	   rdvCertifiedTransport.close();
	}	
	
	if (reliableConfig != null) {
	   reliableConfig.unload();
	}
	if (certifiedConfig != null) {
	   certifiedConfig.unload();
	}
   }

   @Test
   public void testRdvReliableTransport() {
	assertNotNull(rdvReliableTransport);
   }

   @Test
   public void testRdvCertifiedTransport() {
	assertNotNull(rdvCertifiedTransport);
   }

}
