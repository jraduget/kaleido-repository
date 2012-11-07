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
package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_BODY_BYTES_FIELD;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_BODY_TEXT_FIELD;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_ID_FIELD;
import static org.kaleidofoundry.messaging.MessagingConstants.MESSAGE_TYPE_FIELD;
import static org.kaleidofoundry.messaging.ClientContextBuilder.TRANSPORT_REF;
import static org.kaleidofoundry.messaging.ClientContextBuilder.RDV_SUBJECTS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
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

import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvMsg;

/**
 * Producer for Tibco RDV
 * 
 * @author Jerome RADUGET
 */
@Declare(MessagingConstants.RDV_PRODUCER_PLUGIN)
public class RdvProducer extends AbstractProducer {

   private final List<String> rdvSubjectList;
   private final RdvTransport transport;
   
   
   /**
    * @param context
    */
   public RdvProducer(final RuntimeContext<Producer> context) throws TransportException {

	super(context);

	checkContext(context);

	this.rdvSubjectList = this.context.getStringList(RDV_SUBJECTS);
	this.transport = (RdvTransport) super.transport;

	// TODO Publisher on Certified and Dqueue Transport
   }

   /**
    * Consistency check
    * 
    * @param context
    * @throws TransportException
    */
   protected void checkContext(final RuntimeContext<Producer> context) throws TransportException {
	if (StringHelper.isEmpty(context.getString(RDV_SUBJECTS))) { throw new EmptyContextParameterException(RDV_SUBJECTS, context); }
	
	if (!(getTransport() instanceof RdvTransport)) { throw new IllegalContextParameterException(TRANSPORT_REF,
		context.getString(TRANSPORT_REF), context, MESSAGING_BUNDLE.getMessage("messaging.consumer.rdv.transport.illegal",
			context.getString(TRANSPORT_REF))); }	
   }


   @Override
   public void send(Collection<Message> messages) throws MessagingException {
	for (Message message : messages) {
	   send(message);
	}	
   }
   
   
   @Override
   public void send(final Message message) throws MessagingException {

	// Listeners instantiation for each suject
	for (final String rdvSuject : rdvSubjectList) {
	   try {

		// Create a message for the query, set the subject and parameters.
		final TibrvMsg rvMessage = new TibrvMsg();
		rvMessage.setSendSubject(rdvSuject);

		// Parameters copy
		if (message.getParameters() != null) {
		   for (final String key : message.getParameters().keySet()) {
			rvMessage.add(key, message.getParameters().get(key));
		   }
		}

		// Message ID and type		
		rvMessage.add(MESSAGE_ID_FIELD, UUID.randomUUID().toString(), TibrvMsg.STRING);
		rvMessage.add(MESSAGE_TYPE_FIELD, message.getType().getCode(), TibrvMsg.STRING);

		// Build RDV Message
		switch (message.getType()) {
		case Text:
		   final TextMessage textMsg = (TextMessage) message;
		   if (textMsg.getText() != null) {
			rvMessage.add(MESSAGE_BODY_TEXT_FIELD, textMsg.getText());
		   } else {
			rvMessage.add(MESSAGE_BODY_TEXT_FIELD, null, TibrvMsg.STRING);
		   }
		   break;
		case Xml:
		   final XmlMessage xmlMsg = (XmlMessage) message;
		   if (xmlMsg.getDocument() != null) {
			rvMessage.add(MESSAGE_BODY_TEXT_FIELD, xmlMsg.toXml());
		   } else {
			rvMessage.add(MESSAGE_BODY_TEXT_FIELD, null, TibrvMsg.XML);
		   }
		   break;
		case Bytes:
		   final BytesMessage binaryMsg = (BytesMessage) message;
		   if (binaryMsg.getBytes() != null) {
			rvMessage.add(MESSAGE_BODY_BYTES_FIELD, binaryMsg.getBytes());
		   } else {
			rvMessage.add(MESSAGE_BODY_BYTES_FIELD, null);
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
			   rvMessage.add(MESSAGE_BODY_BYTES_FIELD, bos.toByteArray());
			   out.close();
			} else {
			   rvMessage.add(MESSAGE_BODY_BYTES_FIELD, null);
			}

		   } catch (final IOException ioe) {
			throw new MessageException("messaging.producer.rdv.message.serialize", ioe, ioe.getMessage());
		   }
		   break;

		default:
		   throw new IllegalStateException(message.getType().getCode());
		}

		// debug facilities
		debugMessage(message);
		
		// Send the request without blocking
		transport.getRdvTransport().send(rvMessage);

	   } catch (final TibrvException rdve) {
		throw new TransportException("messaging.consumer.rdv.create", rdve, rdvSuject);
	   }
	}
   }

   /**
    * @return RDV Subject messages to listen
    */
   public Iterator<String> getRdvSubjects() {
	return rdvSubjectList.iterator();
   }

}