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
package org.kaleidofoundry.core.naming;

import static org.kaleidofoundry.core.naming.NamingContextBuilder.Caching;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.FailoverEnabled;

import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple JNDI naming service usage</h3> Inject {@link NamingService} context and instance using {@link Context} annotation mixing the
 * use of parameters and external configuration (Parameters have priority to the external configuration)
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class NamingServiceJndiSample03 implements NamingServiceJndiSample {

   // Override external configuration for "myNamingService"
   @Context(value = "myNamingService", parameters = { @Parameter(name = FailoverEnabled, value = "false"), @Parameter(name = Caching, value = "none") })
   protected NamingService myNamingService;

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
