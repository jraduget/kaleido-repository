package org.kaleidofoundry.messaging;

import java.util.Map;

import org.dom4j.Document;

/**
 * Xml Message by TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public class XmlMessage extends AbstractMessage implements Message {

   private Document document;

   public XmlMessage() {
   }

   public XmlMessage(final Map<String, Object> parameters) {
	setParameters(parameters);
   }

   public XmlMessage(final Document document, final Map<String, Object> parameters) {
	setDocument(document);
	setParameters(parameters);
   }

   public Document getDocument() {
	return this.document;
   }

   public void setDocument(final Document document) {
	this.document = document;
   }

   public MessageTypeEnum getType() {
	return MessageTypeEnum.Xml;
   }

   public String toXml() {
	if (document != null)
	   return document.asXML();
	else
	   return null;
   }

   @Override
   public String toString() {
	if (getDocument() != null)
	   return super.toString() + "\n" + getDocument().asXML();
	else
	   return super.toString() + "\nnull";
   }

}
