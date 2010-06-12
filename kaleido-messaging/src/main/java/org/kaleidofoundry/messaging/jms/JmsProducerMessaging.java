package org.kaleidofoundry.messaging.jms;

import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageBinaryBodyField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageTypeField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageXmlBodyField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.SUFFIX_TransportLocalRef_Property;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.Destinations_Separator;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.MESSAGE_FULL_DATE_FORMAT;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_DestinationJndiContext;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_DestinationJndiName;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_Destinations;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_MessageExpiration;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_MessagePriority;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractProducerMessaging;
import org.kaleidofoundry.messaging.BinaryMessage;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.ProducerMessaging;
import org.kaleidofoundry.messaging.ProducerMessagingContext;
import org.kaleidofoundry.messaging.TransportMessagingContext;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.kaleidofoundry.messaging.XmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jms Messaging Producer
 * 
 * @author Jerome RADUGET
 */
public class JmsProducerMessaging extends AbstractProducerMessaging implements ProducerMessaging {

   /** Default Logger */
   static final Logger LOGGER = LoggerFactory.getLogger(JmsProducerMessaging.class);

   private final JmsTransportMessaging transport;
   private final TransportMessagingContext transportCtx;

   /**
    * @param context
    */
   public JmsProducerMessaging(final ProducerMessagingContext context) throws TransportMessagingException {
	super(context);

	// coherence check
	checkContext(context);

	final String transportLocalRefName = context.getProperty(SUFFIX_TransportLocalRef_Property);

	// publisher config and transport
	transportCtx = new TransportMessagingContext(transportLocalRefName, context);
	transport = JmsTransportMessaging.registeredTransport(transportCtx);
   }

   /**
    * check context coherence
    * 
    * @param context
    * @throws TransportMessagingException
    */
   protected void checkContext(final ProducerMessagingContext context) throws TransportMessagingException {
	if (StringHelper.isEmpty(context.getProperty(SUFFIX_TransportLocalRef_Property))) { throw new TransportMessagingException(
		"messaging.producer.jms.transport"); }

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.MessagingSender#sendMessage(org.kaleidofoundry.messaging.Message)
    */
   public void sendMessage(final Message message) throws TransportMessagingException {
	MapMessage jmsMessageToSend = null;
	Session session = null;

	// TODO don't create session at each call - keep jms session in user thread local session
	session = transport.createSession();

	// Build Message
	// ************************************
	try {
	   // message instance
	   jmsMessageToSend = session.createMapMessage();
	   jmsMessageToSend.setJMSPriority(getMessagePriority());
	   jmsMessageToSend.setJMSExpiration(getMessageExpiration());

	   switch (message.getType()) {
	   case Xml:
		final XmlMessage xmlMsg = (XmlMessage) message;
		if (xmlMsg.getDocument() != null) {
		   jmsMessageToSend.setString(MessageXmlBodyField, xmlMsg.getDocument().asXML());
		}

		break;
	   case Binary:
		final BinaryMessage binaryMsg = (BinaryMessage) message;
		if (binaryMsg.getBinary() != null) {
		   jmsMessageToSend.setBytes(MessageBinaryBodyField, binaryMsg.getBinary());
		}

		break;
	   case JavaBean:
		final JavaBeanMessage javaBeanMsg = (JavaBeanMessage) message;
		// Serialize bean to a byte array
		try {
		   final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		   final ObjectOutputStream out = new ObjectOutputStream(bos);

		   if (javaBeanMsg.getJavaBean() != null) {
			out.writeObject(javaBeanMsg.getJavaBean());
			jmsMessageToSend.setBytes(MessageBinaryBodyField, bos.toByteArray());
			out.close();
		   } else {
			jmsMessageToSend.setBytes(MessageBinaryBodyField, null);
		   }

		} catch (final IOException ioe) {
		   throw new TransportMessagingException("messaging.producer.jms.message.serialize", ioe, ioe.getMessage());
		}

		break;
	   default:
		throw new IllegalStateException(message.getType().getCode());
	   }

	   // Message type
	   jmsMessageToSend.setString(MessageTypeField, message.getType().getCode());

	   // Parameters copy
	   if (message.getParameters() != null) {
		for (final String key : message.getParameters().keySet()) {
		   final Object obj = message.getParameters().get(key);

		   if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Byte) {
			jmsMessageToSend.setObject(key, obj);
		   }

		   if (obj instanceof String[] || obj instanceof Number[] || obj instanceof Boolean[]
			   || obj instanceof Byte[]) {
			jmsMessageToSend.setObject(key, obj);
		   }

		   if (obj instanceof Date) {
			DateFormat df = new SimpleDateFormat(MESSAGE_FULL_DATE_FORMAT);
			jmsMessageToSend.setString(key, df.format((Date) obj));
		   }

		   if (obj instanceof Calendar) {
			DateFormat df = new SimpleDateFormat(MESSAGE_FULL_DATE_FORMAT);
			jmsMessageToSend.setString(key, df.format(((Calendar) obj).getTime()));
		   }

		}
	   }

	} catch (final JMSException jmse) {
	   throw new TransportMessagingException("messaging.producer.jms.message.build", jmse);
	}

	// Sending Messagefor each destinations
	// ******************************************
	for (final Destination dest : getDestinations()) {

	   try {
		final MessageProducer producer = session.createProducer(dest);
		producer.send(jmsMessageToSend);
	   } catch (final JMSException jmse) {
		throw new TransportMessagingException("messaging.producer.jms.message.send", jmse);
	   }
	}
   }

   /**
    * @return Jms Destinations to send message
    * @throws TransportMessagingException if jndi connection to destination failed
    */
   public Collection<Destination> getDestinations() throws TransportMessagingException {
	final ProducerMessagingContext ctx = getContext();
	final String strDestinations = ctx.getProperty(PATH_KEY_Destinations);
	final Collection<String> destinationNames = StringHelper.toList(strDestinations, Destinations_Separator);
	final Collection<Destination> destinations = new ArrayList<Destination>();

	for (final String destName : destinationNames) {

	   final String jndiName = ctx.getProperty(StringHelper.replaceAll(PATH_KEY_DestinationJndiName, "?", destName));
	   final String jndiContextName = ctx.getProperty(StringHelper.replaceAll(PATH_KEY_DestinationJndiContext, "?",
		   destName));
	   final JndiContext jndiContext = new JndiContext(jndiContextName, getContext());

	   try {
		final JndiResourceLocator<Destination> jndiLocator = new JndiResourceLocator<Destination>(jndiContext);
		final Destination dest = jndiLocator.lookup(jndiName, Destination.class);
		destinations.add(dest);
	   } catch (final JndiResourceException jndie) {
		throw new TransportMessagingException("messaging.producer.jms.destination.lookup", jndie, jndiName,
			destName);
	   }
	}

	return destinations;
   }

   /**
    * @return Message priority (0 lower by default if not specified)
    * @see javax.jms.Message#getJMSPriority()
    */
   public int getMessagePriority() {
	final String strMsgPriority = getContext().getProperty(PATH_KEY_MessagePriority);

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
	final String strExpiration = getContext().getProperty(PATH_KEY_MessageExpiration);

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
