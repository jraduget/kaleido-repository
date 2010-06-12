package org.kaleidofoundry.messaging;

import java.io.Serializable;
import java.util.Map;

/**
 * JavaBean Message for TransportMessage
 * 
 * @author Jerome RADUGET
 */
public class JavaBeanMessage extends AbstractMessage implements Message {

   private Serializable javaBean;

   public JavaBeanMessage() {
   }

   public JavaBeanMessage(final Map<String, Object> parameters) {
	super(parameters);
   }

   public Serializable getJavaBean() {
	return javaBean;
   }

   public void setJavaBean(final Serializable javaBean) {
	this.javaBean = javaBean;
   }

   public MessageTypeEnum getType() {
	return MessageTypeEnum.JavaBean;
   }

   @Override
   public String toString() {
	if (javaBean != null)
	   return javaBean.toString();
	else
	   return null;
   }
}
