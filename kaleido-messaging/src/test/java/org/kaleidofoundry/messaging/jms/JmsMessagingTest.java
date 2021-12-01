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
package org.kaleidofoundry.messaging.jms;

import static org.kaleidofoundry.messaging.MessagingConstantsTests.JMS_CONFIG_CONSUMER_KEY;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.JMS_CONFIG_PATH;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.JMS_CONFIG_PRODUCER_KEY;

import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import org.kaleidofoundry.messaging.AbstractMessagingTest;
import org.kaleidofoundry.messaging.Consumer;
import org.kaleidofoundry.messaging.Message;
import org.kaleidofoundry.messaging.MessageException;
import org.kaleidofoundry.messaging.MessageHandler;
import org.kaleidofoundry.messaging.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "jmsConfig", uri = JMS_CONFIG_PATH)
public class JmsMessagingTest extends AbstractMessagingTest {

   static final Logger LOGGER = LoggerFactory.getLogger(JmsMessagingTest.class);

   /** Active MQ embedded broker */
   static BrokerService ActiveMqBroker;

   @BeforeClass
   public static void setupStatic() throws Exception {

	// Manually configure a embedded active MQ instance
	ActiveMqBroker = new BrokerService();
	ActiveMqBroker.setPersistent(false);
	ActiveMqBroker.start();

	// Producer
	producer = new JmsProducer(new RuntimeContext<Producer>(JMS_CONFIG_PRODUCER_KEY, Producer.class));

	// Consumer which will do the assertions
	consumer = new JmsConsumer(new RuntimeContext<Consumer>(JMS_CONFIG_CONSUMER_KEY, Consumer.class));
	consumer.addMessageHandler(new MessageHandler() {

	   @Override
	   public boolean onReceive(Message message) throws MessageException {
		RECEIVED_MESSAGES.put(uniqueMessageId(message.getProviderId()), message);
		return true;
	   }

	   @Override
	   public void onError(Message message, Throwable th) {
		th.printStackTrace();
	   }
	});

	consumer.start();
   }

   @AfterClass
   public static void cleanupStatic() throws Exception {
	if (consumer != null) {
	   consumer.stop();
	}
	if (ActiveMqBroker != null) {
	   ActiveMqBroker.stop();
	}
   }
 
}
