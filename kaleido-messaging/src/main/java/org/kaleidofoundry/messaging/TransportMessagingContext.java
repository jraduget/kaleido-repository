package org.kaleidofoundry.messaging;

import static org.kaleidofoundry.messaging.MessageTypeEnum.Binary;
import static org.kaleidofoundry.messaging.MessageTypeEnum.JavaBean;
import static org.kaleidofoundry.messaging.MessageTypeEnum.Xml;

import java.util.Properties;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Context Connection for TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public class TransportMessagingContext extends RuntimeContext {

   private static final long serialVersionUID = 9167563020291632514L;

   public TransportMessagingContext(final String name, final Configuration defaults) {
	super(name, defaults, TransportMessagingConstants.PREFIX_Transport_Property);
   }

   public TransportMessagingContext(final String name, final RuntimeContext context) {
	super(name, context, TransportMessagingConstants.PREFIX_Transport_Property);
   }

   public TransportMessagingContext(final String name, final Properties defaults) {
	super(name, defaults, TransportMessagingConstants.PREFIX_Transport_Property);
   }

   public TransportMessagingContext(final String name) {
	super(name, TransportMessagingConstants.PREFIX_Transport_Property);
   }

   /**
    * @return Type de message
    */
   public boolean isXmlMessage() {
	return getProperty(TransportMessagingConstants.SUFFIX_MessageType_Property).equalsIgnoreCase(Xml.getCode());
   }

   /**
    * @return Type de message
    */
   public boolean isBinaryMessage() {
	return getProperty(TransportMessagingConstants.SUFFIX_MessageType_Property).equalsIgnoreCase(Binary.getCode());
   }

   /**
    * @return Type de message
    */
   public boolean isJavaBeanMessage() {
	return getProperty(TransportMessagingConstants.SUFFIX_MessageType_Property).equalsIgnoreCase(JavaBean.getCode());
   }
}