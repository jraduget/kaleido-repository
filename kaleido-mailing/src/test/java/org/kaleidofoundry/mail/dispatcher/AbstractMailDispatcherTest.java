package org.kaleidofoundry.mail.dispatcher;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kaleidofoundry.mail.MailTestConstants.CC_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.FROM_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.INVALID_MAIL_ADDRESS_01;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_BODY_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_ENCODING;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_PRIORITY;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_SUBJECT;
import static org.kaleidofoundry.mail.MailTestConstants.TO_ADRESS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreFactory;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.mail.InvalidMailAddressException;
import org.kaleidofoundry.mail.MailAttachmentBean;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.mail.MailTestConstants;
import org.kaleidofoundry.mail.MailingDispatcherReport;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dumbster.smtp.SmtpServer;

public abstract class AbstractMailDispatcherTest {

   private static Logger LOGGER = LoggerFactory.getLogger(AbstractMailDispatcherTest.class);

   private FileStore attachementStore;

   public AbstractMailDispatcherTest() {
	attachementStore = FileStoreFactory.provides("classpath:/");
   }

   protected abstract MailDispatcher getMailDispatcher();

   @Test
   public void sendMail() throws MailException {

	assertNotNull(getMailDispatcher());

	MailMessage message = MailTestConstants.createDefaultMailMessage();

	try {
	   getMailDispatcher().send(message);
	} catch (MailException mae) {
	   LOGGER.error("sendMail.send", mae);
	   if (mae instanceof MailDispatcherException) {
		LOGGER.error(((MailDispatcherException) mae).getReport().toString());
	   }
	   throw mae;
	}
   }

   @Test
   public void sendMailWithAttachments() throws MailDispatcherException, MailException, FileNotFoundException, IOException {
	assertNotNull(getMailDispatcher());

	MailMessage message = MailTestConstants.createDefaultMailMessage();
	// first attach using POJO
	message.attach(new MailAttachmentBean("helloworld.xml", attachementStore.get("attachments/helloworld.xml").getInputStream(), null, "text/xml", "UTF-8"));
	// second attach using filestore
	message.attach("helloworld.gif", attachementStore.get("attachments/helloworld.gif"));
	// third attach using uri
	message.attach("helloworld.txt", "/${basedir}/src/test/resources/attachments/helloworld.txt", "UTF-8");

	try {
	   getMailDispatcher().send(message);

	} catch (MailException mae) {
	   LOGGER.error("sendMailWithAttachments.send", mae);
	   if (mae instanceof MailDispatcherException) {
		LOGGER.error(((MailDispatcherException) mae).getReport().toString());
	   }
	   throw mae;
	}
   }

   @Test
   public void sendMailWithoutFromAddress() throws MailException {
	MailMessage message = new MailMessageBean();
	try {
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAs(MAIL_HTML).withBodyCharSet(MAIL_ENCODING).withPriority(MAIL_PRIORITY)
		   .withToAddresses(TO_ADRESS);
	   getMailDispatcher().send(message);
	   fail();
	} catch (MailDispatcherException mde) {

	   LOGGER.debug("sendMailWithAUniqueInvalidAddress.MailDispatcherException", mde);
	   assertEquals("mail.service.send.error", mde.getCode());

	   final MailingDispatcherReport mailReport = mde.getReport();

	   assertNotNull(mailReport);
	   assertNotNull(mailReport.getInvalidAddresses());
	   assertTrue(mailReport.getInvalidAddresses().isEmpty());

	   assertNotNull(mailReport.getMailExceptions());
	   assertEquals(1, mailReport.getMailExceptions().size());

	   MailException mae = mailReport.getMailExceptions().get(message);
	   assertTrue(mae instanceof InvalidMailAddressException);
	   assertEquals("mail.service.message.fromaddress.none", mae.getCode());
	   assertTrue(((InvalidMailAddressException) mae).getInvalidAddresses().isEmpty());
	} catch (MailException mae) {
	   LOGGER.error(mae.getMessage(), mae);
	   if (mae instanceof MailDispatcherException) {
		LOGGER.error(((MailDispatcherException) mae).getReport().toString());
	   }
	   throw mae;
	}
   }

