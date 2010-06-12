package org.kaleidofoundry.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.mail.session.MailSessionContext;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.kaleidofoundry.mail.session.MailSessionFactory;
import org.kaleidofoundry.mail.session.MailSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test MailSessionService
 * 
 * @author Jerome RADUGET
 */
public class MailSessionServiceTest {

   private final static Logger LOGGER = LoggerFactory.getLogger(MailSessionServiceTest.class);

   public static void main(final String[] args) {

	Configuration config = null;
	MailSessionContext mailContext = null;
	MailSessionService mailService = null;
	Session mailSession = null;

	try {
	   // Recherche et chargement de la Configuration
	   config = ConfigurationFactory.provideConfiguration("MailSessionServiceTest", MailTestConstants.CONFIG_RESOURCE, new RuntimeContext<Configuration>());
	   config.load();

	   // MailSessionContext, instanciation et chargement
	   mailContext = new MailSessionContext("kaleido-local", config);
	   // mailContext = new MailSessionContext("kaleido-jndi", config);

	   // Factory pour obtenir le service
	   mailService = MailSessionFactory.createMailSessionService(mailContext);

	   // Nouvelle session mail
	   mailSession = mailService.createSession();

	   try {
		// Creation et envoi d'un message
		final Message message = new MimeMessage(mailSession);
		final InternetAddress[] toRecipients = new InternetAddress[] { new InternetAddress("jerome.raduget@gmail.com") };

		message.setRecipients(Message.RecipientType.TO, toRecipients);
		message.setSubject("Subject");
		message.setContent("Content...", "text/plain");

		Transport.send(message);

		LOGGER.info("Message envoyé.");

	   } catch (final MessagingException mee) {
		LOGGER.error(mee.getMessage(), mee);
	   }

	} catch (final StoreException cfe) {
	   LOGGER.error("configuration exception", cfe);
	} catch (final MailSessionException mse) {
	   LOGGER.error(mse.getMessage(), mse);
	}
   }

}
