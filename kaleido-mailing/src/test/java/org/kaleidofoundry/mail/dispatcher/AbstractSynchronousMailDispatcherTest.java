package org.kaleidofoundry.mail.dispatcher;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kaleidofoundry.mail.MailTestConstants.FROM_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.INVALID_MAIL_ADDRESS_01;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_BODY_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_SUBJECT;
import static org.kaleidofoundry.mail.MailTestConstants.TO_ADRESS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreFactory;
import org.kaleidofoundry.mail.InvalidMailAddressException;
import org.kaleidofoundry.mail.MailAttachmentBean;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.mail.MailingDispatcherReport;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSynchronousMailDispatcherTest {

   private static Logger LOGGER = LoggerFactory.getLogger(AbstractSynchronousMailDispatcherTest.class);

   private FileStore attachementStore;

   public AbstractSynchronousMailDispatcherTest() {
	attachementStore = FileStoreFactory.provides("classpath:/");
   }

   protected abstract MailDispatcher getMailDispatcher();

   @Test
   public void sendMail() throws MailException {

	assertNotNull(getMailDispatcher());
	assertTrue(getMailDispatcher() instanceof SynchronousMailDispatcher);

	MailMessage message = new MailMessageBean();
	message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withFromAddress(FROM_ADRESS).withPriority(3)
		.getToAddresses().add(TO_ADRESS);

	try {
	   getMailDispatcher().send(message);
	} catch (MailException mae) {
	   LOGGER.error("sendMail.send", mae);
	   throw mae;
	}
   }

   @Test
   public void sendMailWithAttachments() throws MailDispatcherException, MailException, FileNotFoundException, IOException {
	assertNotNull(getMailDispatcher());
	assertTrue(getMailDispatcher() instanceof SynchronousMailDispatcher);

	MailMessage message = new MailMessageBean();
	message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withFromAddress(FROM_ADRESS).withPriority(3)
		.getToAddresses().add(TO_ADRESS);

	// first attach using POJO
	message.attach(new MailAttachmentBean("helloworld.xml", "text/xml", null, attachementStore.get("attachments/helloworld.xml").getInputStream()));
	// second attach using filestore
	message.attach("helloworld.gif", attachementStore.get("attachments/helloworld.gif"));
	// third attach using uri
	message.attach("helloworld.txt", "/${basedir}/src/test/resources/attachments/helloworld.txt");

	try {
	   getMailDispatcher().send(message);
	} catch (MailException mae) {
	   LOGGER.error("sendMailWithAttachments.send", mae);
	   throw mae;
	}
   }

   @Test
   public void sendMailWithoutFromAddress() throws MailException {
	MailMessage message = new MailMessageBean();
	try {
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withPriority(3).getToAddresses().add(TO_ADRESS);
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
	} catch (MailException mse) {
	   LOGGER.error(mse.getMessage(), mse);
	   throw mse;
	}
   }

   @Test
   public void sendMailWithoutAddress() {
	MailMessage message = new MailMessageBean();
	try {
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withFromAddress(FROM_ADRESS).withPriority(3);
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
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withFromAddress(FROM_ADRESS).withPriority(3)
		   .getToAddresses().add(INVALID_MAIL_ADDRESS_01);
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
	   message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withFromAddress(FROM_ADRESS).withPriority(3);
	   message.getToAddresses().add(TO_ADRESS);
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

}
