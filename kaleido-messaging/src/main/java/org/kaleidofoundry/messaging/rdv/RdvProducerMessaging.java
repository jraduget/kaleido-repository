package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageBinaryBodyField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageTypeField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageXmlBodyField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.SUFFIX_TransportLocalRef_Property;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Subject;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.Subjects_Separator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

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

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvMsg;

/**
 * ConsumerMessaging pour TibcoRDV
 * 
 * @author Jerome RADUGET
 */
public class RdvProducerMessaging extends AbstractProducerMessaging implements ProducerMessaging {

   static final Logger LOGGER = LoggerFactory.getLogger(RdvProducerMessaging.class);

   private final List<String> rdvSubjectList;
   private RdvTransportMessaging transport;
   private final TransportMessagingContext transportCtx;

   /**
    * @param context
    */
   public RdvProducerMessaging(final ProducerMessagingContext context) throws TransportMessagingException {
	super(context);

	// coherence check
	checkContext(context);

	final String transportLocalRefName = context.getProperty(SUFFIX_TransportLocalRef_Property);
	final String subjects = context.getProperty(PATH_KEY_Subject);

	// publisher config and transport
	this.rdvSubjectList = StringHelper.toList(subjects, Subjects_Separator);
	this.transportCtx = new TransportMessagingContext(transportLocalRefName, context);
	this.transport = RdvTransportMessaging.registeredTransport(transportCtx);

	// TODO Publisher on Certified and Dqueue Transport
   }

   /**
    * check coherence of context
    * 
    * @param context
    * @throws TransportMessagingException
    */
   protected void checkContext(final ProducerMessagingContext context) throws TransportMessagingException {
	if (StringHelper.isEmpty(context.getProperty(PATH_KEY_Subject)))
	   throw new TransportMessagingException("messaging.producer.rdv.subject");

	if (StringHelper.isEmpty(context.getProperty(SUFFIX_TransportLocalRef_Property)))
	   throw new TransportMessagingException("messaging.producer.rdv.transport");

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.ProducerMessaging#sendMessage(org.kaleidofoundry.messaging.Message)
    */
   public void sendMessage(final Message message) throws TransportMessagingException {

	// Listeners instanciation for each suject
	for (final String rdvSuject : rdvSubjectList) {
	   try {

		// Start the dispatcher so we don't need to worry about it in this thread
		new TibrvDispatcher(Tibrv.defaultQueue());

		// Create a message for the query, set the subject and parameters.
		final TibrvMsg rvMessage = new TibrvMsg();
		rvMessage.setSendSubject(rdvSuject);

		// Parameters copy
		if (message.getParameters() != null) {
		   for (final String key : message.getParameters().keySet()) {
			rvMessage.add(key, message.getParameters().get(key));
		   }
		}

		// Message type
		rvMessage.add(MessageTypeField, message.getType().getCode());

		// Build RDV Message
		switch (message.getType()) {
		case Xml:
		   final XmlMessage xmlMsg = (XmlMessage) message;
		   if (xmlMsg.getDocument() != null) {
			rvMessage.add(MessageXmlBodyField, xmlMsg.getDocument().asXML());
		   } else {
			rvMessage.add(MessageXmlBodyField, null, TibrvMsg.XML);
		   }
		   break;
		case Binary:
		   final BinaryMessage binaryMsg = (BinaryMessage) message;
		   if (binaryMsg.getBinary() != null) {
			rvMessage.add(MessageBinaryBodyField, binaryMsg.getBinary());
		   } else {
			rvMessage.add(MessageBinaryBodyField, null);
		   }
		   break;
		case JavaBean:
		   final JavaBeanMessage javaBeanMsg = (JavaBeanMessage) message;

		   try {
			// Serialize bean to a byte array
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ObjectOutputStream out = new ObjectOutputStream(bos);

			if (javaBeanMsg.getJavaBean() != null) {
			   out.writeObject(javaBeanMsg.getJavaBean());
			   rvMessage.add(MessageBinaryBodyField, bos.toByteArray());
			   out.close();
			} else {
			   rvMessage.add(MessageBinaryBodyField, null);
			}

		   } catch (final IOException ioe) {
			throw new TransportMessagingException("messaging.transport.rdv.message.serialize", ioe, ioe
				.getMessage());
		   }
		   break;

		default:
		   throw new IllegalStateException(message.getType().getCode());
		}

		// Send the request without blocking
		transport.getRdvTransport().send(rvMessage);

	   } catch (final TibrvException rdve) {
		LOGGER.error("Can't create RDV listener for subject : " + rdvSuject, rdve);
		throw new TransportMessagingException("messaging.consumer.rdv.create", rdve);
	   }
	}
   }

   /**
    * @return RDV Subject messages to listen
    */
   public List<String> getRdvSubjects() {
	return rdvSubjectList;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.AbstractProducerMessaging#getTransport()
    */
   @Override
   public RdvTransportMessaging getTransport() {
	return transport;
   }

   /**
    * @param transport
    */
   protected void setTransport(final RdvTransportMessaging transport) {
	this.transport = transport;
   }

}