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
package org.kaleidofoundry.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Interface d'un MailMessage. Mod�lisation java bean d'un Mail.
 * </p>
 * <p>
 * <a href="package-summary.html"/>Voir la description du package</a>
 * </p>
 * 
 * @author jraduget
 */
public interface MailMessage extends Serializable, Cloneable {

   /**
    * @return mail subject
    */
   String getSubject();

   /**
    * @return mailboty
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
    * @return R�cup�re l'importance d'envoie des message - A v�rifier ci-dessous <li>0 - Normal</li> <li>1 - Urgent</li> <li>4 - Faible</li>
    */
   int getPriority();

   /**
    * @return CharSet utilis� pour encoder le message
    */
   String getCharSet();

   /**
    * @return ContentType du message d�finit dans le header (text brut / html)
    */
   String getContentType();

   /**
    * D�finit le Charset � utiliser. Par d�faut ISO-8859-1 est utilis�
    * 
    * @param charset
    */
   void setCharSet(String charset);

   /**
    * Ajout d'un fichier attach�
    * 
    * @param attachName nom du fichier attach�
    * @param filename Chemin complet avec nom de fichier
    * @throws FileNotFoundException
    * @throws IOException Type mime inconnu
    */
   void addAttachment(String attachName, String filename) throws FileNotFoundException, IOException;

   /**
    * Ajout d'un fichier attach� � partir d'une URL
    * 
    * @param attachName nom du fichier attach�
    * @param fileURL Chemin complet avec nom de fichier
    * @throws IOException Type mime inconnu
    */
   void addAttachment(String attachName, URL fileURL) throws IOException;

   /**
    * Ajout d'un fichier joint
    * @param attach 
    */
   void addAttachment(MailAttachment attach);

   /** D�finit un envoi de message avec HTML en contenu */
   void setBodyContentHtml();

   /** D�finit un envoi de message avec Text brut en contenu */
   void setBodyContentText();

   /**
    * @return Ensemble des noms des pi�ces attach�s
    */
   Set<String> getAttachments();

   /**
    * @return Fichier joint
    * @param attachName
    */
   MailAttachment getAttachmentFilename(String attachName);

   /**
    * D�finit le sujet du message
    * @param subject 
    */
   void setSubject(String subject);

   /**
    * D�finit l'adresse de l'emetteur
    * @param adress 
    */
   void setFromAdress(String adress);

   /**
    * D�finit le corps du message
    * @param content 
    */
   void setContent(String content);

   /**
    * D�finit la priorit� du message
    * 
    * @param priority
    */
   void setPriority(int priority);

   /**
    * @return Clone de l'instance
    */
   MailMessage clone();

}
