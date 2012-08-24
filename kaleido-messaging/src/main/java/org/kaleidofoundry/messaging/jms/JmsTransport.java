/*  
 * Copyright 2008-2012 the original author or authors 
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

import static org.kaleidofoundry.messaging.jms.JmsMessagingConstants.CONNECTION_FACTORY_NAME;
import static org.kaleidofoundry.messaging.jms.JmsMessagingConstants.CONNECTION_FACTORY_NAMING_SERVICE_REF;
import static org.kaleidofoundry.messaging.jms.JmsMessagingConstants.SESSION_ACKNOLEDGE_MODE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.naming.NamingServiceFactory;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.Transport;
import org.kaleidofoundry.messaging.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jms 1.1 MessagingTransport
 * 
 * @author Jerome RADUGET
 */
@Declare(MessagingConstants.JMS_TRANSPORT_PLUGIN)
public class JmsTransport extends AbstractJmsTransport<ConnectionFactory, Connection, Destination> implements Transport {

   static final Logger LOGGER = LoggerFactory.getLogger(JmsTransport.class);

   private final NamingService namingService;   
   
   /*
    * A connection factory is the object a client uses to create a connection to a provider.
    * A connection factory encapsulates a set of connection configuration parameters
    * that has been defined by an administrator. Each connection factory is an instance of the
    * ConnectionFactory, QueueConnectionFactory, or TopicConnectionFactory interface.
    */   
   private final ConnectionFactory connectionFactory;

   /**
    * @param context
    * @throws TransportException
    */
   public JmsTransport(final RuntimeContext<Transport> context) throws TransportException {
	super(context);

	final String connectionFactoryJndiName = context.getString(CONNECTION_FACTORY_NAME);

	// ConnectionFactory creation
	final RuntimeContext<NamingService> namingServiceContext = new RuntimeContext<NamingService>(getNamingServiceRef(), NamingService.class, context);
	namingService = NamingServiceFactory.provides(namingServiceContext);

	connectionFactory = namingService.locate(connectionFactoryJndiName, ConnectionFactory.class);
   }

   /**
    * Consistency check
    * 
    * @throws TransportException
    */
   @Override
   protected void checkContext() {

	if (StringHelper.isEmpty(context.getString(CONNECTION_FACTORY_NAME))) { 
	   throw new EmptyContextParameterException(CONNECTION_FACTORY_NAME, context); 
	}

	final String strAcknowledgeMode = context.getString(SESSION_ACKNOLEDGE_MODE);

	if (!StringHelper.isEmpty(strAcknowledgeMode)) {
	   try {
		Integer.valueOf(strAcknowledgeMode);
	   } catch (final NumberFormatException nfe) {
		throw new IllegalContextParameterException(SESSION_ACKNOLEDGE_MODE, strAcknowledgeMode, context, nfe);
	   }
	}
   }

   @Override
   protected ConnectionFactory getConnectionFactory() throws TransportException {
	return connectionFactory;
   }

   
   @Override
   protected Destination getDestination(Session session, String destName) throws TransportException {
	return namingService.locate(destName, Destination.class);
   }

   /**
    * @return JNDI context name, used to access ConnectionFactory
    */
   public String getNamingServiceRef() {
	return context.getString(CONNECTION_FACTORY_NAMING_SERVICE_REF);
   }

   /**
    * @return the namingService
    */
   public NamingService getNamingService() {
      return namingService;
   }
   
   // only needed for unit test
   RuntimeContext<Transport> getTransport() {	
	return context;
   }

}
