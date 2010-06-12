package org.kaleidofoundry.mail;

import static org.kaleidofoundry.mail.MailTestConstants.CC_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.CONFIG_RESOURCE;
import static org.kaleidofoundry.mail.MailTestConstants.FROM_ADRESS;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_BODY_HTML;
import static org.kaleidofoundry.mail.MailTestConstants.MAIL_SUBJECT;
import static org.kaleidofoundry.mail.MailTestConstants.TO_ADRESS;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.mail.MessagingException;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.mail.sender.MailSenderFactory;
import org.kaleidofoundry.mail.sender.ejb.EjbMailSender;
import org.kaleidofoundry.mail.sender.ejb.EjbMailSenderHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client pour test EjbMailSender (Ejb gérant l'envoi de mail direct)
 * 
 * @author Jerome RADUGET
 */
public class EjbMailSenderClient {

   private static final Logger LOGGER = LoggerFactory.getLogger(EjbMailSenderClient.class);

   /** Nom du contexte jndi à utiliser dans le fichier de config */
   static final String JndiContextName = "jboss";

   /** Nom jndi de l'ejb gérant l'envoi de mail */
   static final String EjbMailSenderName = "ejbMailSender";

   public static void main(final String[] args) {

	Configuration config = null;

	EjbMailSenderHome sessionMailerHome = null;
	EjbMailSender sessionMailer = null;

	try {
	   // Recherche et chargement de la Configuration
	   config = ConfigurationFactory.provideConfiguration("ejbMailSender", CONFIG_RESOURCE, new RuntimeContext<Configuration>());
	   config.load();

	   // Context de connection JNDI
	   final JndiContext context = new JndiContext(JndiContextName, config);

	   // Locator pour le service (à n'instancier qu'une fois généralement.....)
	   final JndiResourceLocator<EjbMailSenderHome> locator = new JndiResourceLocator<EjbMailSenderHome>(context);

	   // Home de l'ejb
	   sessionMailerHome = locator.lookup(EjbMailSenderName, EjbMailSenderHome.class);

	   // Instanciation
	   sessionMailer = sessionMailerHome.create();

	   // Construction du message
	   final MailMessage message = MailSenderFactory.createMessage();
	   message.setSubject(MAIL_SUBJECT);
	   message.setContent(MAIL_BODY_HTML);
	   message.setBodyContentHtml();

	   message.setFromAdress(FROM_ADRESS);
	   message.getToAdress().add(TO_ADRESS);
	   message.getCcAdress().add(CC_ADRESS);

	   // Send
	   sessionMailer.send(message);

	} catch (final StoreException jndie) {
	   LOGGER.error("store access problem", jndie);
	} catch (final JndiResourceException jndie) {
	   LOGGER.error("jndi access problem", jndie);
	} catch (final MailException mse) {
	   LOGGER.error("mail session problem", mse);
	} catch (final CreateException ejbce) {
	   LOGGER.error("ejb create problem", ejbce);
	} catch (final RemoteException rme) {
	   LOGGER.error("remote exception", rme);
	} catch (final MessagingException msge) {
	   LOGGER.error("messaging exception", msge);
	} finally {

	}

   }

}
