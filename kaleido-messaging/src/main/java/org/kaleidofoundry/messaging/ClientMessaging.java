package org.kaleidofoundry.messaging;

/**
 * ClientMessaging ancêtre
 * 
 * @author Jerome RADUGET
 */
public interface ClientMessaging {

   /**
    * @return
    * @throws TransportMessagingException
    */
   TransportMessaging getTransport() throws TransportMessagingException;

}