   @Test
   public void sendMailWithoutAddress() {
	MailMessage message = new MailMessageBean();
	try {
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAs(MAIL_HTML).withBodyCharSet(MAIL_ENCODING).withFromAddress(FROM_ADRESS)
		   .withPriority(MAIL_PRIORITY);
	   getMailDispatcher().send(message);
	} catch (MailDispatcherException mde) {
	   LOGGER.debug("sendMailWithAUniqueInvalidAddress.MailDispatcherException", mde);
	   assertEquals("mail.service.send.error", mde.getCode());

	   final MailingDispatcherReport mailReport = mde.getReport();

	   assertNotNull(mailReport);
	   assertNotNull(mailReport.getInvalidAddresses());
	   assertTrue(mailReport.getInvalidAddresses().isEmpty());

	   assertNotNull(mailReport.getMailExceptions());
	   assertEquals(1, mailReport.getMailExceptions().size());

	   MailException mae = mailReport.getMailExceptions().get(message);
	   assertTrue(mae instanceof InvalidMailAddressException);
	   assertEquals("mail.service.message.address.none", mae.getCode());
	   assertTrue(((InvalidMailAddressException) mae).getInvalidAddresses().isEmpty());

	} catch (MailSessionException mse) {
	   LOGGER.error(mse.getMessage(), mse);
	   fail();
	}
   }

   @Test
   // send with only one address that is invalid
   public void sendMailWithAUniqueInvalidAddress() {

	MailMessage message = null;
	try {
	   message = new MailMessageBean();
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAs(MAIL_HTML).withBodyCharSet(MAIL_ENCODING).withFromAddress(FROM_ADRESS)
		   .withPriority(MAIL_PRIORITY).getToAddresses().add(INVALID_MAIL_ADDRESS_01);
	   getMailDispatcher().send(message);
	   fail();
	} catch (MailDispatcherException mde) {
	   LOGGER.debug("sendMailWithAUniqueInvalidAddress.MailDispatcherException", mde);
	   final MailingDispatcherReport mailReport = mde.getReport();

	   assertEquals("mail.service.send.error", mde.getCode());

	   assertNotNull(mailReport);
	   assertNotNull(mailReport.getInvalidAddresses());
	   assertEquals(1, mailReport.getInvalidAddresses().size());
	   assertTrue(mailReport.getInvalidAddresses().get(message).contains(INVALID_MAIL_ADDRESS_01));

	   assertNotNull(mailReport.getMailExceptions());
	   assertEquals(1, mailReport.getMailExceptions().size());

	   MailException mae = mailReport.getMailExceptions().get(message);
	   assertTrue(mae instanceof InvalidMailAddressException);
	   assertEquals("mail.service.message.address.invalid", mae.getCode());

	   assertArrayEquals(new String[] { INVALID_MAIL_ADDRESS_01 }, ((InvalidMailAddressException) mae).getInvalidAddresses().toArray(new String[0]));

	} catch (MailSessionException mse) {
	   LOGGER.error(mse.getMessage(), mse);
	   fail();
	}
   }

   @Test
   // send several mails with valid and invalid address
   public void sendMailWithValidAndInvalidAddress() {
	MailMessage message = new MailMessageBean();

	try {
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAs(MAIL_HTML).withBodyCharSet(MAIL_ENCODING).withFromAddress(FROM_ADRESS)
		   .withPriority(MAIL_PRIORITY);
	   message.withToAddresses(TO_ADRESS);
	   message.getToAddresses().add("wrongmail.com");
	   getMailDispatcher().send(message);
	   fail();

	} catch (MailDispatcherException mde) {
	   LOGGER.debug("sendMailWithAUniqueInvalidAddress.MailDispatcherException", mde);
	   assertEquals("mail.service.send.error", mde.getCode());

	   final MailingDispatcherReport mailReport = mde.getReport();

	   assertNotNull(mailReport);
	   assertNotNull(mailReport.getInvalidAddresses());
	   assertEquals(1, mailReport.getInvalidAddresses().size());
	   assertTrue(mailReport.getInvalidAddresses().get(message).contains(INVALID_MAIL_ADDRESS_01));

	   assertNotNull(mailReport.getMailExceptions());
	   assertEquals(1, mailReport.getMailExceptions().size());

	   MailException mae = mailReport.getMailExceptions().get(message);
	   assertTrue(mae instanceof InvalidMailAddressException);
	   assertEquals("mail.service.message.address.invalid", mae.getCode());
	   assertEquals(((InvalidMailAddressException) mae).getInvalidAddresses(), Arrays.asList(INVALID_MAIL_ADDRESS_01));

	} catch (MailSessionException mse) {
	   LOGGER.error(mse.getMessage(), mse);
	   fail();
	}
   }


