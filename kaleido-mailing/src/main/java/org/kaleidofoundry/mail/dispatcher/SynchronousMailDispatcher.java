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
import java.util.ArrayList;
import java.util.Date;
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
import javax.mail.util.ByteArrayDataSource;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.mail.InvalidMailAddressException;
import org.kaleidofoundry.mail.MailAttachment;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailingDispatcherReport;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.kaleidofoundry.mail.session.MailSessionFactory;
import org.kaleidofoundry.mail.session.MailSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mail dispatcher using a mail {@link Session}
 * 
 * @author jraduget
 */
@Declare(SynchronousMailDispatcherPluginName)
public class SynchronousMailDispatcher implements MailDispatcher {

   private static Logger LOGGER = LoggerFactory.getLogger(SynchronousMailDispatcher.class);

   protected final RuntimeContext<MailDispatcher> context;

   protected final MailSessionService mailSessionService;

   public SynchronousMailDispatcher(final RuntimeContext<MailDispatcher> context) {
	String mailServiceContextName = context.getString(MAILSESSION_SERVICE_REF);
	if (StringHelper.isEmpty(mailServiceContextName)) { throw new EmptyContextParameterException(MAILSESSION_SERVICE_REF, context); }
	this.context = context;
	this.mailSessionService = MailSessionFactory.provides(new RuntimeContext<MailSessionService>(mailServiceContextName, MailSessionService.class, context));
   }

   public static void send(MailMessage message, Session session) throws InvalidMailAddressException, MailDispatcherException {

	final MimeMessage mimeMessage = new MimeMessage(session);
	MimeBodyPart messageBodyPart = new MimeBodyPart();
	final Multipart multipart = new MimeMultipart();

	Iterator<String> iterator = null;

	boolean hasAdress = false;
	List<String> incorrectAddresses = new ArrayList<String>();

	try {

	   // From
	   if (StringHelper.isEmpty(message.getFromAddress())) {
		// skip the send
		throw InvalidMailAddressException.emptyFromMailAddressException();
	   }

	   try {
		mimeMessage.setFrom(new InternetAddress(message.getFromAddress(), true));
	   } catch (final AddressException ade) {
		// skip the send
		throw InvalidMailAddressException.invalidMailAddressException(message.getFromAddress());
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
		   String mail = iterator.next();
		   try {
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(mail, true));
			hasAdress = true;
		   } catch (final Throwable ae) {
			incorrectAddresses.add(mail);
		   }
		}
	   }
	   // CC Addresses
	   if (message.getCcAddresses() != null) {
		for (iterator = message.getCcAddresses().iterator(); iterator.hasNext();) {
		   String mail = iterator.next();
		   try {
			mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(mail, true));
			hasAdress = true;
		   } catch (final Throwable ae) {
			incorrectAddresses.add(mail);
		   }
		}
	   }
	   // BCC Addresses
	   if (message.getBccAddresses() != null) {
		for (iterator = message.getBccAddresses().iterator(); iterator.hasNext();) {
		   String mail = iterator.next();
		   try {
			mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(mail, true));
			hasAdress = true;
		   } catch (final Throwable ae) {
			incorrectAddresses.add(mail);
		   }
		}
	   }

	   // Attachments
	   if (hasAdress && message.getAttachmentNames() != null) {

		for (final String attachName : message.getAttachmentNames()) {

		   try {
			final MailAttachment mailAttach = message.getAttachment(attachName);
			StringBuilder contentType = new StringBuilder();
			if (mailAttach.getContentType() != null) {
			   contentType.append(mailAttach.getContentType());
			}
			if (mailAttach.getContentCharset() != null) {
			   if (contentType.length() > 0) {
				contentType.append("; ");
			   }
			   contentType.append("charset=").append(mailAttach.getContentCharset());
			}

			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setFileName(attachName);
			messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(mailAttach.getInputStream(), contentType.length() > 0 ? contentType
				.toString() : null)));
			multipart.addBodyPart(messageBodyPart);
		   } catch (IOException ioe) {
			// skip the send
			throw MailDispatcherException.ioReadMailAttachmentException(attachName, ioe);
		   }
		}

	   }

	   // add content to the envelope
	   mimeMessage.setContent(multipart);
	} catch (MessagingException me) {
	   // technical error while building the mail message
	   throw new MailDispatcherException("mail.service.message.build.error", me);
	}

	// send the message
	if (hasAdress == true) {
	   try {
		long currentTimeMillis = System.currentTimeMillis();
		LOGGER.debug("mail sending starting at {}", new Date().toString());
		Transport.send(mimeMessage);
		LOGGER.debug("mail sending ended in an elapsed time of {} ms", System.currentTimeMillis() - currentTimeMillis);

	   } catch (MessagingException me) {
		// error while sending the mail message
		throw new MailDispatcherException("mail.service.message.send.error", me);
	   }
	}

	// no valid address & no incorrect address
	if (!hasAdress && incorrectAddresses.isEmpty()) {
	   throw InvalidMailAddressException.emptyToMailAddressException();
	}
	// valid address & incorrect address
	else if (!incorrectAddresses.isEmpty()) { throw InvalidMailAddressException.invalidMailAddressException(incorrectAddresses
		.toArray(new String[incorrectAddresses.size()])); }

   }

   @Override
   public void send(final MailMessage... messages) throws MailSessionException, MailDispatcherException {

	final MailingDispatcherReport report = new MailingDispatcherReport();
	final Session session = mailSessionService.createSession();

	for (MailMessage message : messages) {
	   try {
		send(message, session);
	   } catch (MailException mae) {
		report.put(message, mae);
	   }
	}

	// if one of the send mail had an error, raise the exception attaching the report
	if (!report.isEmpty()) { throw new MailDispatcherException(report); }
   }

   @Override
   public void send(MailMessageErrorHandler handler, MailMessage... messages) {

	try {
	   Session session = mailSessionService.createSession();
	   for (MailMessage message : messages) {
		try {
		   send(message, session);
		} catch (MailException mae) {
		   LOGGER.debug("Error sending mail", mae);
		   if (handler != null) {
			LOGGER.debug("Processing message handler");
			handler.process(message, mae);
		   }
		}
	   }
	} catch (MailSessionException e) {
	   LOGGER.error("Error while creating the mail session", e);
	}
   }

   @Override
   public void close() throws Exception {
	LOGGER.info("Closing mail dispatcher service {}", context.getName());
   }

}
