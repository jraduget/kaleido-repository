package org.kaleidofoundry.messaging.jms;

/**
 * JMS Transport Constants
 * 
 * @author Jerome RADUGET
 */
public interface JmsTransportConstants {

   /** Chemin donnant le nom jndi utiliser pour obtenir la connectionFactory */
   public static final String PATH_KEY_ConnectionFactoryName = "jms.connection.factory.jndi.name";
   /** Chemin donnant le nom du contexte jndi (son lien local) à utiliser pour obtenir la connectionFactory */
   public static final String PATH_KEY_ConnectionFactoryLocalRef = "jms.connection.factory.jndi.context.local-ref";
   /** Chemin donnant le nom d'utilisateur spécifique pour l'instanciation de la connectionFactory */
   public static final String PATH_KEY_ConnectionFactoryUser = "jms.connection.factory.user";
   /** Chemin donnant le mot de passe spécifique pour l'instanciation de la connectionFactory */
   public static final String PATH_KEY_ConnectionFactoryPassword = "jms.connection.factory.password";

   /** Chemin propriété pour avoir le service RDV */
   public static final String PATH_KEY_SessionTransacted = "jms.session.transacted";

   /** Chemin propriété pour avoir le service RDV */
   public static final String PATH_KEY_AcknowledgeMode = "jms.session.acknowledgeMode";

   /**
    * 0 (lower) - 9 (highest)
    * 0-4 as gradations of normal priority
    * 5-9 gradations of expedited priority.
    * 
    * @see javax.jms.Message.getJMSPriority()
    */
   public static final String PATH_KEY_MessagePriority = "message.priority";
   /**
    * 0 : no message expiration
    * 
    * @see javax.jms.Message.getJMSExpiration()
    */
   public static final String PATH_KEY_MessageExpiration = "message.expiration";

   /** Liste des destinations vers lequelles ont envoi */
   public static final String PATH_KEY_Destinations = "jms.destinations";
   /** Separateur entre les destinations */
   public static final String Destinations_Separator = ";";

   /** Chemin donnant le nom jndi de la destination (queue ou topic) */
   public static final String PATH_KEY_DestinationJndiName = "jms.destination.?.jndi.name";
   /** Chemin donnant le contexte jndi de la destination */
   public static final String PATH_KEY_DestinationJndiContext = "jms.destination.?.jndi.context.local-ref";

   /** async , sync */
   public static final String PATH_KEY_MessageConsumption = "jms.message.consumption";

   /** Full date format for date handling in message */
   public static final String MESSAGE_FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
