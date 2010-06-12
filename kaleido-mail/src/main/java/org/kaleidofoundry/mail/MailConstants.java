package org.kaleidofoundry.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public interface MailConstants {

   /** Logger */
   static final Logger LOGGER = LoggerFactory.getLogger(MailMessage.class);

   /** Configuration ressource bundle */
   static final String I18nRessource = "i18n/mail/message";

}
