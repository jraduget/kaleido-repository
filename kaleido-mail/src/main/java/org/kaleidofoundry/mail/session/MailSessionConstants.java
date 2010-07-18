/*  
 * Copyright 2008-2010 the original author or authors 
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
package org.kaleidofoundry.mail.session;

/**
 * MailSession Constantes
 * 
 * @author Jerome RADUGET
 */
public interface MailSessionConstants {

   /** Security Provider par défaut en cas d'utilisation du SSL */
   public final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
   /** Port d'envoi SMTP utilisé par défaut */
   public final static int DEFAULT_SMTP_PORT = 25;
   /** Nom de la racine des propriétés du contexte énumérant les différents MailSessions */
   public static final String LocalRootProperty = "mailsession";

   /** Nom de la propriété du contexte contenant le type d'implementation */
   public static final String TypeProperty = "type";

   /** Type d'implementation Locale */
   public static final String LocalMailSessionType = "local";
   /** Nom de la propriété du contexte, spécifiant le nom du serveur d'envoi */
   public static final String LocalMailSessionHost = "mail.smtp.host";
   /** Nom de la propriété du contexte, spécifiant le port du serveur pour envoi */
   public static final String LocalMailSessionPort = "mail.smtp.port";
   /** Nom de la propriété du contexte, spécifiant si l'authentification est activée ou non true / false */
   public static final String LocalMailSessionAuthen = "mail.smtp.auth";
   /** Nom de la propriété du contexte, spécifiant le nom d'utilisateur si l'authentification est activée */
   public static final String LocalMailSessionAuthenUser = "mail.smtp.auth.user";
   /** Nom de la propriété du contexte, spécifiant le mot de passe si l'authentification est activée */
   public static final String LocalMailSessionAuthenPwd = "mail.smtp.auth.pwd";

   // SMTP SSL
   /** Nom de la propriété du contexte, spécifiant la class socketFactory à utiliser pour le SSL */
   public static final String LocalMailSessionSocketFactoryClass = "mail.smtp.socketFactory.class";
   /** Nom de la propriété du contexte, spécifiant le fallback à utiliser avec le socketFactory SSL */
   public static final String LocalMailSessionSocketFactoryFallBack = "mail.smtp.socketFactory.fallback";
   /** Nom de la propriété du contexte, spécifiant le numéro de port a utiliser pour le SSL */
   public static final String LocalMailSessionSocketFactoryPort = "mail.smtp.socketFactory.port";

   /** Type d'implementation Jndi */
   public static final String JndiMailSessionType = "jndi";
   /** Nom de la propriété du contexte, donnant la référence du nom de contexte jndi à utiliser */
   public static final String JndiContextNameProperty = "jndi.context.local-ref";
   /** Nom de la propriété du contexte, donnant le nom jndi de la sessionFactory Jndi */
   public static final String JndiSessionNameProperty = "jndi.name";

}
