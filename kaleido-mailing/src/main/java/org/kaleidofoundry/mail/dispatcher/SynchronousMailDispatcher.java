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
package org.kaleidofoundry.mail.dispatcher;

import static org.kaleidofoundry.mail.MailConstants.SynchronousMailDispatcherPluginName;
import static org.kaleidofoundry.mail.dispatcher.MailDispatcherContextBuilder.MAILSESSION_SERVICE_REF;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.mail.MailAttachment;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.session.MailSessionFactory;
import org.kaleidofoundry.mail.session.MailSessionService;

/**
 * Mail dispatcher using a mail {@link Session}
 * 
 * @author jraduget
 */
@Declare(SynchronousMailDispatcherPluginName)
public class SynchronousMailDispatcher implements MailDispatcher {

   final RuntimeContext<MailDispatcher> context;

   private final MailSessionService sessionService;

   public SynchronousMailDispatcher(final RuntimeContext<MailDispatcher> context) {
	String mailServiceContextName = context.getString(MAILSESSION_SERVICE_REF);
	if (StringHelper.isEmpty(mailServiceContextName)) { throw new EmptyContextParameterException(MAILSESSION_SERVICE_REF, context); }
	this.context = context;
	this.sessionService = MailSessionFactory.provides(new RuntimeContext<MailSessionService>(mailServiceContextName, MailSessionService.class, context));
   }

   @Override
   public void send(final MailMessage message) throws MailException {

	final Session session = sessionService.createSession();
	final MimeMessage mimeMessage = new MimeMessage(session);
	MimeBodyPart messageBodyPart = new MimeBodyPart();
	final Multipart multipart = new MimeMultipart();

	Iterator<String> iterator = null;
	List<String> incorrectAddresses = new ArrayList<String>();

	boolean hasAdress = false;

	if (StringHelper.isEmpty(message.getFromAddress())) { throw MailException.emptyFromMailAddressException(); }

	try {
	   // From
	   try {
		mimeMessage.setFrom(new InternetAddress(message.getFromAddress()));
	   } catch (final AddressException ade) {
		incorrectAddresses.add(message.getFromAddress());
	   }
	   mimeMessage.setSubject(message.getSubject(), message.getBodyCharSet());

	   // mime type and charset
	   messageBodyPart.setContent(message.getBody(), message.getBodyContentType() + "; charset=" + message.getBodyCharSet());

	   // priority
	   mimeMessage.addHeaderLine("X-Priority: " + String.valueOf(message.getPriority()));

	   // first part of the message as multipart
	   multipart.addBodyPart(messageBodyPart);

	   // To Addresses
	   if (message.getToAddresses() != null) {
		for (iterator = message.getToAddresses().iterator(); iterator.hasNext();) {
		   try {
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(iterator.next()));
			hasAdress = true;
		   } catch (final Throwable ae) {
			incorrectAddresses.add(message.getFromAddress());
		   }
		}
	   }
	   // CC Addresses
	   if (message.getCcAddresses() != null) {
		for (iterator = message.getCcAddresses().iterator(); iterator.hasNext();) {
		   try {
			mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(iterator.next()));
			hasAdress = true;
		   } catch (final Throwable ae) {
			incorrectAddresses.add(message.getFromAddress());
		   }
		}
	   }
	   // BCC Addresses
	   if (message.getCciAddresses() != null) {
		for (iterator = message.getCciAddresses().iterator(); iterator.hasNext();) {
		   try {
			mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(iterator.next()));
			hasAdress = true;
		   } catch (final Throwable ae) {
			incorrectAddresses.add(message.getFromAddress());
		   }
		}
	   }

	   // Attachments
	   if (message.getAttachmentNames() != null) {

		for (final String attachName : message.getAttachmentNames()) {

		   final MailAttachment mailAttach = message.getAttachment(attachName);

		   messageBodyPart = new MimeBodyPart();
		   messageBodyPart.setFileName(attachName);
		   messageBodyPart.setDataHandler(new DataHandler(new javax.activation.DataSource() {

			@Override
			public String getContentType() {
			   return mailAttach.getContentType();
			}

			@Override
			public InputStream getInputStream() throws IOException {
			   return mailAttach.getContentInputStream();
			}

			@Override
			public String getName() {
			   return mailAttach.getName();
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
			   return null;
			}

		   }));

		   multipart.addBodyPart(messageBodyPart);
		}

	   }

	   // add content to the envelope
	   mimeMessage.setContent(multipart);

	} catch (MessagingException me) {
	   throw new MailDispatcherException("mail.service.message.error", me);
	}
	// send the message
	if (hasAdress == true) {
	   try {
		Transport.send(mimeMessage);
	   } catch (MessagingException me) {
		throw new MailDispatcherException("mail.service.send.error", me);
	   }
	} else {
	   throw MailException.emptyToMailAddressException();
	}

	// propagate the error if one of the addresses is invalid
	if (!incorrectAddresses.isEmpty()) { throw MailException.invalidMailAddressException(incorrectAddresses); }

   }

}
