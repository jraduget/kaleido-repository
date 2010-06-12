package org.kaleidofoundry.mail;

import static org.kaleidofoundry.mail.MailTestConstants.CC_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.CONFIG_RESOURCE;
import static org.kaleidofoundry.mail.MailTestConstants.FROM_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.LOCAL_MAIL_CONTEXT_NAME;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_BODY_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_SUBJECT;
import static org.kaleidofoundry.mail.MailTestConstants.TO_ADRESS;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.mail.MessagingException;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.mail.sender.MailSenderFactory;
import org.kaleidofoundry.mail.sender.MailSenderService;
import org.kaleidofoundry.mail.session.MailSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test MailSenderService
 * 
 * @author Jerome RADUGET
 */
public class MailSenderServiceTest {

   static final Logger LOGGER = LoggerFactory.getLogger(MailSenderServiceTest.class);

   /** Nom du contexte jndi à utiliser dans le fichier de config */
   static final String JNDI_CONTEXT_NAME = "jboss";

   /** Nom jndi de l'ejb gérant l'envoi de mail */
   static final String EJBMAIL_SENDER_NAME = "ejbMailSender";

   /** Nom jndi de la QueueConnectionFactory JMS */
   static final String JMS_QUEUEFACTORY_NAME = "QueueConnectionFactory";
   /** Nom jndi de la Queue d'envoie JMS */
   static final String JMS_QUEUE_NAME = "queue/queueMailSession";

   public static void main(final String[] args) {

	// Construction du message
	MailMessage message = null;

	try {
	   message = MailSenderFactory.createMessage();

	   message.setSubject(MAIL_SUBJECT);
	   message.setContent(MAIL_BODY_HTML);
	   message.setBodyContentHtml();

	   message.setFromAdress(FROM_ADRESS);
	   message.getToAdress().add(TO_ADRESS);
	   message.getCcAdress().add(CC_ADRESS);

	   // Fichiers attachés eventuels
	   // TODO : dépacer attachment test dans MailTestConstatns
	   message.addAttachment("image-mail-test.jpg", new URL("http://www.kaleido.fr/images/image-mail-test.jpg"));
	   // message.addAttachment("image.bmp", "C:\\WINDOWS\\Zapotec.bmp");
	   // message.addAttachment("new.doc", "C:\\WINDOWS\\SHELLNEW\\WINWORD8.doc");

	   LOGGER.info("sessionSender : begin");
	   sessionSender(args, message);
	   LOGGER.info("sessionSender : end");

	   LOGGER.info("ejbSender : begin");
	   ejbSender(args, message);
	   LOGGER.info("ejbSender : end");

	   LOGGER.info("jmsSender : begin");
	   jmsSender(args, message);
	   LOGGER.info("jmsSender : end");

	} catch (final StoreException cfe) {
	   LOGGER.error("configuration problem", cfe);
	} catch (final MailException mse) {
	   LOGGER.error("mail session problem", mse);
	} catch (final MessagingException msge) {
	   LOGGER.error("messaging exception", msge);
	} catch (final JndiResourceException jndie) {
	   LOGGER.error("messaging exception", jndie);
	} catch (final IOException ioe) {
	   LOGGER.error("messaging exception", ioe);
	} catch (final CreateException cre) {
	   LOGGER.error("create ejb exception", cre);
	} finally {

	}
   }

   // Test sender via Ejb Service
   public static void ejbSender(final String[] args, final MailMessage message) throws StoreException, MailException, MessagingException, RemoteException,
	   CreateException, JndiResourceException {
	Configuration config = null;
	MailSenderService sender = null;
	JndiContext context = null;

	// Recherche et chargement de la Configuration
	config = ConfigurationFactory.provideConfiguration("mailSenderService", CONFIG_RESOURCE, new RuntimeContext<Configuration>());
	config.load();

	// Context de connection JNDI
	context = new JndiContext(JNDI_CONTEXT_NAME, config);

	sender = MailSenderFactory.createEjbService(EJBMAIL_SENDER_NAME, context);

	// Send
	sender.send(message);
   }

   // Test sender via session Service
   public static void sessionSender(final String[] args, final MailMessage message) throws StoreException, MailException, MessagingException {
	Configuration config = null;
	MailSessionContext mailContext = null;
	MailSenderService mailService = null;

	// Recherche et chargement de la Configuration
	config = ConfigurationFactory.provideConfiguration("sessionSender", CONFIG_RESOURCE, new RuntimeContext<Configuration>());
	config.load();

	// MailSessionContext, instanciation et chargement
	mailContext = new MailSessionContext(LOCAL_MAIL_CONTEXT_NAME, config);

	// Factory pour obtenir le service et pour créer un message
	mailService = MailSenderFactory.createSessionService(mailContext);

	mailService.send(message);

   }

   // Test sender via Ejb Service
   public static void jmsSender(final String[] args, final MailMessage message) throws StoreException, MailException, MessagingException, JndiResourceException {
	Configuration config = null;
	MailSenderService sender = null;
	JndiContext context = null;

	// Recherche et chargement de la Configuration
	config = ConfigurationFactory.provideConfiguration("jmsSender", CONFIG_RESOURCE, new RuntimeContext<Configuration>());
	config.load();

	// Context de connection JNDI
	context = new JndiContext(JNDI_CONTEXT_NAME, config);

	// Sender de mail
	sender = MailSenderFactory.createJmsService(JMS_QUEUE_NAME, JMS_QUEUEFACTORY_NAME, context, false);

	// Send
	sender.send(message);
   }
}
