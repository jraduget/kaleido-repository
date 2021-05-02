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

import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * <p>
 * <h3>Simple JNDI naming service usage</h3> Inject {@link NamingService} context and instance manually by coding, using context builder
 * </p>
 * 
 * @author jraduget
 */
public class NamingServiceJndiSample04 implements NamingServiceJndiSample {

   // no context injection, it is create manually with NamingContextBuilder in constructor
   protected NamingService myNamingService;

   public NamingServiceJndiSample04() {

	// create a remote jndi naming service for glassfish V3
	RuntimeContext<NamingService> context = new NamingContextBuilder("myManualNamingService", NamingService.class)
		.withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory").withUrlpkgPrefixes("com.sun.enterprise.naming")
		.withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl").withCorbaORBInitialHost("127.0.0.1")
		.withCorbaORBInitialPort("3700")
		// caching and failover facilities
		.withCaching("all").withCachingStrategy("thread-local").withFailoverEnabled("true").withFailoverWaitBeforeRetry("2000").withFailoverMaxRetry("5")
		.build();

	myNamingService = NamingServiceFactory.provides(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromDatabase(java.lang.String)
    */
   @Override
   public String echoFromDatabase(final String myMessage) throws SQLException {
	return NamingServiceJndiSample01.echoFromDatabase(myNamingService, myMessage);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromJMS(java.lang.String)
    */
   @Override
   public TextMessage echoFromJMS(final String myMessage) throws JMSException {
	return NamingServiceJndiSample01.echoFromJMS(myNamingService, myMessage);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromEJB(java.lang.String)
    */
   @Override
   public String echoFromEJB(final String message) {
	return NamingServiceJndiSample01.echoFromEJB(myNamingService, message);
   }

   @Override
   public NamingService getNamingService() {
	return myNamingService;
   }

}
