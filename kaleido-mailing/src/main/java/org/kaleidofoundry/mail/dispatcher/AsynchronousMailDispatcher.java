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
package org.kaleidofoundry.mail.dispatcher;

import static org.kaleidofoundry.mail.MailConstants.AsynchronousMailDispatcherPluginName;
import static org.kaleidofoundry.mail.dispatcher.MailDispatcherContextBuilder.PRODUCER_SERVICE_NAME;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.messaging.JavaBeanMessage;
import org.kaleidofoundry.messaging.Producer;
import org.kaleidofoundry.messaging.ProducerFactory;

/**
 * @author jraduget
 */
@Declare(AsynchronousMailDispatcherPluginName)
public class AsynchronousMailDispatcher implements MailDispatcher {

   private final RuntimeContext<MailDispatcher> context;

   private final Producer messageProducer;

   public AsynchronousMailDispatcher(final RuntimeContext<MailDispatcher> context) {
	this.context = context;
	final String messageProducereRef = context.getString(PRODUCER_SERVICE_NAME);
	final RuntimeContext<Producer> producerContext = new RuntimeContext<Producer>(messageProducereRef, Producer.class, this.context);
	messageProducer = ProducerFactory.provides(producerContext);

   }

   @Override
   public MailMessage createMessage() {
	return new MailMessageBean();
   }

   @Override
   public void send(final MailMessage message) throws MailDispatcherException {
	try {
	   messageProducer.send(new JavaBeanMessage(message));
	} catch (org.kaleidofoundry.messaging.MessagingException me) {
	   throw new MailDispatcherException("mail.service.send.error", me);
	}
   }

}
