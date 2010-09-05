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
package org.kaleidofoundry.messaging.rdv;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageBinaryBodyTest;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageParameters;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageToSendInTest;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.MessageXmlBodyTest;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.TimeToSleepWaitingMessage;

import java.io.Serializable;

import junit.framework.AssertionFailedError;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.messaging.BinaryMessage;
import org.kaleidofoundry.messaging.ConsumerMessagingContext;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.MessageTypeEnum;
import org.kaleidofoundry.messaging.ProducerMessagingContext;
import org.kaleidofoundry.messaging.SerializableBeanSample;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.kaleidofoundry.messaging.XmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibco.tibrv.TibrvCmListener;
import com.tibco.tibrv.TibrvListener;

/**
 * @author Jerome RADUGET
 */
@Ignore
public class AbstractRdvConsumerMessaging {

   private final String configPath;
   private final String consumerConfigKey;
   private final String producerConfigKey;

   private RdvConsumerSample rdvConsumer;
   private RdvProducerMessaging rdvProducer;

   private static boolean sendMessageDataOK = true;

   public AbstractRdvConsumerMessaging(final String configPath, final String consumerConfigKey, final String producerConfigKey) {
	this.configPath = configPath;
	this.consumerConfigKey = consumerConfigKey;
	this.producerConfigKey = producerConfigKey;
   }

   @Before
   protected void setUp() throws Exception {

	Configuration config = null;
	ConsumerMessagingContext listenerContext = null;
	ProducerMessagingContext publisherContext = null;

	// search and load config
	config = ConfigurationFactory.provides("rdvConsumer", configPath, new RuntimeContext<Configuration>());
	config.load();

	// context config for listener
	listenerContext = new ConsumerMessagingContext(consumerConfigKey, config);
	// listener instanciation
	rdvConsumer = new RdvConsumerSample(listenerContext);

	// context config for listener
	publisherContext = new ProducerMessagingContext(producerConfigKey, config);
	// listener instanciation
	rdvProducer = new RdvProducerMessaging(publisherContext);
   }

   @After
   protected void tearDown() throws Exception {
	// listeners stopping
	rdvConsumer.stop();
   }

   /**
    * Consumer bien instancié et actif
    */
   @Test
   public void testRdvConsumer() {

	assertNotNull(rdvConsumer);

	if (RdvTransportTypeEnum.RELIABLE.equals(rdvConsumer.getTransport().getType())) {
	   for (final TibrvListener listener : rdvConsumer.getRdvListenerList()) {
		assertTrue(listener.isValid());
	   }
	}

	if (RdvTransportTypeEnum.CERTIFIED.equals(rdvConsumer.getTransport().getType())) {
	   for (final TibrvCmListener listener : rdvConsumer.getRdvCmListenerList()) {
		assertTrue(listener.isValid());
	   }
	}
   }

   /**
    * Publisher test envoie message javaBean serializeable
    */
   @Test
   public void testSimpleJavaBeanProducer() {

	final Serializable javaBean = new SerializableBeanSample();
	final JavaBeanMessage javaBeanMsg = new JavaBeanMessage();

	javaBeanMsg.setJavaBean(javaBean);
	sendMessage(javaBeanMsg);
   }

   /**
    * Producer test envoie message xml
    */
   @Test
   public void testSimpleXmlProducer() {

	XmlMessage xmlMsg = null;
	Document document = null;

	try {
	   document = DocumentHelper.parseText(MessageXmlBodyTest);
	   xmlMsg = new XmlMessage(document, MessageParameters);
	   xmlMsg.setDocument(document);
	   sendMessage(xmlMsg);
	} catch (final DocumentException doce) {
	   fail("dom4j DocumentException");
	   doce.printStackTrace();
	}
   }

   /**
    * Producer test envoie message xml
    */
   @Test
   public void testSimpleBinaryProducer() {

	final BinaryMessage binaryMsg = new BinaryMessage(MessageParameters);
	binaryMsg.setBinary(MessageBinaryBodyTest);
	sendMessage(binaryMsg);
   }

