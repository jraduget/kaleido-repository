package org.kaleidofoundry.mail.sender;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.mail.sender.ejb.EjbMailSender;
import org.kaleidofoundry.mail.sender.ejb.EjbMailSenderHome;
import org.kaleidofoundry.mail.session.MailSessionContext;

/**
 * MailSenderService, implémentation ejb
 * 
 * @author Jerome RADUGET
 */
public class MailSenderServiceEjbImpl implements MailSenderService {

   private final EjbMailSenderHome sessionMailerHome;
   private final EjbMailSender sessionMailer;

   public MailSenderServiceEjbImpl(final String jndiName, final JndiContext jndiContext) throws JndiResourceException,
	   RemoteException, CreateException {
	// Locator pour le service (à n'instancier qu'une fois généralement.....)
	final JndiResourceLocator<EjbMailSenderHome> locator = new JndiResourceLocator<EjbMailSenderHome>(jndiContext);

	// Home de l'ejb
	sessionMailerHome = locator.lookup(jndiName, EjbMailSenderHome.class);

	// Instanciation
	sessionMailer = sessionMailerHome.create();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#createMessage()
    */
   public MailMessage createMessage() {
	return new MailMessageBean();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#getSessionContext()
    */
   public MailSessionContext getSessionContext() {
	return null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#send(org.kaleidofoundry.mail.MailMessage)
    */
   public void send(final MailMessage message) throws MailException, AddressException, MessagingException {

	try {
	   sessionMailer.send(message);
	} catch (final RemoteException re) {
	   throw new MailSenderException("jndi.error.remote", re, re.getMessage());
	}
   }

}
