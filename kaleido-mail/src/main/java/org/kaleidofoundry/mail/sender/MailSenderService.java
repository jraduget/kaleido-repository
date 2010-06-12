/*
 * $License$
 */
package org.kaleidofoundry.mail.sender;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.session.MailSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Interface de service pour envoyer un message mail.
 * </p>
 * <p>
 * <a href="package-summary.html"/>Voir la description du package</a>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public interface MailSenderService {

   /** Logger par défaut */
   public static final Logger logger = LoggerFactory.getLogger(MailSenderService.class);

   /** 
    * @return Création d'un nouveaux message à envoyer 
    */
   MailMessage createMessage();

   /**
    * Envoie d'un message mail
    * 
    * @param message
    * @throws MailException Si Problème d'obtention d'une Session mail
    * @throws AddressException Si une des urls est invalide
    * @throws MessagingException Si Problème lors de la construction du message
    */
   void send(MailMessage message) throws MailException, AddressException, MessagingException;

   /**
    * @return Contexte nécessaire pour obtenir une Session utilisée pour envoyer un mail
    */
   MailSessionContext getSessionContext();

}
