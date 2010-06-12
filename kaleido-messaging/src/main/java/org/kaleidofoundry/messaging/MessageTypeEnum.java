package org.kaleidofoundry.messaging;

/**
 * Type possible d'un message
 * 
 * @author Jerome RADUGET
 */
public enum MessageTypeEnum {

   /** Xml Message */
   Xml("xml", XmlMessage.class),
   /** Binary Message */
   Binary("binary", BinaryMessage.class),
   /** JavaBean Message */
   JavaBean("javaBean", JavaBeanMessage.class);

   MessageTypeEnum(final String code, final Class<?> implementation) {
	this.code = code;
	this.implementation = implementation;
   }

   private final String code;
   private final Class<?> implementation;

   public Class<?> getImplementation() {
	return implementation;
   }

   public String getCode() {
	return code;
   }

}
