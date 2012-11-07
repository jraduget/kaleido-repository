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
package org.kaleidofoundry.messaging.jms.amq;

import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_CONNECTION_FACTORY_PASSWORD;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_CONNECTION_FACTORY_URL;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_CONNECTION_FACTORY_USER;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_DESTINATION_TYPE;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.DestinationEnum;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.Transport;
import org.kaleidofoundry.messaging.TransportException;
import org.kaleidofoundry.messaging.jms.AbstractJmsTransport;

/**
 * Apache ActiveMQ transport layer 
 * 
 * @author Jerome RADUGET
 */
@Declare(MessagingConstants.AMQ_TRANSPORT_PLUGIN)
public class AmqTransport extends AbstractJmsTransport<ActiveMQConnectionFactory, ActiveMQConnection, ActiveMQDestination> {

   private final ActiveMQConnectionFactory activeMQConnectionFactory;

   @Task(comment="use context to configure additional connection factory parameters", labels=TaskLabel.Enhancement)
   public AmqTransport(RuntimeContext<Transport> context) throws TransportException {
	super(context);

	if (StringHelper.isEmpty(context.getString(JMS_CONNECTION_FACTORY_USER))) {
	   activeMQConnectionFactory = new ActiveMQConnectionFactory(context.getString(JMS_CONNECTION_FACTORY_URL));
	} else {
	   activeMQConnectionFactory = new ActiveMQConnectionFactory(context.getString(JMS_CONNECTION_FACTORY_URL), context.getString(JMS_CONNECTION_FACTORY_USER),
		   context.getString(JMS_CONNECTION_FACTORY_PASSWORD));
	}

   }

   @Override
   protected ActiveMQConnectionFactory getConnectionFactory() throws TransportException {
	return activeMQConnectionFactory;
   }

   @Override
   protected void checkContext() {

	if (StringHelper.isEmpty(context.getString(JMS_CONNECTION_FACTORY_URL))) {
	   throw new EmptyContextParameterException(JMS_CONNECTION_FACTORY_URL, context);
	} else {
	   try {
		new URI(context.getString(JMS_CONNECTION_FACTORY_URL));
	   } catch (URISyntaxException rse) {
		throw new IllegalContextParameterException(JMS_CONNECTION_FACTORY_URL, context.getString(JMS_CONNECTION_FACTORY_URL), context, "It is not a valid uri");
	   }
	}

	if (DestinationEnum.valueOf(context.getString(JMS_DESTINATION_TYPE, DestinationEnum.queue.name())) == null) { throw new IllegalContextParameterException(
		JMS_DESTINATION_TYPE, context.getString(JMS_DESTINATION_TYPE), context, "The value should be: queue|topic"); }

   }

   @Override
   protected ActiveMQDestination getDestination(Session session, String name) throws TransportException {
	String destinationType = context.getString(JMS_DESTINATION_TYPE, DestinationEnum.queue.name());
	Destination destination;

	try {
	   if (DestinationEnum.queue.name().equalsIgnoreCase(destinationType)) {
		destination = session.createQueue(name);
	   } else if (DestinationEnum.topic.name().equalsIgnoreCase(destinationType)) {
		destination = session.createTopic(name);
	   } else {
		throw new IllegalStateException();
	   }

	   return (ActiveMQDestination) destination;
	} catch (JMSException jmse) {
	   throw new TransportException("messaging.transport.destination.create", jmse, destinationType, name);
	}
   }

}
