package org.kaleidofoundry.mail.dispatcher;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kaleidofoundry.mail.MailTestConstants.FROM_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_BODY_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_SUBJECT;
import static org.kaleidofoundry.mail.MailTestConstants.TO_ADRESS;

import javax.mail.internet.AddressException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreFactory;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceNotFoundException;
import org.kaleidofoundry.mail.MailAttachmentBean;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfigurations(value = { @NamedConfiguration(name = "local-session", uri = "classpath:/mailing/localSession.yaml"),
	@NamedConfiguration(name = "synchronous-dispatcher", uri = "classpath:/mailing/synchronousDispatcher.yaml") })
public class SynchronousMailDispatcherTest {

   public MailDispatcher mailDispatcher;
   public FileStore attachementStore;

   @Before
   public void setup() {
	mailDispatcher = MailDispatcherFactory.provides("myDispatcher");
	attachementStore = FileStoreFactory.provides("classpath:/");
   }

   @Test
   public void send() throws MailDispatcherException, MailException, ResourceNotFoundException, ResourceException {

	assertNotNull(mailDispatcher);
	assertTrue(mailDispatcher instanceof SynchronousMailDispatcher);

	MailMessage message = new MailMessageBean();
	message.withSubject(MAIL_SUBJECT).withBody(MAIL_BODY_HTML).withBodyAsHtml().withBodyCharSet("UTF-8").withFromAddress(FROM_ADRESS).withPriority(3)
		.getToAddresses().add(TO_ADRESS);

	// first attach using POJO
	message.attach(new MailAttachmentBean("logback-test.xml", "text/xml", null, attachementStore.get("logback-test.xml").getInputStream()));
	// second attach using filestore
	message.attach("logback-test2.xml", attachementStore.get("logback-test.xml"));
	// third
	//message.attach("logback-test.xml", "text/xml", null, inputstream));
	//message.createAttachment(...)
	
	mailDispatcher.send(message);
   }

   @Test
   public void sendWithAttachments() {

   }

   @Test
   public void sendWithoutFrom() {

   }

   @Test(expected = AddressException.class)
   public void sendWithIllegalAddress() {

   }

   // mail.service.address.none
   // mail.service.fromaddress.none
   // mail.service.address.invalid

}
