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

import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_CERTIFIED_CONFIG_PATH;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_CONFIG_CONSUMER_KEY;
import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_CONFIG_PRODUCER_KEY;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

/**
 * Tibco RDV certified test for producer and consumer
 * 
 * @author Jerome RADUGET
 */
@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "rdvCertifiedConfiguration", uri = RDV_CERTIFIED_CONFIG_PATH)
@Ignore
public class RdvCertifiedMessagingTest extends AbstractMessagingTest {

   @BeforeClass
   public static void setupStatic() throws Exception {

	// Producer
	producer = new RdvProducer(new RuntimeContext<Producer>(RDV_CONFIG_PRODUCER_KEY, Producer.class));

	// Consumer which will do the assertions
	consumer = new RdvConsumer(new RuntimeContext<Consumer>(RDV_CONFIG_CONSUMER_KEY, Consumer.class));
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
   }

}
