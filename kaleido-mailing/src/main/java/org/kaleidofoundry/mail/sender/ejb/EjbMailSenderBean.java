/*  
 * Copyright 2008-2014 the original author or authors 
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

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.sender.MailSenderFactory;
import org.kaleidofoundry.mail.sender.MailSenderService;
import org.kaleidofoundry.mail.session.MailSessionContext;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulation du service via un EJB
 * 
 * @author jraduget
 */
public class EjbMailSenderBean implements SessionBean {

   private static final long serialVersionUID = 7932719039322163120L;

   /** Logger */
   protected static final Logger LOGGER = LoggerFactory.getLogger(EjbMailSenderBean.class);

   // Session context that the container provides for a session enterprise Bean instance
   private SessionContext sessionContext = null;

   // Context MailSession
   private MailSessionContext mailContext = null;
   // Service d'envoi de mail
   private MailSenderService mailService = null;

   /* ========================= ejbCreate methods =========================== */

   /**
    * There must be one ejbCreate() method per create() method on the Home
    * interface, and with the same signature.
    * 
    * @param mailsessionName Nom de la section sessionMail � utiliser
    *           (voir mailSession.properties dans META-INF/
    * @throws CreateException
    */
   public void ejbCreate(final String mailsessionName) throws CreateException {

	final String classpathRessource = "classpath:/META-INF/mailSession.properties";
	Configuration config = null;

	try {
	   // Debug
	   if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("ejbCreate, mailsessionName=" + mailsessionName + ", this=" + hashCode());
	   }

	   // Recherche et chargement de la configuration
	   config = ConfigurationFactory.provides("ejbCreate", classpathRessource, new RuntimeContext<Configuration>());
	   config.load();

	   // D�tail de la configuration
	   if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(config.toString());
	   }

	   // MailSessionContext, instanciation et chargement
	   mailContext = new MailSessionContext(mailsessionName, config);

	   // Factory pour obtenir le service et pour cr�er un message
	   mailService = MailSenderFactory.createSessionService(mailContext);

	} catch (final MailSessionException mse) {
	   LOGGER.error(mse.getMessage(), mse);
	   throw new CreateException(mse.getMessage());
	} catch (final ResourceException ce) {
	   LOGGER.error(ce.getMessage(), ce);
	   throw new CreateException(ce.getMessage());
	}

   }

   /**
    * There must be one ejbCreate() method per create() method on the Home
    * interface, and with the same signature.
    * Le section sessionMail utilis�e sera "default"
    * (voir mailSession.properties dans META-INF/
    * 
    * @throws CreateException
    */
   public void ejbCreate() throws CreateException {
	ejbCreate("default");
   }

   /* =============== javax.ejb.SessionBean 2.0 implementation ============== */

   /**
    * Set the associated session context. The container calls this method after
    * the instance creation. The enterprise Bean instance should store the
    * reference to the context object in an instance variable. This method is
    * called with no transaction context.
    * 
    * @param sessionContext A SessionContext interface for the instance.
    * @throws EJBException Thrown by the method to indicate a failure caused by
    *            a system-level error.
    * @throws java.rmi.RemoteException This exception is defined in the method
    *            signature to provide backward compatibility for applications
    *            written for the EJB 1.0 specification. Enterprise beans written
    *            for the EJB 1.1 specification should throw the
    *            javax.ejb.EJBException instead of this exception. Enterprise
    *            beans written for the EJB2.0 and higher specifications must throw
    *            the javax.ejb.EJBException instead of this exception.
    */
   public void setSessionContext(final SessionContext sessionContext) throws EJBException, RemoteException {
	this.sessionContext = sessionContext;
   }

   /**
    * A container invokes this method before it ends the life of the session
    * object. This happens as a result of a client's invoking a remove
    * operation, or when a container decides to terminate the session object
    * after a timeout. This method is called with no transaction context.
    * 
    * @throws EJBException Thrown by the method to indicate a failure caused by
    *            a system-level error.
    * @throws java.rmi.RemoteException This exception is defined in the method
    *            signature to provide backward compatibility for enterprise beans
    *            written for the EJB 1.0 specification. Enterprise beans written
    *            for the EJB 1.1 specification should throw the
    *            javax.ejb.EJBException instead of this exception. Enterprise
    *            beans written for the EJB2.0 and higher specifications must throw
    *            the javax.ejb.EJBException instead of this exception.
    */
   public void ejbRemove() throws EJBException, java.rmi.RemoteException {
	LOGGER.debug("ejbRemove" + ", this=" + hashCode());
	mailService = null;
   }

   /**
    * The activate method is called when the instance is activated from its
    * "passive" state. The instance should acquire any resource that it has
    * released earlier in the ejbPassivate() method. This method is called with
    * no transaction context.
    * 
    * @throws EJBException Thrown by the method to indicate a failure caused by
    *            a system-level error.
    * @throws java.rmi.RemoteException This exception is defined in the method
    *            signature to provide backward compatibility for enterprise beans
    *            written for the EJB 1.0 specification. Enterprise beans written
    *            for the EJB 1.1 specification should throw the
    *            javax.ejb.EJBException instead of this exception. Enterprise
    *            beans written for the EJB2.0 and higher specifications must throw
    *            the javax.ejb.EJBException instead of this exception.
    */
   public void ejbActivate() throws EJBException, java.rmi.RemoteException {
	try {
	   LOGGER.debug("ejbActivate" + ", this=" + hashCode());
	   mailService = MailSenderFactory.createSessionService(mailContext);
	} catch (final MailSessionException mae) {
	   LOGGER.error(mae.getMessage(), mae);
	   throw new EJBException(mae.getMessage());
	}
   }

   /**
    * The passivate method is called before the instance enters the "passive"
    * state. The instance should release any resources that it can re-acquire
    * later in the ejbActivate() method. After the passivate method completes,
    * the instance must be in a state that allows the container to use the Java
    * Serialization protocol to externalize and store away the instance's
    * state. This method is called with no transaction context.
    * 
    * @throws EJBException Thrown by the method to indicate a failure caused by
    *            a system-level error.
    * @throws java.rmi.RemoteException This exception is defined in the method
    *            signature to provide backward compatibility for enterprise beans
    *            written for the EJB 1.0 specification. Enterprise beans written
    *            for the EJB 1.1 specification should throw the
    *            javax.ejb.EJBException instead of this exception. Enterprise
    *            beans written for the EJB2.0 and higher specifications must throw
    *            the javax.ejb.EJBException instead of this exception.
    */
   public void ejbPassivate() throws EJBException, java.rmi.RemoteException {
	LOGGER.debug("ejbPassivate" + ", this=" + hashCode());
	mailService = null;
   }

   /* ======================== Mailer implementation ======================== */

   protected SessionContext getSessionContext() {
	return sessionContext;
   }

   /**
    * Envoi de mail
    * 
    * @param message
    * @throws MailException
    * @throws AddressException
    * @throws MessagingException
    */
   public void send(final MailMessage message) throws MailException, AddressException, MessagingException {
	if (LOGGER.isDebugEnabled()) {
	   LOGGER.debug("send MailMessage, this=" + hashCode());
	}

	// Envoie du message
	mailService.send(message);

	if (LOGGER.isDebugEnabled()) {
	   LOGGER.debug("send MailMessage done, this=" + hashCode());
	}

   }

}