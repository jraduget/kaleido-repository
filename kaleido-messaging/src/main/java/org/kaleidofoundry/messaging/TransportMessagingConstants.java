package org.kaleidofoundry.messaging;

/**
 * Constants for TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public interface TransportMessagingConstants {

   /*
    * Constants for context configuration
    * **********************************************************
    */
   /** Prefix for all transport messaging context configuration */
   public static final String PREFIX_Transport_Property = "messaging.transport";

   /** Prefix for all publisher messaging context configuration */
   public static final String PREFIX_Producer_Property = "messaging.producer";

   /** Prefix for all listener messaging context configuration */
   public static final String PREFIX_Listener_Property = "messaging.consumer";

   /** Suffix for path key config, specifying transport (local-ref), for consumer and producer... */
   public static final String SUFFIX_TransportLocalRef_Property = "transport.local-ref";

   /**
    * Kind of message drive by transport :
    * <ul>
    * <li>xml</li>
    * <li>binary</li>
    * </ul>
    */
   public static final String SUFFIX_MessageType_Property = "message";

   /*
    * Constants for message field
    * **********************************************************
    */
   /** Type du message véhiculer */
   public static final String MessageTypeField = "message.type";

   /** Nom de la données du message contenant le corps xml */
   public static final String MessageXmlBodyField = "xml.body";

   /** Nom de la données du message contenant le binaire */
   public static final String MessageBinaryBodyField = "binary.body";
}
