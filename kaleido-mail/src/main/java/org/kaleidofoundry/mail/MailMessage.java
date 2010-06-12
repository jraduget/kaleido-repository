package org.kaleidofoundry.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Interface d'un MailMessage. Modélisation java bean d'un Mail.
 * </p>
 * <p>
 * <a href="package-summary.html"/>Voir la description du package</a>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public interface MailMessage extends Serializable, Cloneable {

   /**
    * @return Sujet du message
    */
   String getSubject();

   /**
    * @return Corps du message
    */
   String getContent();

   /**
    * @return Adresse de l'emetteur
    */
   String getFromAdress();

   /**
    * @return Liste des destinataires TO
    */
   List<String> getToAdress();

   /**
    * @return Liste des destinataires CC
    */
   List<String> getCcAdress();

   /**
    * @return Liste des destinataires CCI
    */
   List<String> getCciAdress();

   /**
    * @return Récupère l'importance d'envoie des message - A vérifier ci-dessous <li>0 - Normal</li> <li>1 - Urgent</li> <li>4 - Faible</li>
    */
   int getPriority();

   /**
    * @return CharSet utilisé pour encoder le message
    */
   String getCharSet();

   /**
    * @return ContentType du message définit dans le header (text brut / html)
    */
   String getContentType();

   /**
    * Définit le Charset à utiliser. Par défaut ISO-8859-1 est utilisé
    * 
    * @param charset
    */
   void setCharSet(String charset);

   /**
    * Ajout d'un fichier attaché
    * 
    * @param attachName nom du fichier attaché
    * @param filename Chemin complet avec nom de fichier
    * @throws FileNotFoundException
    * @throws IOException Type mime inconnu
    */
   void addAttachment(String attachName, String filename) throws FileNotFoundException, IOException;

   /**
    * Ajout d'un fichier attaché à partir d'une URL
    * 
    * @param attachName nom du fichier attaché
    * @param fileURL Chemin complet avec nom de fichier
    * @throws IOException Type mime inconnu
    */
   void addAttachment(String attachName, URL fileURL) throws IOException;

   /**
    * Ajout d'un fichier joint
    * @param attach 
    */
   void addAttachment(MailAttachment attach);

   /** Définit un envoi de message avec HTML en contenu */
   void setBodyContentHtml();

   /** Définit un envoi de message avec Text brut en contenu */
   void setBodyContentText();

   /**
    * @return Ensemble des noms des pièces attachés
    */
   Set<String> getAttachments();

   /**
    * @return Fichier joint
    * @param attachName
    */
   MailAttachment getAttachmentFilename(String attachName);

   /**
    * Définit le sujet du message
    * @param subject 
    */
   void setSubject(String subject);

   /**
    * Définit l'adresse de l'emetteur
    * @param adress 
    */
   void setFromAdress(String adress);

   /**
    * Définit le corps du message
    * @param content 
    */
   void setContent(String content);

   /**
    * Définit la priorité du message
    * 
    * @param priority
    */
   void setPriority(int priority);

   /**
    * @return Clone de l'instance
    */
   MailMessage clone();

}
