package org.kaleidofoundry.messaging;

import java.util.Map;

/**
 * Binary Message for TransportMessage
 * 
 * @author Jerome RADUGET
 */
public class BinaryMessage extends AbstractMessage implements Message {

   private byte[] binary;

   public BinaryMessage() {
   }

   public BinaryMessage(final Map<String, Object> parameters) {
	super(parameters);
   }

   public BinaryMessage(final byte[] binary, final Map<String, Object> parameters) {
	super(parameters);
	setBinary(binary);
   }

   public byte[] getBinary() {
	return binary;
   }

   public void setBinary(final byte[] binary) {
	this.binary = binary;
   }

   public MessageTypeEnum getType() {
	return MessageTypeEnum.Binary;
   }

   @Override
   public String toString() {
	if (binary != null)
	   return super.toString() + "\n" + new String(binary);
	else
	   return super.toString() + "\nnull";
   }
}
