/*
 * $License$
 */
package org.kaleidofoundry.mail.session;

import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Interface de Service fourniseur d'une Session Mail Smtp utilisée pour envoyer des mails
 * </p>
 * <p>
 * <a href="package-summary.html"/>Voir la description du package</a>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public interface MailSessionService {

   /** Logger */
   static final Logger LOGGER = LoggerFactory.getLogger(MailSessionService.class);

   /**
    * @return Contexte de connection
    */
   MailSessionContext getContext();

   /**
    * @return Session javaMail pour envoi de mail
    * @throws MailSessionException
    */
   Session createSession() throws MailSessionException;

}
