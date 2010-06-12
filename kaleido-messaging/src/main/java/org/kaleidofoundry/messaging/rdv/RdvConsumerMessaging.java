package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageBinaryBodyField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageTypeField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.MessageXmlBodyField;
import static org.kaleidofoundry.messaging.TransportMessagingConstants.SUFFIX_TransportLocalRef_Property;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Subject;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.Subjects_Separator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractConsumerMessaging;
import org.kaleidofoundry.messaging.BinaryMessage;
import org.kaleidofoundry.messaging.ConsumerMessaging;
import org.kaleidofoundry.messaging.ConsumerMessagingContext;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.MessageTypeEnum;
import org.kaleidofoundry.messaging.TransportMessagingContext;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.kaleidofoundry.messaging.XmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvCmListener;
import com.tibco.tibrv.TibrvCmMsg;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvMsgField;
import com.tibco.tibrv.TibrvQueue;

/**
 * ConsumerMessaging pour TibcoRDV
 * 
 * @author Jerome RADUGET
 */
public abstract class RdvConsumerMessaging extends AbstractConsumerMessaging implements ConsumerMessaging, TibrvMsgCallback {

   static final Logger LOGGER = LoggerFactory.getLogger(RdvConsumerMessaging.class);

   private final List<String> rdvSubjectList;
   private RdvTransportMessaging transport;
   private TransportMessagingContext transportCtx;

   private final Map<String, TibrvListener> rdvListenerList; // listeners on reliable transport for subject
   private final Map<String, TibrvCmListener> rdvCmListenerList; // listeners on certified transport for subject

   /**
    * @param context
    */
   public RdvConsumerMessaging(final ConsumerMessagingContext context) throws TransportMessagingException {
	super(context);

	// coherence check
	checkContext(context);

	final String transportLocalRefName = context.getProperty(SUFFIX_TransportLocalRef_Property);
	final String subjects = context.getProperty(PATH_KEY_Subject);

	// listener config and transport
	this.rdvSubjectList = StringHelper.toList(subjects, Subjects_Separator);
	this.transportCtx = new TransportMessagingContext(transportLocalRefName, context);
	this.transport = RdvTransportMessaging.registeredTransport(transportCtx);

	// memorize reliable listener list
	this.rdvListenerList = new HashMap<String, TibrvListener>();
	this.rdvCmListenerList = new HashMap<String, TibrvCmListener>();

	// Listeners instanciation for each suject
	for (final String rdvSuject : rdvSubjectList) {
	   try {

		// listener on Reliable Transport
		// ********************************
		if (RdvTransportTypeEnum.RELIABLE.equals(transport.getType())) {
		   final TibrvListener rdvListener = new TibrvListener(Tibrv.defaultQueue(), this, transport.getRdvTransport(), rdvSuject, null);
		   // memorize listener instance
		   rdvListenerList.put(rdvSuject, rdvListener);
		}

		// Listener on Certified Transport
		// *********************************
		if (RdvTransportTypeEnum.CERTIFIED.equals(transport.getType())) {
		   // event queue
		   final TibrvQueue queue = new TibrvQueue();

		   // Create listener for CM messages
		   final TibrvCmListener cmListener = new TibrvCmListener(queue, this, transport.getRdvCmTransport(), rdvSuject, null);

		   // memorize listener instance
		   rdvCmListenerList.put(rdvSuject, cmListener);

		   // Set explicit confirmation
		   cmListener.setExplicitConfirm();
		}

		// TODO Listener on Dqueue Transport
		// *********************************
		// ...

	   } catch (final TibrvException rdve) {
		LOGGER.error("Can't create RDV listener for subject : " + rdvSuject, rdve);
		throw new TransportMessagingException("messaging.consumer.rdv.create", rdve);
	   }
	}

	// Queue dispatch TibRv event for reliable transport
	if (RdvTransportTypeEnum.RELIABLE.equals(transport.getType())) {
	   boolean stoped = false;
	   while (!stoped) {
		try {
		   Tibrv.defaultQueue().dispatch();
		} catch (final TibrvException rdve) {
		   throw new TransportMessagingException("messaging.consumer.rdv.queue.dispatch", rdve, rdve.getMessage());
		} catch (final InterruptedException ie) {
		   stoped = true;
		}
	   }
	}

	// Queue dispatch TibRv event for certified transport
	if (RdvTransportTypeEnum.CERTIFIED.equals(transport.getType())) {
	   for (final TibrvCmListener listener : rdvCmListenerList.values()) {
		final TibrvQueue queue = listener.getQueue();
		new TibrvDispatcher(queue);
	   }
	}
   }

   /**
    * Check cohérence
    * 
    * @param context
    * @throws TransportMessagingException
    */
   protected void checkContext(final ConsumerMessagingContext context) throws TransportMessagingException {
	if (StringHelper.isEmpty(context.getProperty(PATH_KEY_Subject)))
	   throw new TransportMessagingException("messaging.consumer.rdv.subject");

	if (StringHelper.isEmpty(context.getProperty(SUFFIX_TransportLocalRef_Property)))
	   throw new TransportMessagingException("messaging.consumer.rdv.transport");

   }

