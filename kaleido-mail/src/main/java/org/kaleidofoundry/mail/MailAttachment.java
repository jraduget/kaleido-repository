package org.kaleidofoundry.mail;

import java.io.Serializable;
import java.net.URL;

/**
 * Pièce jointe d'un mail.
 * On peut spécifier un chemin système
 * ou
 * une URL java pour localiser la pièce jointe
 * si il est spécifié, le chemin système est utilisé par défaut
 * 
 * @author Jerome RADUGET
 */
public interface MailAttachment extends Serializable {

   /** @return Nom de la pièce jointe */
   String getName();

   /** @return Type mime de la pièce jointe */
   String getContentType();

   /** @return Chemin complet vers la pièce jointe */
   String getContentPath();

   /** @return URL vers la pièce jointe */
   URL getContentURL();

   void setName(String name);

   void setContentType(String contentType);

   void setContentPath(String contentPath);

   void setContentURL(URL contentURL);
}
