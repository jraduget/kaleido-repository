/*  
 * Copyright 2008-2021 the original author or authors 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An xml message, with a {@link Document} accessor
 * 
 * @author jraduget
 */
public class XmlMessage extends TextMessage {

   static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

   static {
	DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
   }

   private final Document document;

   public XmlMessage(final String message) {
	this(null, message, null);
   }

   public XmlMessage(InputStream in, final Map<String, Object> parameters) {
	this(null, in, parameters);
   }

   public XmlMessage(Reader reader, final Map<String, Object> parameters) {
	this(null, reader, parameters);
   }

   public XmlMessage(String message, final Map<String, Object> parameters) {
	this(null, message, parameters);
   }

   public XmlMessage(String correlationId, InputStream in, final Map<String, Object> parameters) {
	super(correlationId, null, parameters);
	try {
	   final DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
	   document = documentBuilder.parse(new InputSource(in));
	} catch (Exception e) {
	   throw handleException(e);
	}
   }

   public XmlMessage(String correlationId, Reader reader, final Map<String, Object> parameters) {
	super(correlationId, null, parameters);
	try {
	   final DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
	   document = documentBuilder.parse(new InputSource(reader));
	} catch (Exception e) {
	   throw handleException(e);
	}
   }

   public XmlMessage(String correlationId, String message, final Map<String, Object> parameters) {
	super(correlationId, message, parameters);
	try {
	   final DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
	   document = documentBuilder.parse(new InputSource(new StringReader(message)));
	} catch (Exception e) {
	   throw handleException(e);
	}
   }

   public XmlMessage(BytesMessage bytesMessage, Charset charset) {
	this(bytesMessage.getCorrelationId(), bytesMessage.toString(charset), bytesMessage.getParameters());
   }

   public XmlMessage(BytesMessage bytesMessage) {
	this(bytesMessage.getCorrelationId(), bytesMessage.toString(), bytesMessage.getParameters());
   }

   public XmlMessage(TextMessage textMessage) {
	this(textMessage.getCorrelationId(), textMessage.getText(), textMessage.getParameters());
   }

   public Document getDocument() {
	return this.document;
   }

   @Override
   public String getText() {
	return toXml();
   }

   public MessageTypeEnum getType() {
	return MessageTypeEnum.Xml;
   }

   public String toXml() {
	return toXml(false);
   }

   public String toXml(boolean formated) {
	if (document != null) {

	   try {
		final Source source = new DOMSource(document);
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);

		// Write the DOM document to the file
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, formated ? "yes" : "no");
		if (formated) {
		   transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		}

		transformer.transform(source, result);
		return writer.toString();

	   } catch (Exception e) {
		throw handleException(e);
	   }
	} else
	   return null;
   }

   @Override
   public String toString() {
	return toXml();
   }

   private IllegalStateException handleException(Exception e) {

	if (e instanceof ParserConfigurationException) {
	   return new IllegalStateException("xml parser configuration problem", e);
	} else if (e instanceof SAXException) {
	   return new IllegalStateException("xml parsing problem", e);
	} else if (e instanceof IOException) {
	   return new IllegalStateException("I/O error parsing xml message", e);
	} else {
	   return new IllegalStateException("unexpected error processing xml message", e);
	}

   }

}