   /**
    * Convert RDV message, to generic message
    * 
    * @param rdvMessage
    * @return
    */
   public Message toMessage(final TibrvMsg rdvMessage) throws TransportMessagingException {
	Message message = null;
	final Map<String, Object> msgParam = new HashMap<String, Object>(); // 

	// For each field of RDV Message, copy field name and value
	for (int i = 0; i < rdvMessage.getNumFields(); i++) {

	   try {
		final TibrvMsgField f = rdvMessage.getFieldByIndex(i);
		msgParam.put(f.name, f.data);

		// Exclude reserved field from parameters ??

	   } catch (final TibrvException tibe) {
		throw new TransportMessagingException("messaging.transport.rdv.message.build", tibe, tibe.getMessage());
	   }
	}

	// Determine main body of the message
	try {

	   final Object msgType = rdvMessage.getField(MessageTypeField).data;

	   // XML Message
	   if (MessageTypeEnum.Xml.getCode().equals(msgType)) {
		final XmlMessage xmlMessage = new XmlMessage(msgParam);
		final Object messageBody = rdvMessage.get(MessageXmlBodyField);
		if (messageBody != null && messageBody instanceof String) {
		   // byte[] bs = ((String) messageBody).getBytes();
		   // String xmlstr = new String(bs);
		   xmlMessage.setDocument(DocumentHelper.parseText((String) messageBody));
		}
		message = xmlMessage;
	   }

	   // Binary Message
	   if (MessageTypeEnum.Binary.getCode().equals(msgType)) {
		final BinaryMessage binaryMessage = new BinaryMessage(msgParam);
		final Object messageBody = rdvMessage.get(MessageBinaryBodyField);
		if (messageBody != null) {
		   final byte[] bytes = (byte[]) messageBody;
		   binaryMessage.setBinary(bytes);
		}
		message = binaryMessage;
	   }

	   // JavaBean Message
	   if (MessageTypeEnum.JavaBean.getCode().equals(msgType)) {
		final JavaBeanMessage javaBeanMessage = new JavaBeanMessage(msgParam);
		final Object messageBody = rdvMessage.get(MessageBinaryBodyField);

		if (messageBody != null) {
		   // Deserialize binary data
		   try {
			final byte[] bytes = (byte[]) messageBody;
			final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			final Object serializedInstance = in.readObject();
			if (serializedInstance instanceof Serializable) {
			   javaBeanMessage.setJavaBean((Serializable) serializedInstance);
			}
			in.close();
			message = javaBeanMessage;

		   } catch (final ClassNotFoundException cnfe) {
			throw new TransportMessagingException("messaging.transport.rdv.message.deserialize.classnotfound", cnfe, cnfe
				.getMessage());
		   } catch (final IOException ioe) {
			throw new TransportMessagingException("messaging.transport.rdv.message.deserialize", ioe, ioe.getMessage());
		   }
		}
	   }

	   return message;

	} catch (final TibrvException tibe) {
	   throw new TransportMessagingException("messaging.transport.rdv.message.build", tibe, tibe.getMessage());
	} catch (final DocumentException doce) {
	   throw new TransportMessagingException("messaging.transport.rdv.message.xml.parse", doce, doce.getMessage());
	}
   }

   /**
    * @param rdvListener
    * @param rdvMessage
    */
   public void onMsg(final TibrvListener rdvListener, final TibrvMsg rdvMessage) {
	try {
	   final Message message = toMessage(rdvMessage);

	   onMessageReceived(message);

	   // Only for Certified Transport
	   if (RdvTransportTypeEnum.CERTIFIED.equals(transport.getType())) {

		try {
		   // Report we are confirming message
		   final long seqno = TibrvCmMsg.getSequence(rdvMessage);

		   // If it was not CM message or very first message
		   // we'll get seqno=0. Only confirm if seqno > 0.
		   if (seqno > 0) {
			System.out.println("Confirming message with seqno=" + seqno);
			System.out.flush();

			// get TibrvCmListener for subject
			final TibrvCmListener cmListener = rdvCmListenerList.get(rdvMessage.getSendSubject());

			// confirm the message
			cmListener.confirmMsg(rdvMessage);
		   }
		} catch (final TibrvException tibe) {
		   throw new TransportMessagingException("messaging.transport.rdv.message.confirm", tibe, tibe.getMessage());
		}

		// if message had the reply subject, send the reply
		try {
		   if (rdvMessage.getReplySubject() != null) {
			final TibrvMsg reply = new TibrvMsg(rdvMessage.getAsBytes());
			transport.getRdvCmTransport().sendReply(reply, rdvMessage);
		   }
		} catch (final TibrvException tibe) {
		   throw new TransportMessagingException("messaging.transport.rdv.message.reply", tibe, tibe.getMessage());
		}
	   }

	} catch (final TransportMessagingException tme) {
	   onMessageReceivedError(tme);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.ConsumerMessaging#stop()
    */
   public void stop() throws TransportMessagingException {

	if (rdvListenerList != null) {
	   for (final TibrvListener listener : rdvListenerList.values()) {
		listener.destroy();
	   }
	}

	if (rdvCmListenerList != null) {
	   for (final TibrvCmListener listener : rdvCmListenerList.values()) {
		listener.destroy();
	   }
	}

	// don't close transport, transport is injected to listener
	// transport instanciation and free must be done out
   }

   /**
    * @return RDV Subject messages to listen
    */
   public List<String> getRdvSubjects() {
	return rdvSubjectList;
   }

   @Override
   public RdvTransportMessaging getTransport() {
	return transport;
   }

   protected void setTransport(final RdvTransportMessaging transport) {
	this.transport = transport;
   }

   public TransportMessagingContext getTransportCtx() {
	return transportCtx;
   }

   protected void setTransportCtx(final TransportMessagingContext transportCtx) {
	this.transportCtx = transportCtx;
   }

   public Collection<TibrvListener> getRdvListenerList() {
	return rdvListenerList.values();
   }

   public Collection<TibrvCmListener> getRdvCmListenerList() {
	return rdvCmListenerList.values();
   }

}