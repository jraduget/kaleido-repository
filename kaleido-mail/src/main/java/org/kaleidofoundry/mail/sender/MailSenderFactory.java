package org.kaleidofoundry.mail.sender;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.mail.sender.ejb.EjbMailSender;
import org.kaleidofoundry.mail.session.MailSessionContext;
import org.kaleidofoundry.mail.session.MailSessionException;

/**
 * Factory permettant la création d'une nouvelle instance d'un service d'envoi de mail
 * 
 * @author Jerome RADUGET
 */
public abstract class MailSenderFactory {

   /**
    * Création d'une nouvelle instance d'un service d'envoi de mail
    * en utilisant un ejb {@link EjbMailSender}
    * 
    * @param ejbJndiName Nom jndi de l'ejb
    * @param context Contexte de connection Jndi
    * @return Nouvel instance d'un service {@link MailSenderService}
    * @throws JndiResourceException En cas de problème d'accès Jndi
    * @throws CreateException 
    * @throws RemoteException 
    */
   public static MailSenderService createEjbService(final String ejbJndiName, final JndiContext context)
	   throws JndiResourceException, CreateException, RemoteException {
	return new MailSenderServiceEjbImpl(ejbJndiName, context);
   }

   /**
    * Création d'une nouvelle instance d'un service d'envoi de mail
    * en utilisant un ejb {@link EjbMailSender}
    * 
    * @param jndiName Nom Jndi de la queue JMS
    * @param jndiQueueFactoryName Nom Jndi de la queueFactory JMS
    * @param context Jndi Context
    * @param jmsSessionTransacted Transaction Jms true/false
    * @return Nouvel instance d'un service {@link MailSenderService}
    * @throws JndiResourceException En cas de problème d'accès Jndi
    */
   public static MailSenderService createJmsService(final String jndiName, final String jndiQueueFactoryName,
	   final JndiContext context, final boolean jmsSessionTransacted) throws JndiResourceException {
	return new MailSenderServiceJmsImpl(jndiName, jndiQueueFactoryName, context, jmsSessionTransacted);
   }

   /**
    * Création d'une nouvelle instance d'un service d'envoi de mail à
    * partir d'un {@link MailSessionContext}
    * 
    * @param context
    * @return Nouvel instance d'un service {@link MailSenderService}
    * @throws MailSessionException
    */
   public static MailSenderService createSessionService(final MailSessionContext context) throws MailSessionException {
	return new MailSenderServiceSessionImpl(context);
   }

   /**
    * @return Constructeur de message
    */
   public static MailMessage createMessage() {
	return new MailMessageBean();
   }
}
