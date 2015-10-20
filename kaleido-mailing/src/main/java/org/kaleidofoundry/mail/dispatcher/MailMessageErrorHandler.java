package org.kaleidofoundry.mail.dispatcher;

import org.kaleidofoundry.mail.MailMessage;

/**
 * Message error handler interface
 * 
 * @param <E>
 */
public interface MailMessageErrorHandler {
   
   /**
    * @param message
    * @param me
    */
   void process(MailMessage message, Exception e);
}