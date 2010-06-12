package org.kaleidofoundry.mail.session;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;


/**
 * Implémentation avec envoi en local du service Mail
 * 
 * @author Jerome RADUGET
 */
public class LocalMailSessionService implements MailSessionService {

   private MailSessionContext context;

   public LocalMailSessionService(final MailSessionContext context) {
	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailSessionService#getContext()
    */
   public MailSessionContext getContext() {
	return context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailSessionService#newSession()
    */
   public Session createSession() throws MailSessionException {

	if (getContext() == null) throw new MailSessionException("mail.session.context.null");

	// HostName
	final String hostName = getContext().getProperty(MailSessionConstants.LocalMailSessionHost);
	// Numéro de port à utiliser
	String portName = getContext().getProperty(MailSessionConstants.LocalMailSessionPort);
	// Authentification ?
	final String authen = getContext().getProperty(MailSessionConstants.LocalMailSessionAuthen);
	// SSL ?
	final boolean ssl = false;
	// Session en retour
	Session mailSession = null;

	final Properties mailProps = new Properties();

	portName = portName == null ? String.valueOf(MailSessionConstants.DEFAULT_SMTP_PORT) : portName;

	if (hostName == null) throw new MailSessionException("mail.session.context.property", new String[] { MailSessionConstants.LocalMailSessionHost });

	if (hostName != null) {
	   mailProps.setProperty(MailSessionConstants.LocalMailSessionHost, hostName);
	}
	if (portName != null) {
	   mailProps.setProperty(MailSessionConstants.LocalMailSessionPort, portName);
	}
	if (authen != null) {
	   mailProps.setProperty(MailSessionConstants.LocalMailSessionAuthen, authen);
	}

	if (ssl) {
	   // check with jdk6 ... Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	   mailProps.put("mail.smtp.socketFactory.class", MailSessionConstants.SSL_FACTORY);
	   mailProps.put("mail.smtp.socketFactory.fallback", "false");
	   mailProps.put("mail.smtp.auth", "true");
	   mailProps.put("mail.smtp.socketFactory.port", portName);
	}

	if (authen == null || Boolean.valueOf(authen).booleanValue() == false) {
	   mailSession = Session.getDefaultInstance(mailProps, null);
	} else {
	   mailSession = Session.getDefaultInstance(mailProps, new Authenticator() {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
		   return new PasswordAuthentication(getContext().getProperty(MailSessionConstants.LocalMailSessionAuthenUser), getContext().getProperty(
			   MailSessionConstants.LocalMailSessionAuthenPwd));
		}
	   });
	}

	return mailSession;
   }

   protected void setContext(final MailSessionContext context) {
	this.context = context;
   }
}
