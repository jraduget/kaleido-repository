package org.kaleidofoundry.mail.sender.ejb;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;

/**
 * Remote interface
 */
public interface EjbMailSender extends EJBObject {

   /**
    * Envoi d'un Message
    * 
    * @param message
    * @throws MailException
    * @throws AddressException
    * @throws MessagingException
    * @throws RemoteException
    */
   void send(MailMessage message) throws MailException, AddressException, MessagingException, RemoteException;

}