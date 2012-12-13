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
package org.kaleidofoundry.messaging.jms.ibm;

import static org.kaleidofoundry.messaging.TransportContextBuilder.MQ_CONNECTION_FACTORY_CHANNEL;
import static org.kaleidofoundry.messaging.TransportContextBuilder.MQ_CONNECTION_FACTORY_HOST;
import static org.kaleidofoundry.messaging.TransportContextBuilder.MQ_CONNECTION_FACTORY_PORT;
import static org.kaleidofoundry.messaging.TransportContextBuilder.MQ_CONNECTION_FACTORY_TRANSPORT_TYPE;
import static org.kaleidofoundry.messaging.TransportContextBuilder.MQ_DESTINATION_PERSISTENT_TYPE;
import static org.kaleidofoundry.messaging.TransportContextBuilder.MQ_DESTINATION_TARGET_CLIENT;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_DESTINATION_TYPE;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.DestinationEnum;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.Transport;
import org.kaleidofoundry.messaging.TransportException;
import org.kaleidofoundry.messaging.jms.AbstractJmsTransport;

import com.ibm.mq.MQC;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQDestination;

/**
 * Websphere MQ transport layer
 * 
 * @author Jerome RADUGET
 */
@Declare(MessagingConstants.WMQ_TRANSPORT_PLUGIN)
public class WebsphereMQTransport extends AbstractJmsTransport<MQConnectionFactory, Connection, MQDestination> {

   private final MQConnectionFactory mqConnectionFactory;

   public WebsphereMQTransport(RuntimeContext<Transport> context) throws TransportException {
	super(context);

	int defaultMQPort = 1414;
	int defaultTransportType = JMSC.MQJMS_TP_CLIENT_MQ_TCPIP;

	try {
	   mqConnectionFactory = new MQConnectionFactory();
	   mqConnectionFactory.setTransportType(context.getInteger(MQ_CONNECTION_FACTORY_TRANSPORT_TYPE, defaultTransportType));
	   mqConnectionFactory.setHostName(context.getString(MQ_CONNECTION_FACTORY_HOST));
	   mqConnectionFactory.setPort(context.getInteger(MQ_CONNECTION_FACTORY_PORT, defaultMQPort));
	   mqConnectionFactory.setChannel(context.getString(MQ_CONNECTION_FACTORY_CHANNEL));
	} catch (JMSException jmse) {
	   throw new TransportException("messaging.transport.connectionFactory.create", jmse, context.getString(MQ_CONNECTION_FACTORY_HOST), context.getString(
		   MQ_CONNECTION_FACTORY_PORT, String.valueOf(defaultMQPort)), context.getString(MQ_CONNECTION_FACTORY_CHANNEL), context.getString(
			   MQ_CONNECTION_FACTORY_TRANSPORT_TYPE, String.valueOf(defaultTransportType)));
	}
   }

   @Override
   protected MQConnectionFactory getConnectionFactory() throws TransportException {
	return mqConnectionFactory;
   }

   @Override
   protected void checkContext() {
	if (StringHelper.isEmpty(context.getString(MQ_CONNECTION_FACTORY_HOST))) { throw new EmptyContextParameterException(MQ_CONNECTION_FACTORY_HOST, context); }
	if (StringHelper.isEmpty(context.getString(MQ_CONNECTION_FACTORY_CHANNEL))) { throw new EmptyContextParameterException(MQ_CONNECTION_FACTORY_CHANNEL, context); }

	if (DestinationEnum.valueOf(context.getString(JMS_DESTINATION_TYPE, DestinationEnum.queue.name())) == null) { throw new IllegalContextParameterException(
		JMS_DESTINATION_TYPE, context.getString(JMS_DESTINATION_TYPE), context, "The value should be: queue|topic"); }

   }

   @Override
   protected MQDestination getDestination(Session session, String name) throws TransportException {
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
	   
	   ((MQDestination) destination).setTargetClient(context.getInteger(MQ_DESTINATION_TARGET_CLIENT, JMSC.MQJMS_CLIENT_NONJMS_MQ));
	   ((MQDestination) destination).setPersistence(context.getInteger(MQ_DESTINATION_PERSISTENT_TYPE, MQC.MQPER_PERSISTENT));
	   
	   return (MQDestination) destination;
	} catch (JMSException jmse) {
	   throw new TransportException("messaging.transport.destination.create", jmse, destinationType, name);
	}
   }

}
