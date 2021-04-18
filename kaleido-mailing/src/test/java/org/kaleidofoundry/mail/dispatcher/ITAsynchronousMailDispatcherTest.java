package org.kaleidofoundry.mail.dispatcher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import org.kaleidofoundry.mail.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfigurations(value = { @NamedConfiguration(name = "local-session", uri = "classpath:/mailing/localSession.yaml"),
	@NamedConfiguration(name = "asynchronous-dispatcher", uri = "classpath:/mailing/asynchronousDispatcher.yaml") })
public class ITAsynchronousMailDispatcherTest extends AbstractMailDispatcherTest {

   private static Logger LOGGER = LoggerFactory.getLogger(ITAsynchronousMailDispatcherTest.class);
   
   private static MailDispatcher mailDispatcher;

   @BeforeClass
   public static void setup() {
	String smtpUserName = System.getProperty("smtpUser");
	String smtpUserPassword = System.getProperty("smtpPassword");

	if (smtpUserName != null) {
	   Configuration sessionConfig = ConfigurationFactory.getRegistry().get("local-session");
	   sessionConfig.setProperty("mailing.sessions.mySession.smtp.auth.user", smtpUserName);
	   sessionConfig.setProperty("mailing.sessions.mySession.smtp.auth.password", smtpUserPassword);
	   sessionConfig.setProperty("mailing.sessions.mySession-ssl.smtp.auth.user", smtpUserName);
	   sessionConfig.setProperty("mailing.sessions.mySession-ssl.smtp.auth.password", smtpUserPassword);
	   sessionConfig.setProperty("mailing.sessions.mySession-tls.smtp.auth.user", smtpUserName);
	   sessionConfig.setProperty("mailing.sessions.mySession-tls.smtp.auth.password", smtpUserPassword);
	}

	mailDispatcher = MailDispatcherFactory.provides("myDispatcher");
   }

   @AfterClass
   public static void cleanup() throws Exception {
	mailDispatcher.close();
   }

   @Override
   protected MailDispatcher getMailDispatcher() {
	return mailDispatcher;
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
