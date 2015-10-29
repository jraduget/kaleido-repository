package org.kaleidofoundry.mail.dispatcher;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kaleidofoundry.mail.MailTestConstants.CC_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.FROM_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_BODY_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_ENCODING;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_PRIORITY;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_SUBJECT;
import static org.kaleidofoundry.mail.MailTestConstants.TO_ADRESS;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.mail.MailException;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfigurations(value = { @NamedConfiguration(name = "local-dumbster-session", uri = "classpath:/mailing/localDumbsterSession.yaml"),
	@NamedConfiguration(name = "synchronous-dispatcher", uri = "classpath:/mailing/synchronousDispatcher.yaml") })
public class SynchronousMailDispatcherTest extends AbstractSynchronousMailDispatcherTest {

   private SmtpServer server;

   private MailDispatcher mailDispatcher;

   @Before
   public void setup() {
	// get smtp server port from the client configuration
	ServerOptions options = new ServerOptions(new String[] { ConfigurationFactory.getRegistry().get("local-dumbster-session")
		.getString("mailing.sessions.mySession-ssl.smtp.port") });

	server = SmtpServerFactory.startServer(options);
	mailDispatcher = MailDispatcherFactory.provides("myDispatcher");
   }

   @After
   public void cleanup() throws Exception {
	server.stop();
	mailDispatcher.close();
   }

   @Override
   protected MailDispatcher getMailDispatcher() {
	return mailDispatcher;
   }

   @Override
   public void sendMail() throws MailException {
	super.sendMail();
	mailAssertions(server, 0);
   }

   @Override
   public void sendMailWithAttachments() throws MailDispatcherException, MailException, FileNotFoundException, IOException {
	super.sendMailWithAttachments();
	mailWithAttachmentsAssertions(server, 0);
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

   private static String[] extractEmails(MailMessage message, String headerField) {
	String[] adresses = StringHelper.split(message.getFirstHeaderValue(headerField), ",");
	if (adresses != null) {
	   for (int i = 0; i < adresses.length; i++) {
		adresses[i] = adresses[i].trim();
	   }
	}
	return adresses;
   }
}
