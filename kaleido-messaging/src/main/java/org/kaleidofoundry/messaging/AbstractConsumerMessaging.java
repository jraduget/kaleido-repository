package org.kaleidofoundry.messaging;

/**
 * Implémentation abstraite de ConsumerMessaging
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractConsumerMessaging implements ConsumerMessaging {

   private ConsumerMessagingContext context;
   private TransportMessaging transport;

   /**
    * @param context
    */
   public AbstractConsumerMessaging(final ConsumerMessagingContext context) {
	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.ClientMessaging#getTransport()
    */
   public TransportMessaging getTransport() throws TransportMessagingException {
	return transport;
   }

   protected void setTransport(final TransportMessaging transport) throws TransportMessagingException {
	this.transport = transport;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.ClientMessaging#getContext()
    */
   public ConsumerMessagingContext getContext() {
	return context;
   }

   protected void setContext(final ConsumerMessagingContext context) {
	this.context = context;
   }

}