   public static void mailAssertions(SmtpServer server, int mailIndex) {
	assertEquals(1, server.getEmailCount());
	assertEquals(String.valueOf(MAIL_PRIORITY), server.getMessage(mailIndex).getFirstHeaderValue("X-Priority"));
	assertEquals(FROM_ADRESS, server.getMessage(mailIndex).getFirstHeaderValue("From"));
	assertArrayEquals(TO_ADRESS, extractEmails(server.getMessage(mailIndex), "To"));
	assertArrayEquals(CC_ADRESS, extractEmails(server.getMessage(mailIndex), "Cc"));
	// assertArrayEquals(BCC_ADRESS, extractEmails(server.getMessage(mailIndex), "Bcc"));
	assertEquals(MAIL_SUBJECT, server.getMessage(mailIndex).getFirstHeaderValue("Subject"));
	assertTrue(server.getMessage(mailIndex).getBody().contains(MAIL_BODY_HTML));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Type: " + (MAIL_HTML ? "text/html" : "text/plain")));
	assertTrue(server.getMessage(mailIndex).getBody().contains("charset=" + MAIL_ENCODING));
   }

   public static void mailWithAttachmentsAssertions(SmtpServer server, int mailIndex) {
	assertEquals(1, server.getEmailCount());
	assertEquals(String.valueOf(MAIL_PRIORITY), server.getMessage(mailIndex).getFirstHeaderValue("X-Priority"));
	assertEquals(FROM_ADRESS, server.getMessage(mailIndex).getFirstHeaderValue("From"));
	assertArrayEquals(TO_ADRESS, extractEmails(server.getMessage(mailIndex), "To"));
	assertArrayEquals(CC_ADRESS, extractEmails(server.getMessage(mailIndex), "Cc"));
	// assertArrayEquals(BCC_ADRESS, extractEmails(server.getMessage(mailIndex), "Bcc"));
	assertEquals(MAIL_SUBJECT, server.getMessage(mailIndex).getFirstHeaderValue("Subject"));
	assertTrue(server.getMessage(mailIndex).getBody().contains(MAIL_BODY_HTML));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Type: " + (MAIL_HTML ? "text/html" : "text/plain")));
	assertTrue(server.getMessage(mailIndex).getBody().contains("charset=" + MAIL_ENCODING));

	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Type: text/html; charset=UTF-8"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Transfer-Encoding: 7bit"));

	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Type: text/xml; charset=UTF-8; name=helloworld.xml"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Transfer-Encoding: quoted-printable"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Disposition: attachment; filename=helloworld.xml"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("<line>this is an email attachment with some specials characters</line>"));

	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Type: image/gif; name=helloworld.gif"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Transfer-Encoding: base64"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Disposition: attachment; filename=helloworld.gif"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("R0lGODlhvAIsAfcAAHPLLiEkIqQnKhkoKjNKYjNUJ9Amc3FrJJAmarInV/LMmP4mavvmsycoHTYo"));

	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Type: text/plain; charset=UTF-8; name=helloworld.txt"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Transfer-Encoding: 7bit"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("Content-Disposition: attachment; filename=helloworld.txt"));
	assertTrue(server.getMessage(mailIndex).getBody().contains("this is an email attachment"));
   }

   public static String[] extractEmails(com.dumbster.smtp.MailMessage message, String headerField) {
	String[] adresses = StringHelper.split(message.getFirstHeaderValue(headerField), ",");
	if (adresses != null) {
	   for (int i = 0; i < adresses.length; i++) {
		adresses[i] = adresses[i].trim();
	   }
	}
	return adresses;
   }   
}
