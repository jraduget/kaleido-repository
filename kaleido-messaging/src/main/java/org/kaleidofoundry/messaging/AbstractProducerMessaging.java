package org.kaleidofoundry.messaging;

/**
 * Implémentation abstraite de ConsumerMessaging
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractProducerMessaging implements ProducerMessaging {

   private ProducerMessagingContext context;
   private TransportMessaging transport;

   /**
    * @param context
    */
   public AbstractProducerMessaging(final ProducerMessagingContext context) {
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
   public ProducerMessagingContext getContext() {
	return context;
   }

   protected void setContext(final ProducerMessagingContext context) {
	this.context = context;
   }

}
