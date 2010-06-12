/*
 * $License$
 */
package org.kaleidofoundry.mail;

/**
 * @author Jerome RADUGET
 */
public interface MailTestConstants {

   public static final String FROM_ADRESS = "integration.test@kaleido.org";
   public static final String TO_ADRESS = "integration.test@kaleido.org";
   public static final String CC_ADRESS = "jerome.raduget@gmail.com";

   public static final String MAIL_SUBJECT = "Kaleido integration test subject";
   public static final String MAIL_BODY_HTML = "<b>Kaleido integration test body</b><br/>Hello world!";

   /** Fichier de configuration utilisé */
   public static final String CONFIG_RESOURCE = "classpath:/org/kaleidofoundry/mail/mailSession.properties";

   /** Nom de context à utiliser pour récupérer les config de la session mail */
   public static final String LOCAL_MAIL_CONTEXT_NAME = "kaleido-local";

}
