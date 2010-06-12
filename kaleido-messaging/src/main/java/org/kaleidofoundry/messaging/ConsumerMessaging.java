package org.kaleidofoundry.messaging;

/**
 * Ancêtre pour consomateur de message asynchrone
 * 
 * @author Jerome RADUGET
 */
public interface ConsumerMessaging extends ClientMessaging {

   /**
    * @param message
    * @throws TransportMessagingException
    */
   void onMessageReceived(Message message) throws TransportMessagingException;

   /**
    * Gestion d'erreur eventuel à la reception
    * 
    * @param th
    */
   void onMessageReceivedError(Throwable th);

   /**
    * Stoppe l'écoute
    * 
    * @throws TransportMessagingException
    */
   void stop() throws TransportMessagingException;
}
