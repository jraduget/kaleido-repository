/*
 *  Copyright 2008-2010 the original author or authors.
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
 * @author Jerome RADUGET
 */
public class NamingServiceJndiSample04 implements NamingServiceJndiSample {

   // no context injection, it is create manually with NamingContextBuilder in constructor
   protected NamingService namingService;

   public NamingServiceJndiSample04() {

	// create a remote jndi naming service for glassfish V3 
	RuntimeContext<NamingService> context = 
	   new NamingContextBuilder("myManualNamingCtx", NamingService.class)
		.withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory")
		.withUrlpkgPrefixes("com.sun.enterprise.naming")
		.withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl")
		.withCorbaORBInitialHost("127.0.0.1")
		.withCorbaORBInitialPort("3700")
		// caching and failover facilities
		.withCaching("all")
		.withCachingStrategy("thread-local")
		.withFailoverEnabled("true")
		.withFailoverWaitBeforeRetry("2000")
		.withFailoverMaxRetry("5")
		.build();
	 
	namingService = NamingServiceFactory.provides(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromDatabase(java.lang.String)
    */
   @Override
   public String echoFromDatabase(String myMessage) throws SQLException {
	return NamingServiceJndiSample01.echoFromDatabase(namingService, myMessage);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromJMS(java.lang.String)
    */
   @Override
   public TextMessage echoFromJMS(String myMessage) throws JMSException {
	return NamingServiceJndiSample01.echoFromJMS(namingService, myMessage);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromEJB(java.lang.String)
    */
   @Override
   public String echoFromEJB(String message) {
	return NamingServiceJndiSample01.echoFromEJB(namingService, message);
   }
   
   @Override
   public NamingService getNamingService() {
	return namingService;
   }

}
