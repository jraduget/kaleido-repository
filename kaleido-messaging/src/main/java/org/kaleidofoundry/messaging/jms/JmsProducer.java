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

import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_TYPE_FIELD;
import static org.kaleidofoundry.messaging.ClientContextBuilder.MESSAGE_EXPIRATION;
import static org.kaleidofoundry.messaging.ClientContextBuilder.MESSAGE_FULL_DATE_FORMAT;
import static org.kaleidofoundry.messaging.ClientContextBuilder.MESSAGE_PRIORITY;
import static org.kaleidofoundry.messaging.ClientContextBuilder.PRODUCER_DESTINATIONS;
import static org.kaleidofoundry.messaging.ClientContextBuilder.PRODUCER_DESTINATIONS_SEPARATOR;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractMessage;
import org.kaleidofoundry.messaging.AbstractProducer;
import org.kaleidofoundry.messaging.BytesMessage;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.MessageException;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.MessagingException;
import org.kaleidofoundry.messaging.Producer;
import org.kaleidofoundry.messaging.TextMessage;
import org.kaleidofoundry.messaging.TransportException;
import org.kaleidofoundry.messaging.XmlMessage;

/**
 * Jms Messaging Producer
 * 
 * @author jraduget
 */
@Declare(MessagingConstants.JMS_PRODUCER_PLUGIN)
public class JmsProducer extends AbstractProducer {

   private final AbstractJmsTransport<ConnectionFactory, Connection, Destination> transport;

   /**
    * @param context
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public JmsProducer(final RuntimeContext<Producer> context) {
	super(context);
	this.transport = (AbstractJmsTransport) super.transport;
   }

   @Override
   public void send(Collection<Message> messages) throws MessagingException {
	Connection connection = null;
	Session session = null;
	try {
	   connection = transport.createConnection();
	   session = transport.createSession(connection);
	   for (Message message : messages) {
		send(session, message);
	   }
	} finally {
	   transport.closeSession(session);
	   transport.closeConnection(connection);
	}
   }

   @Override
   public void send(final Message message) throws MessagingException {
	Connection connection = null;
	Session session = null;
	try {
	   connection = transport.createConnection();
	   session = transport.createSession(connection);
	   send(session, message);
	} finally {
	   transport.closeSession(session);
	   transport.closeConnection(connection);
	}
   }

   /**
    * @param session
    * @param message
    * @throws MessagingException
    */
   public void send(Session session, final Message message) throws MessagingException {
	javax.jms.Message jmsMessageToSend = null;
	try {
	   switch (message.getType()) {
	   case Text:
		final TextMessage textMsg = (TextMessage) message;
		jmsMessageToSend = session.createTextMessage();
		((javax.jms.TextMessage) jmsMessageToSend).setText(textMsg.getText());
		break;
	   case Xml:
		final XmlMessage xmlMsg = (XmlMessage) message;
		jmsMessageToSend = session.createTextMessage();
		((javax.jms.TextMessage) jmsMessageToSend).setText(xmlMsg.toXml());
		break;
	   case Bytes:
		final BytesMessage binaryMsg = (BytesMessage) message;
		jmsMessageToSend = session.createBytesMessage();
		((javax.jms.BytesMessage) jmsMessageToSend).writeBytes(binaryMsg.getBytes());
		break;
	   case JavaBean:
		final JavaBeanMessage javaBeanMsg = (JavaBeanMessage) message;
		jmsMessageToSend = session.createObjectMessage();
		((javax.jms.ObjectMessage) jmsMessageToSend).setObject(javaBeanMsg.getJavaBean());
		break;
	   default:
		jmsMessageToSend = session.createMessage();
	   }

	   // jms headers
	   jmsMessageToSend.setJMSPriority(getMessagePriority());
	   jmsMessageToSend.setJMSExpiration(getMessageExpiration());

	   // Message type
	   jmsMessageToSend.setStringProperty(MESSAGE_TYPE_FIELD, message.getType().getCode());

	   // correlationID if specified
	   jmsMessageToSend.setJMSCorrelationID(message.getCorrelationId());

	   // Parameters copy
	   if (message.getParameters() != null) {
		for (final String key : message.getParameters().keySet()) {
		   final Object obj = message.getParameters().get(key);

		   if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Byte) {
			jmsMessageToSend.setObjectProperty(key, obj);
		   }

		   if (obj instanceof String[] || obj instanceof Number[] || obj instanceof Boolean[] || obj instanceof Byte[]) {
			jmsMessageToSend.setObjectProperty(key, obj);
		   }

		   if (obj instanceof Date) {
			DateFormat df = new SimpleDateFormat(MESSAGE_FULL_DATE_FORMAT);
			jmsMessageToSend.setStringProperty(key, df.format((Date) obj));
		   }

		   if (obj instanceof Calendar) {
			DateFormat df = new SimpleDateFormat(MESSAGE_FULL_DATE_FORMAT);
			jmsMessageToSend.setStringProperty(key, df.format(((Calendar) obj).getTime()));
		   }

		}
	   }

	} catch (final JMSException jmse) {
	   ProcessedMessagesKO.incrementAndGet();
	   throw new MessageException("messaging.producer.jms.message.build", jmse);
	}

	// Sending Message for each destinations
	boolean error = false;
	for (final Destination dest : getDestinations(session)) {

	   try {
		// create session and send the message to the destination
		final MessageProducer producer = session.createProducer(dest);
		// debug facilities
		debugMessage(message);
		// send it
		producer.send(jmsMessageToSend);
		// memorize the internal provider id in the message
		((AbstractMessage) message).setProviderId(jmsMessageToSend.getJMSMessageID());

	   } catch (final JMSException jmse) {
		error = true;
		throw new TransportException("messaging.producer.jms.message.send", jmse);
	   }
	}

	if (error) {
	   ProcessedMessagesKO.incrementAndGet();
	} else {
	   ProcessedMessagesOK.incrementAndGet();
	}
   }

   /**
    * @param session
    * @return JMS destinations where to send message
    * @throws TransportException
    */
   public Collection<Destination> getDestinations(Session session) throws TransportException {

	final String strDestinations = context.getString(PRODUCER_DESTINATIONS);
	final Collection<String> destinationNames = StringHelper.toList(strDestinations, PRODUCER_DESTINATIONS_SEPARATOR);
	final Collection<Destination> destinations = new ArrayList<Destination>();

	for (final String destName : destinationNames) {
	   destinations.add(transport.getDestination(session, destName));
	}

	return destinations;
   }

   /**
    * @return Message priority (0 lower by default if not specified)
    * @see javax.jms.Message#getJMSPriority()
    */
   public int getMessagePriority() {
	final String strMsgPriority = context.getString(MESSAGE_PRIORITY);

	if (!StringHelper.isEmpty(strMsgPriority)) {
	   try {
		final int msgPriority = Integer.valueOf(strMsgPriority);
		return msgPriority;
	   } catch (final NumberFormatException nfe) {
	   }
	}
	return 0;
   }

   /**
    * @return Message priority (0 never expired by default)
    * @see javax.jms.Message#getJMSExpiration()
    */
   public long getMessageExpiration() {
	final String strExpiration = context.getString(MESSAGE_EXPIRATION);

	if (!StringHelper.isEmpty(strExpiration)) {
	   try {
		final long msgExpiration = Long.valueOf(strExpiration);
		return msgExpiration;
	   } catch (final NumberFormatException nfe) {
	   }
	}
	return 0;
   }

}
