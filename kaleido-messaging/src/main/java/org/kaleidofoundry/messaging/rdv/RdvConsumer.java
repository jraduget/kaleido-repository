/*  
 * Copyright 2008-2014 the original author or authors 
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
package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.ClientContextBuilder.CONSUMER_RECEIVE_TIMEOUT_PROPERTY;
import static org.kaleidofoundry.messaging.ClientContextBuilder.TRANSPORT_REF;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_BODY_BYTES_FIELD;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_BODY_TEXT_FIELD;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_ID_FIELD;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_TYPE_FIELD;
import static org.kaleidofoundry.messaging.ClientContextBuilder.RDV_SUBJECTS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractConsumer;
import org.kaleidofoundry.messaging.AbstractMessage;
import org.kaleidofoundry.messaging.BytesMessage;
import org.kaleidofoundry.messaging.Consumer;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.MessageException;
import org.kaleidofoundry.messaging.MessageTypeEnum;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.MessagingException;
import org.kaleidofoundry.messaging.TextMessage;
import org.kaleidofoundry.messaging.TransportException;
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
 * Consumer for Tibco RDV
 * 
 * @author jraduget
 */
@Declare(MessagingConstants.RDV_CONSUMER_PLUGIN)
public class RdvConsumer extends AbstractConsumer implements Consumer {

   static final Logger LOGGER = LoggerFactory.getLogger(RdvConsumer.class);

   public abstract class TibcorvConsumer extends ConsumerWorker implements TibrvMsgCallback {

	public TibcorvConsumer(int index, String name) {
	   super(index, name);
	}

   }

   // RDV subject list
   private final List<String> rdvSubjectList;

   private final RdvTransport transport;

   private TibrvDispatcher tibrvDispatcher;

   /**
    * @param context
    */
   public RdvConsumer(final RuntimeContext<Consumer> context) throws TransportException {

	super(context);

	// coherence check
	checkContext(context);

	// listener config and transport
	this.rdvSubjectList = context.getStringList(RDV_SUBJECTS);
	this.transport = (RdvTransport) super.transport;
   }

   /**
    * Consistency check
    * 
    * @param context
    * @throws TransportException
    */
   protected void checkContext(final RuntimeContext<Consumer> context) throws TransportException {

	if (StringHelper.isEmpty(context.getString(RDV_SUBJECTS))) throw new EmptyContextParameterException(RDV_SUBJECTS, context);

	if (!(getTransport() instanceof RdvTransport)) { throw new IllegalContextParameterException(TRANSPORT_REF, context.getString(TRANSPORT_REF), context,
		MESSAGING_BUNDLE.getMessage("messaging.consumer.rdv.transport.illegal", context.getString(TRANSPORT_REF))); }
   }

   @Override
   public synchronized void start() throws TransportException {
	super.start();

	if (tibrvDispatcher == null) {
	   // Start the dispatcher so we don't need to worry about it in this thread
	   this.tibrvDispatcher = new TibrvDispatcher("RDVConsumer" + getName(), Tibrv.defaultQueue());
	}
   }

