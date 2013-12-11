/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.mail.sender.ejb;

import java.util.Hashtable;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire d'envoi de mail via écoute sur queue JMS
 * 
 * @author Jerome RADUGET
 */
public class EjbMailJmsReceiver implements MessageDrivenBean, MessageListener {

   private static final long serialVersionUID = -836991640983270215L;

   /** Logger du bean */
   protected static final Logger logger = LoggerFactory.getLogger(EjbMailJmsReceiver.class);

   /** Nom du fichier de configuration de l'ejb par défaut */
   public final static String ConfigurationName = "classpath:/META-INF/jmsReceiver.properties";

   /** Nom par défaut de l'ejb envoyant les mail */
   public final static String DefaultEjbMailSender = "ejbMailSender";
   /** Nom par défaut de la queue de mail en erreur */
   public final static String DefaultQueueError = "queue/queueMailSessionError";
   /** Nom par défaut de la queue de message non destiné */
   public final static String DefaultQueueBin = "queue/queueMailSessionBin";
   /** Nom par défaut de la queue de message non destiné */
   public final static String DefaultQueueConnectionFactory = "QueueConnectionFactory";

   /** Nom de la propriété donnant le nom de l'ejb envoyant les mail */
   public final static String PropertyEjbMailSender = "ejb.mailsender.name";
   /** Nom de la propriété donnant le nom de la queue d'erreur eventuelle */
   public final static String PropertyQueueError = "queue.error.name";
   /** Nom de la propriété donnant le nom de la queue poubelle eventuelle */
   public final static String PropertyQueueBin = "queue.bin.name";
   /** Nom de la propriété donnant le nom de la queue factory */
   public final static String PropertyQueueConnectionFactory = "queue.connection.factory.name";

   // Ejb Context
   protected MessageDrivenContext context;

   // Properties
   protected String ejbMailSender;
   protected String queueError;
   protected String queueBin;
   protected String queueConnectionFactory;

   /*
    * (non-Javadoc)
    * @see javax.ejb.MessageDrivenBean#setMessageDrivenContext(javax.ejb.MessageDrivenContext)
    */
   public void setMessageDrivenContext(final MessageDrivenContext context) throws EJBException {
	this.context = context;
   }

   public void ejbCreate() {
	logger.debug("ejbCreate, this=" + hashCode());

	// Chargement des informations de configuration eventuelles
	Configuration config = null;

	try {
	   // Recherche et chargement de la configuration
	   config = ConfigurationFactory.provides("ejbMailJmsReceiver", ConfigurationName, new RuntimeContext<Configuration>());
	   config.load();
	} catch (final ResourceException cfe) {
	   logger.warn(cfe.getMessage() + ". The default configuration will be used.");
	}

	// Si une configuration est trouvé
	if (config != null) {
	   ejbMailSender = config.getString(PropertyEjbMailSender);
	   queueError = config.getString(PropertyQueueError);
	   queueBin = config.getString(PropertyQueueBin);
	   queueConnectionFactory = config.getString(PropertyQueueConnectionFactory);

	   if (ejbMailSender == null) {
		ejbMailSender = DefaultEjbMailSender;
	   }
	   if (queueConnectionFactory == null) {
		queueConnectionFactory = DefaultQueueConnectionFactory;
	   }

	   // Sinon option par défaut
	} else {
	   ejbMailSender = DefaultEjbMailSender;
	   queueError = DefaultQueueError;
	   queueBin = DefaultQueueBin;
	   queueConnectionFactory = DefaultQueueConnectionFactory;
	}

   }

   /*
    * (non-Javadoc)
    * @see javax.ejb.MessageDrivenBean#ejbRemove()
    */
   public void ejbRemove() {

	logger.debug("ejbRemove, this=" + hashCode());
	context = null;
   }

   /*
    * (non-Javadoc)
    * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
    */
   public void onMessage(final Message message) {

	MailMessage mail = null;
	Throwable th = null;

	if (logger.isDebugEnabled()) {
	   logger.debug("onMessage, this={}", hashCode());
	}

	try {

	   // Debug
	   if (logger.isDebugEnabled()) {
		logger.debug("onMessage, this=" + hashCode() + ", JMSMessageID=" + message.getJMSMessageID());
		logger.debug("onMessage, this=" + hashCode() + ", JMSCorrelationID=" + message.getJMSCorrelationID());
	   }

	   // Reception du message
	   if (message != null && message instanceof ObjectMessage) {

		mail = (MailMessage) ((ObjectMessage) message).getObject();

		if (logger.isDebugEnabled()) {
		   logger.debug("onMessage, this=" + hashCode() + ", MailMessage=" + mail.toString());
		}

		// Si message n'est pas "destiné"
	   } else {
		// Reroutage file poubelle ?
		onMessageBin(message, null);
		// Retour
		return;
	   }

	   // Instanciation du sender
	   final EjbMailSender sender = createEjbMailSender(getEjbMailSenderName());

	   // Envoi du message
	   sender.send(mail);

	} catch (final MailSessionException mse) {
	   th = mse;
	} catch (final AddressException adre) {
	   th = adre;
	} catch (final MessagingException mse) {
	   th = mse;
	} catch (final Throwable t) {
	   logger.error("onMessage, this=" + hashCode(), t);
	   // Reroutage autre file poubelle ?
	   onMessageBin(message, t);
	   th = t;
	} finally {
	   if (logger.isDebugEnabled()) {
		logger.debug("onMessage.finally, this=" + hashCode());
	   }

	   if (th != null) {
		onMessageError(message, th);
	   }
	}
   }

