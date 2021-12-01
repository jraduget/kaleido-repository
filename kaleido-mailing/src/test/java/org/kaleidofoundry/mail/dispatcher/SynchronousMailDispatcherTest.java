package org.kaleidofoundry.mail.dispatcher;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfigurations(value = { @NamedConfiguration(name = "local-dumbster-session", uri = "classpath:/mailing/localDumbsterSession.yaml"),
	@NamedConfiguration(name = "synchronous-dispatcher", uri = "classpath:/mailing/synchronousDispatcher.yaml") })
public class SynchronousMailDispatcherTest extends AbstractMailDispatcherTest {

   private static Logger LOGGER = LoggerFactory.getLogger(SynchronousMailDispatcherTest.class);
   
   private SmtpServer server;

   private MailDispatcher mailDispatcher;
   

   @Before
   public void setup() {
	// get smtp server port from the client configuration
	ServerOptions options = new ServerOptions(
		new String[] { ConfigurationFactory.getRegistry().get("local-dumbster-session").getString("mailing.sessions.mySession.smtp.port") });

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
	assertTrue(getMailDispatcher() instanceof SynchronousMailDispatcher);
	super.sendMail();
	mailAssertions(server, 0);
   }

   @Override
   public void sendMailWithAttachments() throws MailDispatcherException, MailException, FileNotFoundException, IOException {
	assertTrue(getMailDispatcher() instanceof SynchronousMailDispatcher);
	super.sendMailWithAttachments();
	mailWithAttachmentsAssertions(server, 0);
   }

   @Override
   protected MailMessageErrorHandler getMailMessageErrorHandler() {
	return new MailMessageErrorHandler() {
	   @Override
	   public void process(MailMessage message, Exception e) {
		LOGGER.error("MailMessageErrorHandler: {}", message.toString(), e);
	   }
	};
   }
   
}