   @Override
   public synchronized void stop() throws TransportException {
	super.stop();
	if (this.tibrvDispatcher != null) {
	   this.tibrvDispatcher.destroy();
	   this.tibrvDispatcher = null;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.AbstractConsumer#newWorker(java.lang.String, int)
    */
   @Override
   protected ConsumerWorker newWorker(String workerName, int workerIndex) throws TransportException {

	return new TibcorvConsumer(workerIndex, workerName) {

	   // handle the communication between the tibcoRDV listener (method TibrvMsgCallback#onMsg) and the consumer receive method
	   private final ThreadLocal<TibrvMsg> threadLocalForMessage = new ThreadLocal<TibrvMsg>();

	   // listeners on reliable transport for subject
	   private Map<String, TibrvListener> reliableListenerBySubject;
	   // listeners on certified transport for subject
	   private Map<String, TibrvCmListener> certifiedListenerBySubject;

	   @Override
	   public void init() throws TransportException {

		// memorize reliable listener list
		this.reliableListenerBySubject = new ConcurrentHashMap<String, TibrvListener>();
		this.certifiedListenerBySubject = new ConcurrentHashMap<String, TibrvCmListener>();

		// Listeners instantiation for each suject
		for (final String rdvSuject : rdvSubjectList) {
		   try {

			// listener on Reliable Transport
			if (RdvTransportTypeEnum.RELIABLE.equals(transport.getType())) {
			   final TibrvListener rdvListener = new TibrvListener(Tibrv.defaultQueue(), this, transport.getRdvTransport(), rdvSuject, null);
			   // memorize listener instance
			   reliableListenerBySubject.put(rdvSuject, rdvListener);
			}

			// Listener on Certified Transport
			if (RdvTransportTypeEnum.CERTIFIED.equals(transport.getType())) {
			   // event queue
			   final TibrvQueue queue = new TibrvQueue();

			   // Create listener for CM messages
			   final TibrvCmListener cmListener = new TibrvCmListener(queue, this, transport.getRdvCmTransport(), rdvSuject, null);

			   // memorize listener instance
			   certifiedListenerBySubject.put(rdvSuject, cmListener);

			   // Set explicit confirmation
			   cmListener.setExplicitConfirm();
			}

			// TODO Listener on Dqueue Transport

		   } catch (final TibrvException rdve) {
			throw new TransportException("messaging.consumer.rdv.create", rdve, rdvSuject);
		   }
		}

		// Queue dispatch TibRv event for reliable transport
		if (RdvTransportTypeEnum.RELIABLE.equals(transport.getType())) {
		   boolean stopped = false;
		   while (!stopped) {
			try {
			   Tibrv.defaultQueue().dispatch();
			} catch (final TibrvException rdve) {
			   throw new TransportException("messaging.consumer.rdv.queue.dispatch", rdve, rdve.getMessage());
			} catch (final InterruptedException ie) {
			   stopped = true;
			}
		   }
		}

		// Queue dispatch TibRv event for certified transport
		if (RdvTransportTypeEnum.CERTIFIED.equals(transport.getType())) {
		   for (final TibrvCmListener listener : certifiedListenerBySubject.values()) {
			final TibrvQueue queue = listener.getQueue();
			new TibrvDispatcher(queue);
		   }
		}
	   }

	   @Override
	   public void receive(MessageWrapper messageWrapper) {

		long messageTimeout = context.getLong(CONSUMER_RECEIVE_TIMEOUT_PROPERTY, -1l);

		if (messageTimeout < 0) {
		   LOGGER.debug("{} waiting incoming messages...", getName());
		} else {
		   LOGGER.debug("{} waiting incoming messages for {}ms...", getName(), messageTimeout);
		}

		try {
		   // wait onMsg call
		   if (messageTimeout < 0) {
			wait();
		   } else {
			wait(messageTimeout);
		   }

		   TibrvMsg tibrvMsg = threadLocalForMessage.get();

		   if (tibrvMsg != null) {
			try {
			   messageWrapper.setProviderObject(tibrvMsg);
			   messageWrapper.setMessage(toMessage(tibrvMsg));
			} finally {
			   threadLocalForMessage.remove();
			}
		   }

		} catch (InterruptedException ite) {
		   messageWrapper.setError(ite);
		} catch (MessagingException mse) {
		   messageWrapper.setError(mse);
		}
	   }

	   @Override
	   public void onMsg(final TibrvListener rdvListener, final TibrvMsg rdvMessage) {
		threadLocalForMessage.set(rdvMessage);
		// notify receive method
		notify();
	   }

	   @Override
	   public void acknowledge(MessageWrapper messageWrapper) throws MessagingException {

		TibrvMsg rdvMessage = (TibrvMsg) messageWrapper.getProviderObject();

		// Only for Certified Transport
		if (RdvTransportTypeEnum.CERTIFIED.equals(transport.getType())) {

		   try {
			// Report we are confirming message
			final long seqno = TibrvCmMsg.getSequence(rdvMessage);

			// If it was not CM message or very first message
			// we'll get seqno=0. Only confirm if seqno > 0.
			if (seqno > 0) {
			   LOGGER.debug("Confirming message with seqno={}", seqno);

			   // get TibrvCmListener for subject
			   final TibrvCmListener cmListener = certifiedListenerBySubject.get(rdvMessage.getSendSubject());

			   // confirm the message
			   cmListener.confirmMsg(rdvMessage);
			}
		   } catch (final TibrvException tibe) {
			throw new MessageException("messaging.consumer.rdv.message.confirm", tibe, tibe.getMessage());
		   }

		   // if message had the reply subject, send the reply
		   try {
			if (rdvMessage.getReplySubject() != null) {
			   final TibrvMsg reply = new TibrvMsg(rdvMessage.getAsBytes());
			   transport.getRdvCmTransport().sendReply(reply, rdvMessage);
			}
		   } catch (final TibrvException tibe) {
			throw new TransportException("messaging.consumer.rdv.message.reply", tibe, tibe.getMessage());
		   }
		}
	   }

	   @Override
	   public void destroy() {
		super.destroy();

		if (reliableListenerBySubject != null) {
		   for (final TibrvListener listener : reliableListenerBySubject.values()) {
			listener.destroy();
		   }
		}

		if (certifiedListenerBySubject != null) {
		   for (final TibrvCmListener listener : certifiedListenerBySubject.values()) {
			listener.destroy();
		   }
		}
	   }

	};
   }

   /**
    * Convert RDV message, to generic message
    * 
    * @param rdvMessage
    * @return
    */
   public Message toMessage(final TibrvMsg rdvMessage) throws MessagingException {
	Message message = null;
	final Map<String, Object> msgParam = new HashMap<String, Object>();

	// For each field of RDV Message, copy field name and value
	for (int i = 0; i < rdvMessage.getNumFields(); i++) {

	   try {
		final TibrvMsgField f = rdvMessage.getFieldByIndex(i);
		msgParam.put(f.name, f.data);

		// Exclude reserved field from parameters ??

	   } catch (final TibrvException tibe) {
		throw new MessageException("messaging.consumer.rdv.message.build", tibe, tibe.getMessage());
	   }
	}

	// Determine main body of the message
	try {

	   final Object msgType = rdvMessage.getField(MESSAGE_TYPE_FIELD).data;
	   final Object messageUuid = rdvMessage.getField(MESSAGE_ID_FIELD).data;

	   // providerId
	   if (messageUuid != null && message instanceof AbstractMessage) {
		((AbstractMessage) message).setProviderId(String.valueOf(messageUuid));
	   }

	   // Text Message
	   if (MessageTypeEnum.Text.getCode().equals(msgType)) {
		final Object messageBody = rdvMessage.get(MESSAGE_BODY_TEXT_FIELD);
		message = new TextMessage((String) messageBody, msgParam);
	   }

	   // XML Message
	   if (MessageTypeEnum.Xml.getCode().equals(msgType)) {
		final Object messageBody = rdvMessage.get(MESSAGE_BODY_TEXT_FIELD);
		message = new XmlMessage((String) messageBody, msgParam);
	   }

	   // Binary Message
	   if (MessageTypeEnum.Bytes.getCode().equals(msgType)) {
		final Object messageBody = rdvMessage.get(MESSAGE_BODY_BYTES_FIELD);
		message = new BytesMessage((byte[]) messageBody, msgParam);
	   }

	   // JavaBean Message
	   if (MessageTypeEnum.JavaBean.getCode().equals(msgType)) {
		final Object messageBody = rdvMessage.get(MESSAGE_BODY_BYTES_FIELD);
		if (messageBody != null) {
		   // Deserialize binary data
		   try {
			final byte[] bytes = (byte[]) messageBody;
			final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			final Object serializedInstance = in.readObject();
			in.close();
			message = new JavaBeanMessage((Serializable) serializedInstance, msgParam);

		   } catch (final ClassNotFoundException cnfe) {
			throw new MessageException("messaging.consumer.rdv.message.deserialize.classnotfound", cnfe, cnfe.getMessage());
		   } catch (final IOException ioe) {
			throw new MessageException("messaging.consumer.rdv.message.deserialize", ioe, ioe.getMessage());
		   }
		}
	   }

	   return message;

	} catch (final TibrvException tibe) {
	   throw new MessageException("messaging.consumer.rdv.message.build", tibe, tibe.getMessage());
	}
   }

   /**
    * @return RDV Subject messages to listen
    */
   public Iterator<String> getRdvSubjects() {
	return rdvSubjectList.iterator();
   }

}