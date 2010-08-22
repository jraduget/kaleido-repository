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
package org.kaleidofoundry.messaging.jms;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.JmsConfigPath;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.JmsProducerConfigKey;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageBinaryBodyTest;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageParameters;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageXmlBodyTest;

import java.io.Serializable;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.messaging.BinaryMessage;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.ProducerMessagingContext;
import org.kaleidofoundry.messaging.SerializableBeanSample;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.kaleidofoundry.messaging.XmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public class TestJmsProducerMessaging {

   public static final Logger LOGGER = LoggerFactory.getLogger(TestJmsProducerMessaging.class);

   private JmsProducerMessaging jmsProducer;

   @Before
   protected void setUp() throws Exception {

	Configuration jmsConfig = null;
	ProducerMessagingContext publisherContext = null;

	// Reliable config test
	// **********************
	// search and load config
	jmsConfig = ConfigurationFactory.provides("jmsProducer", JmsConfigPath, new RuntimeContext<Configuration>());
	jmsConfig.load();
	// context config for producer
	publisherContext = new ProducerMessagingContext(JmsProducerConfigKey, jmsConfig);
	// Producer instanciation
	jmsProducer = new JmsProducerMessaging(publisherContext);
   }

   @After
   protected void tearDown() throws Exception {

   }

   /**
    * Publisher test envoie message javaBean serializeable
    */
   @Test
   public void testSimpleJavaBeanPublisher() {

	final Serializable javaBean = new SerializableBeanSample();
	final JavaBeanMessage javaBeanMsg = new JavaBeanMessage();

	javaBeanMsg.setJavaBean(javaBean);
	sendMessage(javaBeanMsg);
   }

   /**
    * Publisher test envoie message xml
    */
   @Test
   public void testSimpleXmlPublisher() {

	XmlMessage xmlMsg = null;
	Document document = null;

	try {
	   document = DocumentHelper.parseText(MessageXmlBodyTest);
	   xmlMsg = new XmlMessage(document, MessageParameters);
	   xmlMsg.setDocument(document);
	   sendMessage(xmlMsg);
	} catch (final DocumentException doce) {
	   LOGGER.error("dom4j DocumentException", doce);
	   fail("dom4j DocumentException");
	   doce.printStackTrace();
	}
   }

   /**
    * Publisher test envoie message xml
    */
   @Test
   public void testSimpleBinaryPublisher() {

	final BinaryMessage binaryMsg = new BinaryMessage(MessageParameters);
	binaryMsg.setBinary(MessageBinaryBodyTest);
	sendMessage(binaryMsg);
   }

   private void sendMessage(final Message msg) {

	assertNotNull(jmsProducer);

	try {
	   jmsProducer.sendMessage(msg);
	} catch (final TransportMessagingException tmse) {
	   LOGGER.error("jmsProducer.sendMessage", tmse);
	   fail("jmsProducer.sendMessage");
	   tmse.printStackTrace();
	}
   }

}
