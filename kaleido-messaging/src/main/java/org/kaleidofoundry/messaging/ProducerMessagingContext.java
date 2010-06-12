package org.kaleidofoundry.messaging;

import java.util.Properties;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Context Connection for Listener Messaging
 * 
 * @author Jerome RADUGET
 */
public class ProducerMessagingContext extends RuntimeContext {

   private static final long serialVersionUID = 9167563020291632514L;

   public ProducerMessagingContext(final String name, final Configuration defaults) {
	super(name, defaults, TransportMessagingConstants.PREFIX_Producer_Property);
   }

   public ProducerMessagingContext(final String name, final RuntimeContext context) {
	super(name, context, TransportMessagingConstants.PREFIX_Producer_Property);
   }

   public ProducerMessagingContext(final String name, final Properties defaults) {
	super(name, defaults, TransportMessagingConstants.PREFIX_Producer_Property);
   }

   public ProducerMessagingContext(final String name) {
	super(name, TransportMessagingConstants.PREFIX_Producer_Property);
   }

}