   /**
    * Producer test envoie message xml
    */
   @Test
   public void testMultipleMessageProducer() {

	assertNotNull(rdvProducer);

	for (int i = 0; i < MessageToSendInTest; i++) {

	   XmlMessage xmlMsg = null;

	   try {
		final Document document = DocumentHelper.parseText(MessageXmlBodyTest);
		xmlMsg = new XmlMessage(document, MessageParameters);
		xmlMsg.setDocument(document);
		rdvProducer.sendMessage(xmlMsg);
	   } catch (final TransportMessagingException tme) {
		fail("rdvProducer.sendMessage");
		tme.printStackTrace();
	   } catch (final DocumentException doce) {
		fail("dom4j DocumentException");
		doce.printStackTrace();
	   }
	}

	try {
	   Thread.sleep(TimeToSleepWaitingMessage * 1000);
	} catch (final InterruptedException ite) {
	}

	// compare message send, message received
	assertEquals("messages not received by listener in a valid time...", MessageToSendInTest, rdvConsumer.getMessageCount());
	// count message in error
	assertEquals(rdvConsumer.getMessageCountError() + "messages received by listener in error...", 0, rdvConsumer.getMessageCountError());
   }

   /**
    * All messages received by listener sample, have right value
    */
   @Test
   public void testSendMessageDataOK() {
	assertTrue("one of messages received, does not match expected message.", sendMessageDataOK);
   }

   private void sendMessage(final Message msg) {

	assertNotNull(rdvProducer);

	try {
	   rdvProducer.sendMessage(msg);
	} catch (final TransportMessagingException tmse) {
	   fail("rdvProducer.sendMessage");
	   tmse.printStackTrace();
	}

	try {
	   Thread.sleep(TimeToSleepWaitingMessage * 1000);
	} catch (final InterruptedException ite) {
	}
   }

   // Listener Inner Class for test
   public static class RdvConsumerSample extends RdvConsumerMessaging {

	public final static Logger LOGGER = LoggerFactory.getLogger(RdvConsumerSample.class);

	private int messageCount;
	private int messageCountError;

	public RdvConsumerSample(final ConsumerMessagingContext context) throws TransportMessagingException {
	   super(context);
	   messageCount = 0;
	   messageCountError = 0;
	}

	public void onMessageReceived(final Message message) throws TransportMessagingException {
	   messageCount++;
	   // LOGGER.info("message received : " + message != null ? message.toString() : "null");
	   LOGGER.info("message received : " + (message != null ? message.getClass().getName() : "null") + "[" + messageCount + "]");

	   boolean localTestOk = false;

	   try {

		if (MessageTypeEnum.Xml.equals(message.getType())) {
		   try {
			final XmlMessage xmlMessage = ((XmlMessage) message);
			final Document document = DocumentHelper.parseText(MessageXmlBodyTest);
			assertEquals("XmlMessage body not equals", document.asXML(), xmlMessage.getDocument().asXML());
			localTestOk = true;
		   } catch (final DocumentException doce) {
			fail(doce.getMessage());
		   }
		}

		if (MessageTypeEnum.JavaBean.equals(message.getType())) {
		   final JavaBeanMessage javaBeanMessage = ((JavaBeanMessage) message);
		   final SerializableBeanSample sample = new SerializableBeanSample();
		   assertEquals("JavaBeanMessage content not equals", sample, javaBeanMessage.getJavaBean());
		   localTestOk = true;
		}

		if (MessageTypeEnum.Binary.equals(message.getType())) {
		   final BinaryMessage binaryMessage = ((BinaryMessage) message);
		   assertEquals("BinaryMessage content not equals", new String(MessageBinaryBodyTest), new String(binaryMessage.getBinary()));
		   localTestOk = true;
		}

	   } catch (final AssertionFailedError afe) {
		LOGGER.info(afe.getMessage());
		LOGGER.info("message information : " + message.toString());
	   } finally {
		// none assertion error ?
		sendMessageDataOK = sendMessageDataOK && localTestOk;
	   }
	}

	/*
	 * (non-Javadoc)
	 * @see org.kaleidofoundry.messaging.ConsumerMessaging#onMessageReceivedError(java.lang.Throwable)
	 */
	public void onMessageReceivedError(final Throwable th) {
	   messageCountError++;
	   LOGGER.error("message received error", th);
	}

	/**
	 * @return Count total message received
	 */
	public int getMessageCount() {
	   return messageCount;
	}

	/**
	 * @return Count message in error
	 */
	public int getMessageCountError() {
	   return messageCountError;
	}

   }
}