   /**
    * Traitement des messages avec erreurs de traitement
    * 
    * @param message
    * @param th exception eventuelle
    */
   protected void onMessageError(final Message message, final Throwable th) {
	logger.error("onMessageError, this=" + hashCode(), th);

	// RollBack Transaction
	// context.setRollbackOnly();

	if (!StringHelper.isEmpty(getJmsQueueErrorName())) {

	   try {
		redirectMessage(message, th, getJmsQueueErrorName());
	   } catch (final Exception e) {
		// Transaction à annuler ?
		// context.setRollbackOnly();
	   }
	}
   }

   /**
    * Traitement des messages non désirés
    * 
    * @param message
    * @param th exception eventuelle
    */
   protected void onMessageBin(final Message message, final Throwable th) {
	logger.error("onMessageBin, this=" + hashCode(), th);

	if (!StringHelper.isEmpty(getJmsQueueBinName())) {

	   try {
		redirectMessage(message, th, getJmsQueueBinName());
	   } catch (final Exception e) {
		// Transaction à annuler ?
	   }
	}
   }

   /**
    * @return Accès à l'ejb qui envoie les mail
    * @param ejbMailSenderName Nom de l'ejb (accès local) qui envoi les mail
    * @throws Exception
    */
   protected EjbMailSender createEjbMailSender(final String ejbMailSenderName) throws Exception {
	// InitialContext Initialisation
	Context initialContext = null;
	try {
	   final Hashtable<?, ?> env = new Hashtable<Object, Object>();
	   logger.debug("newIntialContext");
	   initialContext = new InitialContext(env);
	} catch (final NamingException ne) {
	   logger.error("newIntialContext", ne);
	   throw ne;
	}

	// Connecting to the mailSenderHome via JNDI
	EjbMailSenderHome mailSenderHome = null;
	try {
	   logger.debug("initialContext.lookup");
	   mailSenderHome = (EjbMailSenderHome) PortableRemoteObject.narrow(initialContext.lookup(ejbMailSenderName), EjbMailSenderHome.class);

	} catch (final Exception e) {
	   logger.error("initialContext.lookup", e);
	   throw e;
	}

	// MailerBean creation
	EjbMailSender mailSender = null;
	try {
	   logger.debug("mailSenderHome.create");
	   mailSender = mailSenderHome.create();

	} catch (final Exception e) {
	   logger.error("mailSenderHome.create", e);
	   throw e;
	}

	return mailSender;
   }

   /**
    * @return Nom de l'ejb (local) capable d'envoyer les message
    */
   protected String getEjbMailSenderName() {
	return ejbMailSender;
   }

   /**
    * @return Nom JNDI de la queue JMS utilisée pour les messages rejetés
    */
   protected String getJmsQueueErrorName() {
	return queueError;
   }

   /**
    * @return Nom JNDI de la queue JMS utilisée pour les messages poubelle
    */
   protected String getJmsQueueBinName() {
	return queueBin;
   }

   /**
    * @return Nom JNDI de la queue connection factory (pour les messages erreur / bin)
    */
   protected String getQueueConnectionFactory() {
	return queueConnectionFactory;
   }

   /**
    * Envoi de message, (erreur ou bin)
    * 
    * @param message
    * @param th
    * @param queueName
    * @throws NamingException
    * @throws JMSException
    */
   protected void redirectMessage(final Message message, final Throwable th, final String queueName) throws NamingException, JMSException {
	Context initialContext = null;
	QueueConnectionFactory factory = null;
	QueueConnection connection = null;
	QueueSession session = null;
	Queue queue = null;
	QueueSender sender = null;

	try {
	   initialContext = new InitialContext();
	} catch (final NamingException nae) {
	   logger.error("redirectMessage, intialContext error", nae);
	   throw nae;
	}

	// Lookup connection factory (jndi name)
	try {
	   factory = (QueueConnectionFactory) initialContext.lookup(getQueueConnectionFactory());

	} catch (final NamingException nae) {
	   logger.error("redirectMessage.error, intialContext lookup QueueConnectionFactory failed (" + getQueueConnectionFactory() + ")", nae);
	   throw nae;
	}

	// Lookup queue (jndi name)
	try {
	   queue = (Queue) initialContext.lookup(queueName);

	} catch (final NamingException nae) {
	   logger.error("redirectMessage.error, intialContext lookup Queue failed", nae);
	   throw nae;
	}

	try {
	   // Create connection
	   connection = factory.createQueueConnection();
	   session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

	   // Create session (with no transaction)
	   sender = session.createSender(queue);

	   // Send
	   sender.send(message);

	} catch (final JMSException jmse) {
	   logger.error("redirectMessage.error, JMS exception occured", jmse);
	   throw jmse;

	} finally {
	   if (connection != null) {
		try {
		   connection.close();
		} catch (final JMSException jmse) {
		   logger.error("redirectMessage.error, JMS connection close error", jmse);
		}
	   }
	}
   }
}
