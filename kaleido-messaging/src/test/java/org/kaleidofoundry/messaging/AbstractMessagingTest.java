/*  
 * Copyright 2008-2014 the original author or authors 
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.MESSAGE_BINARY_TEST;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.MESSAGE_TEXT_BODY_TEST;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.MESSAGE_XML_BODY_TEST;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.buildParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Test;

/**
 * @author jraduget
 */
public abstract class AbstractMessagingTest {

   /** Producer to test */
   protected static Producer producer;
   /** Consumer to test */
   protected static Consumer consumer;

   /** received messages by provider id */
   protected static final Map<String, Message> RECEIVED_MESSAGES = new HashMap<String, Message>();

   @Test
   public void javaBeanMessage() throws InterruptedException, MessagingException {
	_javaBeanMessage(-1);
   }
   
   @Test
   public void javaBeanMessageWithTimeout() throws InterruptedException, MessagingException {
	_javaBeanMessage(3000);
   }
   
   protected void _javaBeanMessage(long timeout) throws InterruptedException, MessagingException {
	Map<String, Object> parameters = buildParameters(new HashMap<String, Object>());
	parameters.put("kind", "bean");

	Message message = new JavaBeanMessage(new SerializableBeanSample(), buildParameters(parameters));
	sendMessageWithAssertions(message, timeout);

	message = RECEIVED_MESSAGES.get(uniqueMessageId(message.getProviderId()));
	assertNotNull(message);
	assertEquals(MessageTypeEnum.JavaBean, message.getType());
	assertNotNull(((JavaBeanMessage) message).getJavaBean());
	assertTrue(((JavaBeanMessage) message).getJavaBean() instanceof SerializableBeanSample);
	SerializableBeanSample bean = (SerializableBeanSample) ((JavaBeanMessage) message).getJavaBean();
	assertEquals(new SerializableBeanSample().getName(), bean.getName());
	assertEquals(new SerializableBeanSample().getFirstName(), bean.getFirstName());
	assertEquals(new SerializableBeanSample().getAge(), bean.getAge());
	assertEquals(new SerializableBeanSample().getBirthDate(), bean.getBirthDate());
	
	parametersCommonAssertions(message.getParameters());
	assertTrue(message.getParameters().containsKey("kind"));
	assertEquals("bean", message.getParameters().get("kind"));		
   }

   @Test
   public void xmlMessage() throws InterruptedException, MessagingException {
	_xmlMessage(-1);
   }
   
   @Test
   public void xmlMessageWithTimeout() throws InterruptedException, MessagingException {
	_xmlMessage(3000);
   }   
   
   protected void _xmlMessage(long timeout) throws InterruptedException, MessagingException {
	Map<String, Object> parameters = buildParameters(new HashMap<String, Object>());
	parameters.put("kind", "xml");
	Message message = new XmlMessage(MESSAGE_XML_BODY_TEST, buildParameters(parameters));
	sendMessageWithAssertions(message, timeout);

	message = RECEIVED_MESSAGES.get(uniqueMessageId(message.getProviderId()));
	assertNotNull(message);
	assertEquals(MessageTypeEnum.Xml, message.getType());
	assertEquals(MESSAGE_XML_BODY_TEST, ((XmlMessage) message).toXml());
	
	parametersCommonAssertions(message.getParameters());
	assertTrue(message.getParameters().containsKey("kind"));
	assertEquals("xml", message.getParameters().get("kind"));	
   }

   @Test
   public void bytesMessage() throws InterruptedException, MessagingException {
	_bytesMessage(-1);
   }
   
   @Test
   public void bytesMessageWithTimeout() throws InterruptedException, MessagingException {
	_bytesMessage(3000);
   }
   
   protected void _bytesMessage(long timeout) throws InterruptedException, MessagingException {
	Map<String, Object> parameters = buildParameters(new HashMap<String, Object>());
	parameters.put("kind", "bytes");
	Message message = new BytesMessage(MESSAGE_BINARY_TEST, buildParameters(parameters));
	sendMessageWithAssertions(message, timeout);

	message = RECEIVED_MESSAGES.get(uniqueMessageId(message.getProviderId()));
	assertNotNull(message);
	assertEquals(MessageTypeEnum.Bytes, message.getType());
	assertArrayEquals(MESSAGE_BINARY_TEST, ((BytesMessage) message).getBytes());
	
	parametersCommonAssertions(message.getParameters());
	assertTrue(message.getParameters().containsKey("kind"));
	assertEquals("bytes", message.getParameters().get("kind"));
   }

   @Test
   public void textMessage() throws InterruptedException, MessagingException {
	_textMessage(-1);
   }
   
   @Test
   public void textMessageWithTimeout() throws InterruptedException, MessagingException {
	_textMessage(3000);
   }   
   
   protected void _textMessage(long timeout) throws InterruptedException, MessagingException {
	Map<String, Object> parameters = buildParameters(new HashMap<String, Object>());
	parameters.put("kind", "text");
	Message message = new TextMessage(MESSAGE_TEXT_BODY_TEST, buildParameters(parameters));
	sendMessageWithAssertions(message, timeout);

	message = RECEIVED_MESSAGES.get(uniqueMessageId(message.getProviderId()));
	assertNotNull(message);
	assertEquals(MessageTypeEnum.Text, message.getType());
	assertEquals(MESSAGE_TEXT_BODY_TEST, ((TextMessage) message).getText());
	
	parametersCommonAssertions(message.getParameters());
	assertTrue(message.getParameters().containsKey("kind"));
	assertEquals("text", message.getParameters().get("kind"));
   }

   private void sendMessageWithAssertions(final Message msg, long timeout) throws InterruptedException, MessagingException {
	assertNotNull(producer);
	if (timeout <=0) {
	   producer.send(msg);
	} else {
	   producer.send(msg, timeout);
	}
	assertNotNull(msg.getProviderId());

	// wait the message consummation
	Thread.sleep(3000);
   }

   private void parametersCommonAssertions(Map<String, Object> parameters) {
	assertTrue(parameters.containsKey("paramStr"));
	assertEquals("strValue", parameters.get("paramStr"));
	assertTrue(parameters.containsKey("paramDate"));
	assertEquals("2010-03-06T00:00:00.000", parameters.get("paramDate"));
	assertTrue(parameters.containsKey("paramCalendar"));
	assertEquals("2012-03-06T00:00:00.000", parameters.get("paramCalendar"));
	assertTrue(parameters.containsKey("paramInt"));
	assertEquals(5, parameters.get("paramInt"));
   }
   
   
   /** provider unique id */
   protected static String uniqueMessageId(String providerId) {
	StringTokenizer strTokens = new StringTokenizer(providerId, ":");
	String result = null;

	if (strTokens.hasMoreTokens()) {
	   strTokens.nextToken();
	}

	if (strTokens.hasMoreTokens()) {
	   result = strTokens.nextToken();
	}
	return result;
   }   

}
