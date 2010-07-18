/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
