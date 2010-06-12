package org.kaleidofoundry.messaging;

/**
 * Interface TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public interface TransportMessaging {

   /**
    * @return Code unique descrivant le transport
    */
   String getCode();

   /**
    * @return Version de l'implementation du transport
    */
   String getVersion();

   /**
    * @return Contexte de connection au Transport
    */
   TransportMessagingContext getContext();

   /**
    * Fermeture et libération des ressources
    * 
    * @throws TransportMessagingException
    */
   void close() throws TransportMessagingException;

   /**
    * @return Transport drive xml messages
    */
   boolean isXmlMessage();

   /**
    * @return Transport drive binary messages
    */
   boolean isBinaryMessage();
}
